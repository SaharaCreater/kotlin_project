const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 5000;
const JWT_SECRET = process.env.JWT_SECRET;
if (!JWT_SECRET) { console.error('FATAL: JWT_SECRET is not set'); process.exit(1); }
const DB_PATH = path.join(__dirname, 'data', 'db.json');

// ===== Simple JSON "SQLite-like" DB =====
let DB = { users: [], progress: [], sessions: [] };

function loadDB() {
  try {
    if (fs.existsSync(DB_PATH)) {
      DB = JSON.parse(fs.readFileSync(DB_PATH, 'utf8'));
      DB.users = DB.users || [];
      DB.progress = DB.progress || [];
      DB.sessions = DB.sessions || [];
    }
  } catch (e) { console.error('DB load error:', e.message); }
}

function saveDB() {
  try {
    fs.mkdirSync(path.dirname(DB_PATH), { recursive: true });
    fs.writeFileSync(DB_PATH, JSON.stringify(DB, null, 2));
  } catch (e) { console.error('DB save error:', e.message); }
}

loadDB();

// ===== Middleware =====
app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'web')));

function authMiddleware(req, res, next) {
  const auth = req.headers.authorization;
  if (!auth || !auth.startsWith('Bearer ')) return res.status(401).json({ error: 'Unauthorized' });
  try {
    req.user = jwt.verify(auth.slice(7), JWT_SECRET);
    next();
  } catch { res.status(401).json({ error: 'Invalid token' }); }
}

// ===== AUTH ROUTES =====

app.post('/api/auth/register', async (req, res) => {
  try {
    const { email, name, password } = req.body;
    if (!email || !name || !password) return res.status(400).json({ error: 'All fields required' });
    if (DB.users.find(u => u.email === email)) return res.status(409).json({ error: 'Email already registered' });
    
    const hash = await bcrypt.hash(password, 10);
    const colors = ['#6750A4','#B5264C','#006A6B','#7D5700','#3B6939'];
    const user = {
      id: uuidv4(),
      email,
      name,
      password_hash: hash,
      avatar_color: colors[Math.floor(Math.random() * colors.length)],
      created_at: Date.now()
    };
    DB.users.push(user);
    saveDB();
    
    const token = jwt.sign({ id: user.id, email: user.email, name: user.name }, JWT_SECRET, { expiresIn: '7d' });
    res.json({ token, user: { id: user.id, email: user.email, name: user.name, avatar_color: user.avatar_color } });
  } catch (e) { res.status(500).json({ error: e.message }); }
});

app.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    const user = DB.users.find(u => u.email === email);
    if (!user) return res.status(401).json({ error: 'Invalid email or password' });
    
    const valid = await bcrypt.compare(password, user.password_hash);
    if (!valid) return res.status(401).json({ error: 'Invalid email or password' });
    
    const token = jwt.sign({ id: user.id, email: user.email, name: user.name }, JWT_SECRET, { expiresIn: '7d' });
    res.json({ token, user: { id: user.id, email: user.email, name: user.name, avatar_color: user.avatar_color } });
  } catch (e) { res.status(500).json({ error: e.message }); }
});

app.get('/api/auth/me', authMiddleware, (req, res) => {
  const user = DB.users.find(u => u.id === req.user.id);
  if (!user) return res.status(404).json({ error: 'User not found' });
  res.json({ id: user.id, email: user.email, name: user.name, avatar_color: user.avatar_color, created_at: user.created_at });
});

app.put('/api/auth/profile', authMiddleware, async (req, res) => {
  const { name } = req.body;
  const user = DB.users.find(u => u.id === req.user.id);
  if (!user) return res.status(404).json({ error: 'Not found' });
  if (name) user.name = name;
  user.updated_at = Date.now();
  saveDB();
  res.json({ id: user.id, email: user.email, name: user.name, avatar_color: user.avatar_color });
});

// ===== EXPERIMENTS ROUTES =====

