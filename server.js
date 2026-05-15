try { require('dotenv').config(); } catch (_) {}

const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 5000;
const JWT_SECRET = process.env.JWT_SECRET || 'dopp-physics-secret-2024';
const DB_PATH = path.join(__dirname, 'data', 'db.json');

let DB = { users: [], progress: [] };

function loadDB() {
  try {
    if (fs.existsSync(DB_PATH)) {
      const raw = JSON.parse(fs.readFileSync(DB_PATH, 'utf8'));
      DB.users    = raw.users    || [];
      DB.progress = raw.progress || [];
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

app.use(cors());
app.use(express.json());

// ── Auth middleware ──────────────────────────────────────────────────────────
function auth(req, res, next) {
  const header = req.headers.authorization;
  if (!header?.startsWith('Bearer ')) return res.status(401).json({ error: 'Unauthorized' });
  try {
    req.user = jwt.verify(header.slice(7), JWT_SECRET);
    next();
  } catch {
    res.status(401).json({ error: 'Invalid token' });
  }
}

// ── POST /api/auth/register ──────────────────────────────────────────────────
app.post('/api/auth/register', async (req, res) => {
  const { email, name, password } = req.body || {};
  if (!email || !name || !password)
    return res.status(400).json({ error: 'All fields required' });
  if (DB.users.find(u => u.email === email))
    return res.status(409).json({ error: 'Email already registered' });

  const hash = await bcrypt.hash(password, 10);
  const colors = ['#6750A4','#B5264C','#006A6B','#7D5700','#3B6939'];
  const user = {
    id: uuidv4(), email, name, password_hash: hash,
    avatar_color: colors[Math.floor(Math.random() * colors.length)],
    created_at: Date.now()
  };
  DB.users.push(user);
  saveDB();

  const token = jwt.sign({ id: user.id, email, name }, JWT_SECRET, { expiresIn: '7d' });
  res.json({ token, user: { id: user.id, email, name, avatar_color: user.avatar_color } });
});

// ── POST /api/auth/login ─────────────────────────────────────────────────────
app.post('/api/auth/login', async (req, res) => {
  const { email, password } = req.body || {};
  const user = DB.users.find(u => u.email === email);
  if (!user) return res.status(401).json({ error: 'Invalid email or password' });

  const valid = await bcrypt.compare(password, user.password_hash);
  if (!valid) return res.status(401).json({ error: 'Invalid email or password' });

  const token = jwt.sign({ id: user.id, email: user.email, name: user.name }, JWT_SECRET, { expiresIn: '7d' });
  res.json({ token, user: { id: user.id, email: user.email, name: user.name, avatar_color: user.avatar_color } });
});

// ── GET /api/auth/me ─────────────────────────────────────────────────────────
app.get('/api/auth/me', auth, (req, res) => {
  const user = DB.users.find(u => u.id === req.user.id);
  if (!user) return res.status(404).json({ error: 'User not found' });
  res.json({ id: user.id, email: user.email, name: user.name, avatar_color: user.avatar_color, created_at: user.created_at });
});

// ── PUT /api/auth/profile ────────────────────────────────────────────────────
app.put('/api/auth/profile', auth, (req, res) => {
  const user = DB.users.find(u => u.id === req.user.id);
  if (!user) return res.status(404).json({ error: 'Not found' });
  if (req.body?.name) user.name = req.body.name;
  user.updated_at = Date.now();
  saveDB();
  res.json({ id: user.id, email: user.email, name: user.name, avatar_color: user.avatar_color });
});

// ── Experiments (static list) ────────────────────────────────────────────────
const EXPERIMENTS = [
  { id: 'PENDULUM',        name: 'Маятник',                category: 'MECHANICS',      difficulty: 'easy',   description: 'Колебания простого маятника' },
  { id: 'FREE_FALL',       name: 'Свободное падение',       category: 'MECHANICS',      difficulty: 'easy',   description: 'Падение тел под действием гравитации' },
  { id: 'COLLISION',       name: 'Столкновение шаров',      category: 'MECHANICS',      difficulty: 'medium', description: 'Упругое и неупругое столкновение' },
  { id: 'ELECTRIC_CIRCUIT',name: 'Электрическая цепь',      category: 'ELECTRICITY',    difficulty: 'medium', description: 'Последовательное и параллельное соединение резисторов' },
  { id: 'MAGNETIC_FIELD',  name: 'Магнитное поле',          category: 'ELECTRICITY',    difficulty: 'medium', description: 'Визуализация магнитного поля проводника' },
  { id: 'LIGHT_REFRACTION',name: 'Преломление света',       category: 'OPTICS',         difficulty: 'medium', description: 'Преломление света на границе двух сред' },
  { id: 'LENS',            name: 'Линзы',                   category: 'OPTICS',         difficulty: 'hard',   description: 'Фокусировка света линзами' },
  { id: 'BROWNIAN_MOTION', name: 'Броуновское движение',    category: 'THERMODYNAMICS', difficulty: 'easy',   description: 'Хаотическое движение частиц в жидкости' },
  { id: 'GAS_EXPANSION',   name: 'Расширение газа',         category: 'THERMODYNAMICS', difficulty: 'medium', description: 'Изменение объёма газа при нагревании' },
];

app.get('/api/experiments', (req, res) => {
  const { category } = req.query;
  res.json(category ? EXPERIMENTS.filter(e => e.category === category) : EXPERIMENTS);
});

app.get('/api/experiments/:id', (req, res) => {
  const exp = EXPERIMENTS.find(e => e.id === req.params.id);
  exp ? res.json(exp) : res.status(404).json({ error: 'Not found' });
});

// ── Progress ─────────────────────────────────────────────────────────────────
app.get('/api/progress', auth, (req, res) => {
  res.json(DB.progress.filter(p => p.user_id === req.user.id));
});

app.post('/api/progress/:expId', auth, (req, res) => {
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

// ── Categories ───────────────────────────────────────────────────────────────
app.get('/api/categories', (req, res) => {
  res.json([
    { id: 'MECHANICS',      name: 'Механика',       description: 'Движение тел, силы, энергия', color: '#2196F3', count: 3 },
    { id: 'ELECTRICITY',    name: 'Электричество',  description: 'Цепи, ток, магнетизм',        color: '#FF9800', count: 2 },
    { id: 'OPTICS',         name: 'Оптика',         description: 'Свет, преломление, линзы',    color: '#9C27B0', count: 2 },
    { id: 'THERMODYNAMICS', name: 'Термодинамика',  description: 'Тепло, газы, молекулы',       color: '#F44336', count: 2 },
  ]);
});

// ── Health check ─────────────────────────────────────────────────────────────
app.get('/api/health', (req, res) => {
  res.json({ ok: true, users: DB.users.length, progress: DB.progress.length });
});

// ── 404 fallback ─────────────────────────────────────────────────────────────
app.use((req, res) => res.status(404).json({ error: 'Not found' }));

app.listen(PORT, '0.0.0.0', () => {
  console.log(`DoPP Physics AR server running on port ${PORT}`);
});