const EXPERIMENTS = [
  { id: 'PENDULUM', name: 'Маятник', description: 'Колебания простого маятника с настраиваемой длиной и начальным углом', category: 'MECHANICS', icon: 'swap_vert', difficulty: 'easy' },
  { id: 'FREE_FALL', name: 'Свободное падение', description: 'Падение тел под действием гравитации с разной начальной высотой', category: 'MECHANICS', icon: 'arrow_downward', difficulty: 'easy' },
  { id: 'COLLISION', name: 'Столкновение шаров', description: 'Упругое и неупругое столкновение двух тел', category: 'MECHANICS', icon: 'compress', difficulty: 'medium' },
  { id: 'ELECTRIC_CIRCUIT', name: 'Электрическая цепь', description: 'Последовательное и параллельное соединение резисторов', category: 'ELECTRICITY', icon: 'cable', difficulty: 'medium' },
  { id: 'MAGNETIC_FIELD', name: 'Магнитное поле', description: 'Визуализация магнитного поля проводника с током', category: 'ELECTRICITY', icon: 'radar', difficulty: 'medium' },
  { id: 'LIGHT_REFRACTION', name: 'Преломление света', description: 'Преломление света на границе двух сред по закону Снеллиуса', category: 'OPTICS', icon: 'flare', difficulty: 'medium' },
  { id: 'LENS', name: 'Линзы', description: 'Фокусировка света собирающей и рассеивающей линзами', category: 'OPTICS', icon: 'center_focus_weak', difficulty: 'hard' },
  { id: 'BROWNIAN_MOTION', name: 'Броуновское движение', description: 'Хаотическое движение частиц в жидкости', category: 'THERMODYNAMICS', icon: 'grain', difficulty: 'easy' },
  { id: 'GAS_EXPANSION', name: 'Расширение газа', description: 'Изменение объёма газа при нагревании по закону Шарля', category: 'THERMODYNAMICS', icon: 'air', difficulty: 'medium' },
];

app.get('/api/experiments', (req, res) => {
  const { category } = req.query;
  let list = EXPERIMENTS;
  if (category) list = list.filter(e => e.category === category);
  res.json(list);
});

app.get('/api/experiments/:id', (req, res) => {
  const exp = EXPERIMENTS.find(e => e.id === req.params.id);
  if (!exp) return res.status(404).json({ error: 'Not found' });
  res.json(exp);
});

// ===== PROGRESS ROUTES =====

app.get('/api/progress', authMiddleware, (req, res) => {
  const progress = DB.progress.filter(p => p.user_id === req.user.id);
  res.json(progress);
});

app.post('/api/progress/:expId', authMiddleware, (req, res) => {
  const { expId } = req.params;
  let prog = DB.progress.find(p => p.user_id === req.user.id && p.experiment_id === expId);
  if (!prog) {
    prog = { id: uuidv4(), user_id: req.user.id, experiment_id: expId, run_count: 0, completed: false };
    DB.progress.push(prog);
  }
  prog.run_count = (prog.run_count || 0) + 1;
  prog.last_run_at = Date.now();
  prog.completed = true;
  saveDB();
  res.json(prog);
});

// ===== CATEGORIES ROUTE =====
app.get('/api/categories', (req, res) => {
  res.json([
    { id: 'MECHANICS', name: 'Механика', description: 'Движение тел, силы, энергия', color: '#2196F3', count: 3 },
    { id: 'ELECTRICITY', name: 'Электричество', description: 'Цепи, ток, магнетизм', color: '#FF9800', count: 2 },
    { id: 'OPTICS', name: 'Оптика', description: 'Свет, преломление, линзы', color: '#9C27B0', count: 2 },
    { id: 'THERMODYNAMICS', name: 'Термодинамика', description: 'Тепло, газы, молекулы', color: '#F44336', count: 2 },
  ]);
});

// ===== SPA fallback =====
app.use((req, res) => {
  if (req.path.startsWith('/api')) return res.status(404).json({ error: 'Not found' });
  res.sendFile(path.join(__dirname, 'web', 'index.html'));
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`DoPP Physics AR server running on port ${PORT}`);
});
