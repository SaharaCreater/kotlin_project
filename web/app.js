// ============== DATA ==============

const EXPERIMENTS = [
  { id: 'PENDULUM',       name: 'Маятник',              desc: 'Колебания простого маятника с настраиваемой длиной и начальным углом',         cat: 'MECHANICS' },
  { id: 'FREE_FALL',      name: 'Свободное падение',    desc: 'Падение тел под действием гравитации с разной начальной высотой',               cat: 'MECHANICS' },
  { id: 'COLLISION',      name: 'Столкновение шаров',   desc: 'Упругое и неупругое столкновение двух тел',                                     cat: 'MECHANICS' },
  { id: 'ELECTRIC_CIRCUIT',name:'Электрическая цепь',   desc: 'Последовательное и параллельное соединение резисторов',                         cat: 'ELECTRICITY' },
  { id: 'MAGNETIC_FIELD', name: 'Магнитное поле',       desc: 'Визуализация магнитного поля проводника с током',                               cat: 'ELECTRICITY' },
  { id: 'LIGHT_REFRACTION',name:'Преломление света',    desc: 'Преломление света на границе двух сред',                                         cat: 'OPTICS' },
  { id: 'LENS',           name: 'Линзы',                desc: 'Фокусировка света собирающей и рассеивающей линзами',                            cat: 'OPTICS' },
  { id: 'BROWNIAN_MOTION',name: 'Броуновское движение', desc: 'Хаотическое движение частиц в жидкости',                                         cat: 'THERMODYNAMICS' },
  { id: 'GAS_EXPANSION',  name: 'Расширение газа',      desc: 'Изменение объёма газа при нагревании',                                           cat: 'THERMODYNAMICS' },
];

const CATEGORIES = [
  { id: 'MECHANICS',      name: 'Механика',       desc: 'Движение тел, силы, энергия', color: '#2196F3' },
  { id: 'ELECTRICITY',    name: 'Электричество',  desc: 'Цепи, ток, магнетизм',        color: '#FFC107' },
  { id: 'OPTICS',         name: 'Оптика',          desc: 'Свет, преломление, линзы',    color: '#9C27B0' },
  { id: 'THERMODYNAMICS', name: 'Термодинамика',  desc: 'Тепло, газы, молекулы',       color: '#FF5722' },
];

const CAT_ICONS = {
  MECHANICS: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 15.5A3.5 3.5 0 018.5 12 3.5 3.5 0 0112 8.5a3.5 3.5 0 013.5 3.5 3.5 3.5 0 01-3.5 3.5m7.43-2.92c.04-.36.07-.73.07-1.08s-.03-.73-.07-1.08l2.38-1.85c.21-.16.27-.44.14-.67l-2.25-3.9c-.13-.23-.41-.3-.64-.21l-2.8 1.12c-.58-.45-1.21-.83-1.9-1.12l-.42-2.98A.53.53 0 0014 2h-4a.53.53 0 00-.53.45l-.42 2.98c-.69.29-1.32.67-1.9 1.12L4.35 5.43a.5.5 0 00-.64.21L1.46 9.54c-.13.23-.07.51.14.67l2.38 1.85c-.04.35-.07.72-.07 1.08s.03.73.07 1.08L1.6 16.07a.5.5 0 00-.14.67l2.25 3.9c.13.23.41.3.64.21l2.8-1.12c.58.45 1.21.83 1.9 1.12l.42 2.98c.07.26.29.45.53.45h4c.24 0 .46-.19.53-.45l.42-2.98c.69-.29 1.32-.67 1.9-1.12l2.8 1.12c.23.09.51.02.64-.21l2.25-3.9c.13-.23.07-.51-.14-.67l-2.38-1.85z"/></svg>`,
  ELECTRICITY: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M7 2v11h3v9l7-12h-4l4-8z"/></svg>`,
  OPTICS: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 21c0 .55.45 1 1 1h4c.55 0 1-.45 1-1v-1H9v1zm3-19C8.14 2 5 5.14 5 9c0 2.38 1.19 4.47 3 5.74V17c0 .55.45 1 1 1h6c.55 0 1-.45 1-1v-2.26c1.81-1.27 3-3.36 3-5.74 0-3.86-3.14-7-7-7z"/></svg>`,
  THERMODYNAMICS: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M13.5.67s.74 2.65.74 4.8c0 2.06-1.35 3.73-3.41 3.73-2.07 0-3.63-1.67-3.63-3.73l.03-.36C5.21 7.51 4 10.62 4 14c0 4.42 3.58 8 8 8s8-3.58 8-8C20 8.61 17.41 3.8 13.5.67z"/></svg>`,
};

const EXP_ICONS = {
  PENDULUM: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C10.34 2 9 3.34 9 5c0 1.31.84 2.41 2 2.83V20.5a1 1 0 002 0V7.83c1.16-.42 2-1.52 2-2.83 0-1.66-1.34-3-3-3z"/></svg>`,
  FREE_FALL: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M20 12l-1.41-1.41L13 16.17V4h-2v12.17l-5.58-5.59L4 12l8 8 8-8z"/></svg>`,
  COLLISION: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M19 3H5C3.9 3 3 3.9 3 5v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z"/></svg>`,
  ELECTRIC_CIRCUIT: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M4 11h16v2H4zm0-4h16v2H4zm0 8h16v2H4z"/></svg>`,
  MAGNETIC_FIELD: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/></svg>`,
  LIGHT_REFRACTION: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 4L2.5 19h19L12 4zm0 3.84L18.32 17H5.68L12 7.84z"/></svg>`,
  LENS: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 9c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3zm0-7C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93V18c0-.55-.45-1-1-1H9c-1.66 0-3-1.34-3-3v-2c0-.55-.45-1-1-1H3.07C3.56 6.19 7.35 3 12 3s8.44 3.19 8.93 7H19c-.55 0-1 .45-1 1v2c0 1.66-1.34 3-3 3h-1c-.55 0-1 .45-1 1v1.93z"/></svg>`,
  BROWNIAN_MOTION: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 14.5v-9l6 4.5-6 4.5z"/></svg>`,
  GAS_EXPANSION: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm4.3 14.3L11 12V7h1.5v4.4l4.8 2.85-.0 1.05z"/></svg>`,
};

// ============== STATE ==============

let currentFilter = null;
let currentExp = null;
let simState = null;
let simParams = {};
let isRunning = false;
let animFrame = null;
let showInfoPanel = true;
let paramsExpanded = false;
let canvas, ctx;

const defaultParams = {
  PENDULUM:        { length: 1.0, initialAngle: 0.5, mass: 1.0, damping: 0.02 },
  FREE_FALL:       { initialHeight: 10, initialVelocity: 0, mass: 1.0, showTrail: true },
  COLLISION:       { mass1: 2, mass2: 1, velocity1: 5, velocity2: -3, isElastic: true },
  ELECTRIC_CIRCUIT:{ voltage: 12, resistance1: 100, resistance2: 200, resistance3: 150, isParallel: false },
  MAGNETIC_FIELD:  { current: 5, wireLength: 1, showFieldLines: true, numberOfLines: 8 },
  LIGHT_REFRACTION:{ incidentAngle: 0.5, n1: 1.0, n2: 1.5, showNormal: true },
  LENS:            { focalLength: 0.5, objectDistance: 1.0, objectHeight: 0.3, isConverging: true },
  BROWNIAN_MOTION: { temperature: 300, particleRadius: 1e-6, viscosity: 0.001, numberOfParticles: 20 },
  GAS_EXPANSION:   { initialTemperature: 300, finalTemperature: 450, initialVolume: 1, moles: 1, pressure: 101325 },
};

// ============== NAVIGATION ==============

function showMainScreen() {
  stopSim();
  document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
  document.getElementById('screen-main').classList.add('active');
}

function showExperimentsScreen(filter) {
  stopSim();
  currentFilter = filter || null;
  document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
  document.getElementById('screen-experiments').classList.add('active');
  renderExperimentsScreen();
}

function showSimScreen(expId) {
  document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
  document.getElementById('screen-sim').classList.add('active');
  currentExp = EXPERIMENTS.find(e => e.id === expId);
  document.getElementById('sim-exp-title').textContent = currentExp.name;
  simParams = { ...defaultParams[expId] };
  simState = null;
  isRunning = false;
  paramsExpanded = false;
  showInfoPanel = true;

  canvas = document.getElementById('sim-canvas');
  ctx = canvas.getContext('2d');
  resizeCanvas();

  renderParamsPanel();
  updatePlayBtn();
  updateInfoPanel();
  updateInfoToggleBtn();
  document.getElementById('params-content').classList.remove('expanded');
  document.getElementById('params-toggle-btn').classList.remove('expanded');

  document.querySelector('.start-hint') && document.querySelector('.start-hint').remove();
  const hint = document.createElement('div');
  hint.className = 'start-hint';
  hint.id = 'start-hint';
  hint.innerHTML = `<svg viewBox="0 0 24 24" fill="currentColor" width="32" height="32"><path d="M15 16.54V7.46L8.54 12 15 16.54zM12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z"/></svg><div class="start-hint-main">Нажмите кнопку Старт или экран</div><div class="start-hint-sub">для запуска симуляции</div>`;
  document.getElementById('screen-sim').appendChild(hint);

  canvas.onclick = () => { if (!simState) startSim(); };
  renderFrame();
}

// ============== EXPERIMENTS LIST ==============

function renderExperimentsScreen() {
  const chipsEl = document.getElementById('filter-chips');
  const listEl = document.getElementById('experiments-list');

  chipsEl.innerHTML = '';
  const allChip = mkChip('Все', currentFilter === null, () => { currentFilter = null; renderExperimentsScreen(); });
  chipsEl.appendChild(allChip);
  CATEGORIES.forEach(cat => {
    const c = mkChip(cat.name, currentFilter === cat.id, () => {
      currentFilter = currentFilter === cat.id ? null : cat.id;
      renderExperimentsScreen();
    }, cat.color);
    chipsEl.appendChild(c);
  });

  listEl.innerHTML = '';
  const filtered = currentFilter ? EXPERIMENTS.filter(e => e.cat === currentFilter) : EXPERIMENTS;

  if (currentFilter) {
    filtered.forEach(exp => listEl.appendChild(mkExpCard(exp)));
  } else {
    CATEGORIES.forEach(cat => {
      const exps = filtered.filter(e => e.cat === cat.id);
      if (!exps.length) return;
      listEl.appendChild(mkCatHeader(cat));
      exps.forEach(exp => listEl.appendChild(mkExpCard(exp)));
    });
  }
}

function mkChip(label, active, onClick, color) {
  const el = document.createElement('button');
  el.className = 'chip' + (active ? ' active' : '');
  el.textContent = label;
  if (color && active) el.style.background = color + '22';
  el.onclick = onClick;
  return el;
}

function mkCatHeader(cat) {
  const el = document.createElement('div');
  el.className = 'cat-header';
  el.innerHTML = `
    <div class="cat-header-icon" style="background:${cat.color}22;color:${cat.color}">${CAT_ICONS[cat.id]}</div>
    <div><div class="cat-header-name">${cat.name}</div><div class="cat-header-desc">${cat.desc}</div></div>`;
  return el;
}

function mkExpCard(exp) {
  const cat = CATEGORIES.find(c => c.id === exp.cat);
  const el = document.createElement('div');
  el.className = 'exp-card';
  el.innerHTML = `
    <div class="exp-icon" style="background:linear-gradient(135deg,${cat.color}CC,${cat.color}88)">${EXP_ICONS[exp.id]}</div>
    <div class="exp-info"><div class="exp-name">${exp.name}</div><div class="exp-desc">${exp.desc}</div></div>
    <div class="exp-arrow"><svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20"><path d="M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"/></svg></div>`;
  el.onclick = () => showSimScreen(exp.id);
  return el;
}

// ============== SIMULATION CONTROL ==============

function startSim() {
  const hint = document.getElementById('start-hint');
  if (hint) hint.remove();
  const id = currentExp.id;
  switch (id) {
    case 'PENDULUM':        simState = SimEngine.initPendulum(simParams); break;
    case 'FREE_FALL':       simState = SimEngine.initFreeFall(simParams); break;
    case 'COLLISION':       simState = SimEngine.initCollision(simParams); break;
    case 'ELECTRIC_CIRCUIT':simState = SimEngine.initCircuit(simParams); break;
    case 'MAGNETIC_FIELD':  simState = SimEngine.initMagField(simParams); break;
    case 'LIGHT_REFRACTION':simState = SimEngine.initRefraction(simParams); break;
    case 'LENS':            simState = SimEngine.initLens(simParams); break;
    case 'BROWNIAN_MOTION': simState = SimEngine.initBrownian(simParams); break;
    case 'GAS_EXPANSION':   simState = SimEngine.initGas(simParams); break;
  }
  isRunning = true;
  updatePlayBtn();
  loop();
}

function stopSim() {
  isRunning = false;
  if (animFrame) { cancelAnimationFrame(animFrame); animFrame = null; }
}

function togglePlay() {
  if (!simState) { startSim(); return; }
  isRunning = !isRunning;
  updatePlayBtn();
  if (isRunning) loop();
}

function resetSim() {
  stopSim();
  startSim();
}

function updatePlayBtn() {
  const btn = document.getElementById('btn-play');
  if (!btn) return;
  btn.innerHTML = isRunning
    ? `<svg viewBox="0 0 24 24" fill="currentColor" width="22" height="22"><path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z"/></svg>`
    : `<svg viewBox="0 0 24 24" fill="currentColor" width="22" height="22"><path d="M8 5v14l11-7z"/></svg>`;
}

function loop() {
  if (!isRunning) return;
  const id = currentExp.id;
  switch (id) {
    case 'PENDULUM':        simState = SimEngine.updatePendulum(simParams, simState); break;
    case 'FREE_FALL':
      simState = SimEngine.updateFreeFall(simParams, simState);
      if (simState.hasLanded) { isRunning = false; updatePlayBtn(); }
      break;
    case 'COLLISION':       simState = SimEngine.updateCollision(simParams, simState); break;
    case 'ELECTRIC_CIRCUIT':simState = SimEngine.updateCircuit(simParams, simState); break;
    case 'MAGNETIC_FIELD':  simState = SimEngine.updateMagField(simParams, simState); break;
    case 'LIGHT_REFRACTION':simState = SimEngine.updateRefraction(simParams, simState); break;
    case 'LENS':            simState = SimEngine.updateLens(simParams, simState); break;
    case 'BROWNIAN_MOTION': simState = SimEngine.updateBrownian(simParams, simState); break;
    case 'GAS_EXPANSION':   simState = SimEngine.updateGas(simParams, simState); break;
  }
  renderFrame();
  updateInfoPanel();
  animFrame = requestAnimationFrame(loop);
}

function renderFrame() {
  if (!canvas || !ctx) return;
  resizeCanvas();
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  if (!simState) { drawBackground(); return; }
  drawBackground();
  switch (currentExp.id) {
    case 'PENDULUM':        drawPendulum(); break;
    case 'FREE_FALL':       drawFreeFall(); break;
    case 'COLLISION':       drawCollision(); break;
    case 'ELECTRIC_CIRCUIT':drawCircuit(); break;
    case 'MAGNETIC_FIELD':  drawMagnetic(); break;
    case 'LIGHT_REFRACTION':drawRefraction(); break;
    case 'LENS':            drawLens(); break;
    case 'BROWNIAN_MOTION': drawBrownian(); break;
    case 'GAS_EXPANSION':   drawGas(); break;
  }
}

function resizeCanvas() {
  const wrap = canvas.parentElement;
  canvas.width = wrap.clientWidth;
  canvas.height = wrap.clientHeight;
}

function drawBackground() {
  const w = canvas.width, h = canvas.height;
  const grad = ctx.createLinearGradient(0, 0, 0, h);
  grad.addColorStop(0, '#0a0a2e');
  grad.addColorStop(1, '#1a0a2e');
  ctx.fillStyle = grad;
  ctx.fillRect(0, 0, w, h);
  // grid dots
  ctx.fillStyle = 'rgba(255,255,255,0.04)';
  for (let x = 40; x < w; x += 40)
    for (let y = 40; y < h; y += 40) {
      ctx.beginPath(); ctx.arc(x, y, 1.5, 0, Math.PI * 2); ctx.fill();
    }
}

// ============== DRAW PENDULUM ==============
function drawPendulum() {
  const w = canvas.width, h = canvas.height;
  const cx = w / 2, cy = h * 0.15;
  const scale = Math.min(w, h) * 0.35;
  const { angle, length } = { angle: simState.angle, length: simParams.length };
  const bx = cx + Math.sin(angle) * scale;
  const by = cy + Math.cos(angle) * scale;

  // pivot
  ctx.fillStyle = '#8a8aff';
  ctx.beginPath(); ctx.arc(cx, cy, 8, 0, Math.PI * 2); ctx.fill();

  // string
  ctx.strokeStyle = '#aaaaff88';
  ctx.lineWidth = 2;
  ctx.beginPath(); ctx.moveTo(cx, cy); ctx.lineTo(bx, by); ctx.stroke();

  // energy bar
  const totalE = simState.ke + simState.pe;
  if (totalE > 0) {
    const barW = 80, barH = 8, bx0 = 20, by0 = h - 80;
    ctx.fillStyle = 'rgba(255,255,255,0.1)';
    ctx.beginPath(); ctx.roundRect(bx0, by0, barW, barH, 4); ctx.fill();
    const peRatio = simState.pe / totalE;
    const keRatio = simState.ke / totalE;
    ctx.fillStyle = '#64B5F6';
    ctx.beginPath(); ctx.roundRect(bx0, by0, barW * peRatio, barH, 4); ctx.fill();
    ctx.fillStyle = '#81C784';
    ctx.beginPath(); ctx.roundRect(bx0 + barW * peRatio, by0, barW * keRatio, barH, 4); ctx.fill();
    ctx.fillStyle = 'rgba(255,255,255,0.7)';
    ctx.font = '10px sans-serif';
    ctx.fillText('PE', bx0, by0 - 4);
    ctx.fillText('KE', bx0 + 45, by0 - 4);
  }

  // bob shadow
  ctx.fillStyle = 'rgba(103,80,164,0.3)';
  ctx.beginPath(); ctx.ellipse(bx, h * 0.85, 18, 6, 0, 0, Math.PI * 2); ctx.fill();

  // bob
  const bobR = 18 + simParams.mass * 3;
  const grd = ctx.createRadialGradient(bx - bobR * 0.3, by - bobR * 0.3, 2, bx, by, bobR);
  grd.addColorStop(0, '#C5A4FF');
  grd.addColorStop(1, '#6750A4');
  ctx.fillStyle = grd;
  ctx.beginPath(); ctx.arc(bx, by, bobR, 0, Math.PI * 2); ctx.fill();

  ctx.strokeStyle = 'rgba(255,255,255,0.2)';
  ctx.lineWidth = 1.5;
  ctx.stroke();

  // angle arc
  if (Math.abs(angle) > 0.02) {
    ctx.strokeStyle = 'rgba(255,200,100,0.5)';
    ctx.lineWidth = 1.5;
    ctx.setLineDash([4, 4]);
    ctx.beginPath();
    ctx.arc(cx, cy, scale * 0.4, Math.PI / 2, Math.PI / 2 + angle, angle < 0);
    ctx.stroke();
    ctx.setLineDash([]);
  }
}

// ============== DRAW FREE FALL ==============
function drawFreeFall() {
  const w = canvas.width, h = canvas.height;
  const groundY = h * 0.85;
  const maxH = simParams.initialHeight;
  const normH = simState.height / maxH;
  const objY = groundY - normH * (groundY - h * 0.1);
  const cx = w / 2;

  // height ruler
  ctx.strokeStyle = 'rgba(255,255,255,0.15)';
  ctx.lineWidth = 1;
  ctx.setLineDash([4, 4]);
  ctx.beginPath(); ctx.moveTo(cx + 60, h * 0.1); ctx.lineTo(cx + 60, groundY); ctx.stroke();
  ctx.setLineDash([]);

  // tick marks
  ctx.fillStyle = 'rgba(255,255,255,0.4)';
  ctx.font = '10px sans-serif';
  ctx.textAlign = 'left';
  for (let i = 0; i <= 4; i++) {
    const ty = groundY - (i / 4) * (groundY - h * 0.1);
    ctx.fillStyle = 'rgba(255,255,255,0.2)';
    ctx.fillRect(cx + 54, ty, 12, 1);
    ctx.fillStyle = 'rgba(255,255,255,0.5)';
    ctx.fillText(`${Math.round(maxH * i / 4)}м`, cx + 70, ty + 4);
  }

  // trail
  if (simParams.showTrail && simState.time > 0) {
    const steps = 20;
    for (let i = 0; i < steps; i++) {
      const frac = i / steps;
      const pt = simState.time * frac;
      const trailH = Math.max(0, PhysicsCalc.freeFallPos(maxH, simParams.initialVelocity, pt));
      const ty = groundY - (trailH / maxH) * (groundY - h * 0.1);
      const alpha = frac * 0.4;
      ctx.fillStyle = `rgba(100,181,246,${alpha})`;
      ctx.beginPath(); ctx.arc(cx, ty, 5, 0, Math.PI * 2); ctx.fill();
    }
  }

  // ground
  ctx.fillStyle = 'rgba(100,200,100,0.3)';
  ctx.fillRect(cx - 80, groundY, 160, 6);
  ctx.fillStyle = '#81C784';
  ctx.beginPath(); ctx.roundRect(cx - 80, groundY, 160, 3, 2); ctx.fill();

  // velocity vector
  if (isRunning && Math.abs(simState.vel) > 0.5) {
    const arrowLen = Math.min(60, Math.abs(simState.vel) * 3);
    ctx.strokeStyle = '#FFB74D';
    ctx.lineWidth = 2.5;
    ctx.beginPath(); ctx.moveTo(cx + 40, objY); ctx.lineTo(cx + 40, objY + arrowLen); ctx.stroke();
    ctx.fillStyle = '#FFB74D';
    ctx.beginPath();
    ctx.moveTo(cx + 40, objY + arrowLen + 8);
    ctx.lineTo(cx + 36, objY + arrowLen);
    ctx.lineTo(cx + 44, objY + arrowLen);
    ctx.closePath(); ctx.fill();
  }

  // ball
  if (!simState.hasLanded) {
    const grd = ctx.createRadialGradient(cx - 8, objY - 8, 2, cx, objY, 22);
    grd.addColorStop(0, '#90CAF9');
    grd.addColorStop(1, '#1976D2');
    ctx.fillStyle = grd;
    ctx.beginPath(); ctx.arc(cx, objY, 22, 0, Math.PI * 2); ctx.fill();
    ctx.strokeStyle = 'rgba(255,255,255,0.3)';
    ctx.lineWidth = 1.5; ctx.stroke();
  } else {
    // Landed splat
    ctx.fillStyle = '#42A5F5';
    ctx.beginPath(); ctx.ellipse(cx, groundY, 30, 8, 0, 0, Math.PI * 2); ctx.fill();
    ctx.fillStyle = 'rgba(255,255,255,0.8)';
    ctx.font = 'bold 14px sans-serif'; ctx.textAlign = 'center';
    ctx.fillText('Приземление!', cx, groundY - 20);
  }
  ctx.textAlign = 'left';
}

// ============== DRAW COLLISION ==============
function drawCollision() {
  const w = canvas.width, h = canvas.height;
  const cy = h * 0.5;
  const scale = w * 0.1;
  const ballR1 = 12 + simParams.mass1 * 3;
  const ballR2 = 12 + simParams.mass2 * 3;

  const toX = p => w / 2 + p * scale;
  const x1 = toX(simState.pos1), x2 = toX(simState.pos2);

  // track
  ctx.strokeStyle = 'rgba(255,255,255,0.1)';
  ctx.lineWidth = 2;
  ctx.beginPath(); ctx.moveTo(20, cy + 30); ctx.lineTo(w - 20, cy + 30); ctx.stroke();

  // velocity vectors
  const drawArrow = (x, vel, color) => {
    if (Math.abs(vel) < 0.1) return;
    const len = Math.min(50, Math.abs(vel) * 8);
    const dir = vel > 0 ? 1 : -1;
    ctx.strokeStyle = color; ctx.lineWidth = 2;
    ctx.beginPath(); ctx.moveTo(x, cy - 40); ctx.lineTo(x + dir * len, cy - 40); ctx.stroke();
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.moveTo(x + dir * (len + 8), cy - 40);
    ctx.lineTo(x + dir * len, cy - 44);
    ctx.lineTo(x + dir * len, cy - 36);
    ctx.closePath(); ctx.fill();
    ctx.fillStyle = color; ctx.font = '11px sans-serif'; ctx.textAlign = 'center';
    ctx.fillText(`${Math.abs(vel).toFixed(1)}м/с`, x + dir * (len / 2), cy - 52);
  };
  drawArrow(x1, simState.vel1, '#64B5F6');
  drawArrow(x2, simState.vel2, '#EF9A9A');
  ctx.textAlign = 'left';

  // ball1
  let grd1 = ctx.createRadialGradient(x1 - ballR1 * 0.3, cy - ballR1 * 0.3, 2, x1, cy, ballR1);
  grd1.addColorStop(0, '#90CAF9'); grd1.addColorStop(1, '#1565C0');
  ctx.fillStyle = grd1;
  ctx.beginPath(); ctx.arc(x1, cy, ballR1, 0, Math.PI * 2); ctx.fill();
  ctx.strokeStyle = 'rgba(255,255,255,0.25)'; ctx.lineWidth = 1.5; ctx.stroke();
  ctx.fillStyle = '#fff'; ctx.font = 'bold 11px sans-serif'; ctx.textAlign = 'center';
  ctx.fillText(`${simParams.mass1}кг`, x1, cy + 4);

  // ball2
  let grd2 = ctx.createRadialGradient(x2 - ballR2 * 0.3, cy - ballR2 * 0.3, 2, x2, cy, ballR2);
  grd2.addColorStop(0, '#EF9A9A'); grd2.addColorStop(1, '#C62828');
  ctx.fillStyle = grd2;
  ctx.beginPath(); ctx.arc(x2, cy, ballR2, 0, Math.PI * 2); ctx.fill();
  ctx.strokeStyle = 'rgba(255,255,255,0.25)'; ctx.lineWidth = 1.5; ctx.stroke();
  ctx.fillStyle = '#fff'; ctx.font = 'bold 11px sans-serif'; ctx.textAlign = 'center';
  ctx.fillText(`${simParams.mass2}кг`, x2, cy + 4);
  ctx.textAlign = 'left';

  if (simState.hasCollided) {
    ctx.fillStyle = 'rgba(255,235,59,0.9)';
    ctx.font = 'bold 14px sans-serif'; ctx.textAlign = 'center';
    ctx.fillText(simParams.isElastic ? 'Упругое!' : 'Неупругое!', w / 2, cy - 80);
    ctx.textAlign = 'left';
  }
}

// ============== DRAW CIRCUIT ==============
function drawCircuit() {
  const w = canvas.width, h = canvas.height;
  const cx = w / 2, cy = h / 2;
  const bw = Math.min(w - 60, 320), bh = Math.min(h - 80, 200);
  const l = cx - bw / 2, r = cx + bw / 2, t = cy - bh / 2, b = cy + bh / 2;
  const { isParallel } = simParams;
  const { i1, i2, i3, u1, u2, u3, totalI, phase } = simState;

  const drawResistor = (x, y, label, Ival, Uval) => {
    const rw = 44, rh = 20;
    ctx.fillStyle = 'rgba(255,193,7,0.2)';
    ctx.strokeStyle = '#FFC107';
    ctx.lineWidth = 2;
    ctx.beginPath(); ctx.roundRect(x - rw/2, y - rh/2, rw, rh, 4); ctx.fill(); ctx.stroke();
    ctx.fillStyle = '#FFC107'; ctx.font = 'bold 11px sans-serif'; ctx.textAlign = 'center';
    ctx.fillText(label, x, y + 4);
    ctx.fillStyle = 'rgba(255,255,255,0.6)'; ctx.font = '10px sans-serif';
    ctx.fillText(`${Ival.toFixed(3)}А`, x, y + rh/2 + 14);
    ctx.fillText(`${Uval.toFixed(1)}В`, x, y + rh/2 + 26);
  };

  const drawBattery = (x, y) => {
    ctx.fillStyle = 'rgba(129,199,132,0.2)';
    ctx.strokeStyle = '#81C784';
    ctx.lineWidth = 2;
    ctx.beginPath(); ctx.roundRect(x - 20, y - 28, 40, 56, 6); ctx.fill(); ctx.stroke();
    ctx.fillStyle = '#fff'; ctx.font = 'bold 10px sans-serif'; ctx.textAlign = 'center';
    ctx.fillText(`${simParams.voltage}В`, x, y + 4);
    ctx.fillStyle = '#81C784'; ctx.font = '18px sans-serif';
    ctx.fillText('+', x, y - 10);
    ctx.fillText('−', x, y + 18);
  };

  // wires
  ctx.strokeStyle = '#4FC3F7'; ctx.lineWidth = 2.5;
  ctx.beginPath();
  ctx.moveTo(l, t); ctx.lineTo(r, t);
  ctx.moveTo(l, b); ctx.lineTo(r, b);
  ctx.moveTo(l, t); ctx.lineTo(l, b);
  ctx.moveTo(r, t); ctx.lineTo(r, b);
  ctx.stroke();

  if (isParallel) {
    // 3 parallel resistors in the middle
    const m1 = cy - bh * 0.28, m2 = cy, m3 = cy + bh * 0.28;
    [m1, m2, m3].forEach(my => {
      ctx.strokeStyle = '#4FC3F7'; ctx.lineWidth = 1.5;
      ctx.beginPath(); ctx.moveTo(cx - bw * 0.3, my); ctx.lineTo(cx + bw * 0.3, my); ctx.stroke();
    });
    // junctions
    [[cx - bw*0.3, m1],[cx - bw*0.3, m3],[cx + bw*0.3, m1],[cx + bw*0.3, m3]].forEach(([px,py]) => {
      ctx.strokeStyle = '#4FC3F7'; ctx.lineWidth = 1.5;
      ctx.beginPath(); ctx.moveTo(px, py); ctx.lineTo(px, t > m1 ? t : t); ctx.stroke();
    });
    drawResistor(cx, m1, `R₁=${simParams.resistance1}Ω`, i1, u1);
    drawResistor(cx, m2, `R₂=${simParams.resistance2}Ω`, i2, u2);
    drawResistor(cx, m3, `R₃=${simParams.resistance3}Ω`, i3, u3);
  } else {
    const gap = bw / 4;
    drawResistor(cx - gap, cy, `R₁=${simParams.resistance1}Ω`, i1, u1);
    drawResistor(cx, cy, `R₂=${simParams.resistance2}Ω`, i2, u2);
    drawResistor(cx + gap, cy, `R₃=${simParams.resistance3}Ω`, i3, u3);
    ctx.strokeStyle = '#4FC3F7'; ctx.lineWidth = 2.5;
    [[t, cy, l, cx - gap - 22],[t, cy, cx - gap + 22, cx - gap * 2 + 22, false]].forEach(() => {});
  }

  // Battery on left
  drawBattery(l, cy);

  // Electron flow animation
  const pts = [[l, t],[r, t],[r, b],[l, b]];
  const totalLen = bw * 2 + bh * 2;
  const pos = (phase / (2 * Math.PI)) * totalLen;
  for (let i = 0; i < 6; i++) {
    const p = (pos + i * totalLen / 6) % totalLen;
    let ex, ey;
    if (p < bw) { ex = l + p; ey = t; }
    else if (p < bw + bh) { ex = r; ey = t + p - bw; }
    else if (p < 2 * bw + bh) { ex = r - (p - bw - bh); ey = b; }
    else { ex = l; ey = b - (p - 2 * bw - bh); }
    ctx.fillStyle = 'rgba(79,195,247,0.9)';
    ctx.beginPath(); ctx.arc(ex, ey, 4, 0, Math.PI * 2); ctx.fill();
  }

  ctx.textAlign = 'left';
}

// ============== DRAW MAGNETIC ==============
function drawMagnetic() {
  const w = canvas.width, h = canvas.height;
  const cx = w / 2, cy = h / 2;
  const { current, numberOfLines, showFieldLines } = simParams;
  const { animPhase } = simState;

  // Wire
  ctx.fillStyle = '#FF8A65';
  ctx.beginPath(); ctx.roundRect(cx - 6, h * 0.1, 12, h * 0.8, 6); ctx.fill();
  ctx.strokeStyle = '#FFCCBC'; ctx.lineWidth = 1;
  ctx.stroke();

  // Current direction arrow
  ctx.fillStyle = '#fff'; ctx.font = '20px sans-serif'; ctx.textAlign = 'center';
  ctx.fillText('↑', cx, cy + 8);
  ctx.font = '10px sans-serif'; ctx.fillStyle = 'rgba(255,255,255,0.6)';
  ctx.fillText(`I = ${current.toFixed(1)}А`, cx + 24, cy + 4);
  ctx.textAlign = 'left';

  if (!showFieldLines) return;

  const maxRadius = Math.min(cx, cy) * 0.9;
  for (let i = 0; i < numberOfLines; i++) {
    const r = (maxRadius / numberOfLines) * (i + 1);
    const alpha = 0.6 - (i / numberOfLines) * 0.5;
    const hue = 200 + (current / 20) * 40;
    ctx.strokeStyle = `hsla(${hue},80%,70%,${alpha})`;
    ctx.lineWidth = 1.5;
    ctx.beginPath(); ctx.arc(cx, cy, r, 0, Math.PI * 2); ctx.stroke();

    // Field direction arrows on circles
    const numArrows = Math.max(4, Math.floor(i * 2 + 4));
    for (let j = 0; j < numArrows; j++) {
      const angle = (j / numArrows) * Math.PI * 2 + animPhase;
      const ax = cx + r * Math.cos(angle), ay = cy + r * Math.sin(angle);
      const tx = -Math.sin(angle), ty = Math.cos(angle);
      ctx.fillStyle = `hsla(${hue},80%,70%,${alpha + 0.2})`;
      ctx.save();
      ctx.translate(ax, ay);
      ctx.rotate(Math.atan2(ty, tx));
      ctx.beginPath();
      ctx.moveTo(5, 0); ctx.lineTo(-3, -3); ctx.lineTo(-3, 3);
      ctx.closePath(); ctx.fill();
      ctx.restore();
    }
  }

  // Field strength labels
  ctx.fillStyle = 'rgba(255,255,255,0.5)'; ctx.font = '10px sans-serif';
  const dists = [0.05, 0.1, 0.2];
  dists.forEach((d, i) => {
    const r2 = (maxRadius / numberOfLines) * (Math.floor(numberOfLines * d / 0.5 * 3) || 2);
    const B = PhysicsCalc.magneticB(current, d);
    ctx.fillText(`${(B * 1e6).toFixed(1)}μT`, cx + 8 + (maxRadius / numberOfLines) * (i * 2 + 1), cy + 4);
  });
}

// ============== DRAW REFRACTION ==============
function drawRefraction() {
  const w = canvas.width, h = canvas.height;
  const midY = h * 0.5;
  const cx = w * 0.5;
  const { incidentAngle, n1, n2, showNormal } = simParams;
  const { refAngle, isTIR, progress } = simState;

  // Media
  const grad1 = ctx.createLinearGradient(0, 0, 0, midY);
  grad1.addColorStop(0, `hsla(210,60%,20%,${n1 * 0.4})`);
  grad1.addColorStop(1, `hsla(210,60%,30%,${n1 * 0.5})`);
  ctx.fillStyle = grad1; ctx.fillRect(0, 0, w, midY);

  const grad2 = ctx.createLinearGradient(0, midY, 0, h);
  grad2.addColorStop(0, `hsla(270,60%,25%,${n2 * 0.5})`);
  grad2.addColorStop(1, `hsla(270,60%,15%,${n2 * 0.4})`);
  ctx.fillStyle = grad2; ctx.fillRect(0, midY, w, h - midY);

  // Interface
  ctx.strokeStyle = 'rgba(255,255,255,0.3)'; ctx.lineWidth = 2;
  ctx.setLineDash([6, 4]);
  ctx.beginPath(); ctx.moveTo(0, midY); ctx.lineTo(w, midY); ctx.stroke();
  ctx.setLineDash([]);

  // Labels
  ctx.fillStyle = 'rgba(255,255,255,0.5)'; ctx.font = '12px sans-serif'; ctx.textAlign = 'left';
  ctx.fillText(`Среда 1: n₁=${n1.toFixed(2)}`, 12, 24);
  ctx.fillText(`Среда 2: n₂=${n2.toFixed(2)}`, 12, midY + 24);

  // Normal
  if (showNormal) {
    ctx.strokeStyle = 'rgba(255,255,255,0.2)'; ctx.lineWidth = 1;
    ctx.setLineDash([4, 4]);
    ctx.beginPath(); ctx.moveTo(cx, midY - h * 0.3); ctx.lineTo(cx, midY + h * 0.3); ctx.stroke();
    ctx.setLineDash([]);
  }

  // Incident ray
  const rayLen = h * 0.35 * Math.min(1, progress * 2);
  const ix = cx - Math.sin(incidentAngle) * rayLen;
  const iy = midY - Math.cos(incidentAngle) * rayLen;
  ctx.strokeStyle = '#FFD54F'; ctx.lineWidth = 3;
  ctx.beginPath(); ctx.moveTo(ix, iy); ctx.lineTo(cx, midY); ctx.stroke();
  // arrowhead
  ctx.fillStyle = '#FFD54F';
  const aDx = cx - ix, aDy = midY - iy;
  const aLen = Math.sqrt(aDx*aDx + aDy*aDy);
  if (aLen > 0) {
    const nx2 = aDx / aLen, ny2 = aDy / aLen;
    const ax = cx, ay = midY;
    ctx.beginPath();
    ctx.moveTo(ax, ay);
    ctx.lineTo(ax - nx2*12 + ny2*5, ay - ny2*12 - nx2*5);
    ctx.lineTo(ax - nx2*12 - ny2*5, ay - ny2*12 + nx2*5);
    ctx.closePath(); ctx.fill();
  }

  if (isTIR && progress > 0.3) {
    // Total internal reflection
    const refX = cx + Math.sin(incidentAngle) * rayLen * Math.min(1, (progress - 0.3) * 2);
    const refY = midY - Math.cos(incidentAngle) * rayLen * Math.min(1, (progress - 0.3) * 2);
    ctx.strokeStyle = '#FF7043'; ctx.lineWidth = 2.5;
    ctx.beginPath(); ctx.moveTo(cx, midY); ctx.lineTo(refX, refY); ctx.stroke();
    ctx.fillStyle = '#FF7043'; ctx.font = 'bold 12px sans-serif'; ctx.textAlign = 'center';
    ctx.fillText('ПВО!', cx, midY - 20);
  } else if (!isTIR && refAngle !== null && progress > 0.5) {
    const refLen = h * 0.35 * Math.min(1, (progress - 0.5) * 2);
    const rfx = cx + Math.sin(refAngle) * refLen;
    const rfy = midY + Math.cos(refAngle) * refLen;
    ctx.strokeStyle = '#4FC3F7'; ctx.lineWidth = 2.5;
    ctx.beginPath(); ctx.moveTo(cx, midY); ctx.lineTo(rfx, rfy); ctx.stroke();
  }

  // Angle arcs
  ctx.strokeStyle = 'rgba(255,213,79,0.5)'; ctx.lineWidth = 1.5;
  ctx.beginPath();
  ctx.arc(cx, midY, 40, -Math.PI/2 - incidentAngle, -Math.PI/2);
  ctx.stroke();
  ctx.fillStyle = '#FFD54F'; ctx.font = '11px sans-serif'; ctx.textAlign = 'center';
  ctx.fillText(`θ₁=${(incidentAngle * 180 / Math.PI).toFixed(0)}°`, cx - 30, midY - 50);
  if (!isTIR && refAngle !== null) {
    ctx.strokeStyle = 'rgba(79,195,247,0.5)'; ctx.lineWidth = 1.5;
    ctx.beginPath();
    ctx.arc(cx, midY, 40, Math.PI/2, Math.PI/2 + refAngle);
    ctx.stroke();
    ctx.fillStyle = '#4FC3F7';
    ctx.fillText(`θ₂=${(refAngle * 180 / Math.PI).toFixed(0)}°`, cx + 30, midY + 55);
  }
  ctx.textAlign = 'left';
}

// ============== DRAW LENS ==============
function drawLens() {
  const w = canvas.width, h = canvas.height;
  const cx = w / 2, cy = h / 2;
  const scale = Math.min(w, h) * 0.3;
  const { focalLength, objectDistance, objectHeight, isConverging } = simParams;
  const { imageDist, imageH, mag, isVirtual, isInverted, progress } = simState;
  const unitPx = scale / 2;

  // Optical axis
  ctx.strokeStyle = 'rgba(255,255,255,0.2)'; ctx.lineWidth = 1;
  ctx.setLineDash([4, 4]);
  ctx.beginPath(); ctx.moveTo(0, cy); ctx.lineTo(w, cy); ctx.stroke();
  ctx.setLineDash([]);

  // Lens
  const lensH = Math.min(h * 0.4, 120);
  ctx.strokeStyle = isConverging ? '#4FC3F7' : '#CE93D8';
  ctx.lineWidth = 3;
  ctx.beginPath();
  if (isConverging) {
    ctx.moveTo(cx - 10, cy - lensH / 2);
    ctx.quadraticCurveTo(cx + 15, cy, cx - 10, cy + lensH / 2);
    ctx.moveTo(cx + 10, cy - lensH / 2);
    ctx.quadraticCurveTo(cx - 15, cy, cx + 10, cy + lensH / 2);
  } else {
    ctx.moveTo(cx + 10, cy - lensH / 2);
    ctx.quadraticCurveTo(cx - 15, cy, cx + 10, cy + lensH / 2);
    ctx.moveTo(cx - 10, cy - lensH / 2);
    ctx.quadraticCurveTo(cx + 15, cy, cx - 10, cy + lensH / 2);
  }
  ctx.stroke();
  ctx.strokeStyle = isConverging ? '#4FC3F7' : '#CE93D8';
  ctx.lineWidth = 1.5;
  ctx.beginPath();
  ctx.moveTo(cx, cy - lensH / 2 - 8); ctx.lineTo(cx, cy + lensH / 2 + 8);
  ctx.stroke();

  // Focal points
  const f = focalLength * unitPx;
  [-f, f].forEach(fx => {
    ctx.fillStyle = 'rgba(255,255,255,0.4)';
    ctx.beginPath(); ctx.arc(cx + fx, cy, 5, 0, Math.PI * 2); ctx.fill();
    ctx.fillStyle = 'rgba(255,255,255,0.4)'; ctx.font = '11px sans-serif'; ctx.textAlign = 'center';
    ctx.fillText('F', cx + fx, cy + 16);
  });

  // Object arrow
  const ox = cx - objectDistance * unitPx;
  const oh = objectHeight * unitPx;
  ctx.strokeStyle = '#81C784'; ctx.lineWidth = 2.5;
  ctx.beginPath(); ctx.moveTo(ox, cy); ctx.lineTo(ox, cy - oh); ctx.stroke();
  ctx.fillStyle = '#81C784';
  ctx.beginPath(); ctx.moveTo(ox, cy - oh); ctx.lineTo(ox - 6, cy - oh + 10); ctx.lineTo(ox + 6, cy - oh + 10); ctx.closePath(); ctx.fill();
  ctx.fillStyle = 'rgba(129,199,132,0.5)'; ctx.font = '11px sans-serif'; ctx.textAlign = 'center';
  ctx.fillText('Объект', ox, cy - oh - 10);

  // Rays and image
  if (progress > 0.3 && isFinite(imageDist)) {
    const ix = cx + imageDist * unitPx;
    const ih = imageH * unitPx * (isInverted ? 1 : -1);
    const rayAlpha = Math.min(1, (progress - 0.3) * 2);

    // Ray 1: parallel to axis → through focal point
    ctx.strokeStyle = `rgba(255,213,79,${rayAlpha})`; ctx.lineWidth = 1.5;
    ctx.setLineDash([3, 3]);
    ctx.beginPath(); ctx.moveTo(ox, cy - oh); ctx.lineTo(cx, cy - oh); ctx.stroke();
    ctx.setLineDash([]);
    ctx.beginPath();
    if (!isVirtual) { ctx.moveTo(cx, cy - oh); ctx.lineTo(ix, cy + ih); }
    else { ctx.moveTo(cx, cy - oh); ctx.lineTo(w, cy - oh + (cy + ih - (cy - oh)) / (ix - cx) * (w - cx)); }
    ctx.stroke();

    // Ray 2: through center
    ctx.strokeStyle = `rgba(255,138,101,${rayAlpha})`; ctx.lineWidth = 1.5;
    ctx.beginPath(); ctx.moveTo(ox, cy - oh); ctx.lineTo(cx, cy);
    if (!isVirtual) { ctx.lineTo(ix, cy + ih); }
    else { ctx.lineTo(w, cy + (cy - (cy-oh)) / (cx - ox) * (w - ox)); }
    ctx.stroke();

    if (!isVirtual) {
      // Image arrow
      ctx.strokeStyle = '#EF9A9A'; ctx.lineWidth = 2;
      ctx.beginPath(); ctx.moveTo(ix, cy); ctx.lineTo(ix, cy + ih); ctx.stroke();
      ctx.fillStyle = '#EF9A9A';
      ctx.beginPath();
      ctx.moveTo(ix, cy + ih);
      ctx.lineTo(ix - 5, cy + ih - Math.sign(ih) * 8);
      ctx.lineTo(ix + 5, cy + ih - Math.sign(ih) * 8);
      ctx.closePath(); ctx.fill();
      ctx.fillStyle = 'rgba(239,154,154,0.5)'; ctx.font = '11px sans-serif'; ctx.textAlign = 'center';
      ctx.fillText('Изображение', ix, cy + ih + Math.sign(ih) * 18);
    }
  }
  ctx.textAlign = 'left';
}

// ============== DRAW BROWNIAN ==============
function drawBrownian() {
  const w = canvas.width, h = canvas.height;
  const cx = w / 2, cy = h / 2;
  const scale = Math.min(w, h) * 0.45;
  const { positions, trails, time } = simState;
  const { temperature } = simParams;
  const hue = Math.max(0, Math.min(240, 240 - (temperature - 200) / 300 * 240));

  // Container
  ctx.strokeStyle = `hsl(${hue},60%,50%)`; ctx.lineWidth = 2;
  ctx.strokeRect(cx - scale, cy - scale, scale * 2, scale * 2);
  ctx.fillStyle = `hsla(${hue},60%,30%,0.1)`;
  ctx.fillRect(cx - scale, cy - scale, scale * 2, scale * 2);

  // Temp label
  ctx.fillStyle = `hsl(${hue},80%,70%)`; ctx.font = '12px sans-serif'; ctx.textAlign = 'center';
  ctx.fillText(`T = ${temperature.toFixed(0)} K  (${(temperature - 273).toFixed(0)}°C)`, cx, cy - scale - 10);
  ctx.textAlign = 'left';

  // Trails
  trails.forEach((trail, i) => {
    if (trail.length < 2) return;
    ctx.strokeStyle = `hsla(${hue},70%,60%,0.2)`;
    ctx.lineWidth = 1;
    ctx.beginPath();
    trail.forEach(([px, py], j) => {
      const sx = cx + px * scale, sy = cy + py * scale;
      j === 0 ? ctx.moveTo(sx, sy) : ctx.lineTo(sx, sy);
    });
    ctx.stroke();
  });

  // Particles
  positions.forEach(([px, py]) => {
    const sx = cx + px * scale, sy = cy + py * scale;
    const r = 5 + (temperature - 200) / 300 * 4;
    const grd = ctx.createRadialGradient(sx - r * 0.3, sy - r * 0.3, 1, sx, sy, r);
    grd.addColorStop(0, `hsl(${hue + 40},90%,80%)`);
    grd.addColorStop(1, `hsl(${hue},70%,50%)`);
    ctx.fillStyle = grd;
    ctx.beginPath(); ctx.arc(sx, sy, r, 0, Math.PI * 2); ctx.fill();
  });
}

// ============== DRAW GAS ==============
function drawGas() {
  const w = canvas.width, h = canvas.height;
  const cx = w / 2, cy = h / 2;
  const { temp, vol, mols, speeds } = simState;
  const containerSize = Math.sqrt(vol / simParams.initialVolume);
  const scale = Math.min(w, h) * 0.38;
  const cs = containerSize * scale;

  const hue = Math.max(0, Math.min(60, (temp - 200) / 400 * 60));
  const color = `hsl(${hue},80%,60%)`;

  // Container walls (animated expansion)
  ctx.strokeStyle = color; ctx.lineWidth = 2;
  ctx.strokeRect(cx - cs, cy - cs, cs * 2, cs * 2);
  ctx.fillStyle = `hsla(${hue},60%,30%,0.08)`;
  ctx.fillRect(cx - cs, cy - cs, cs * 2, cs * 2);

  // Molecules
  const avgSpeed = speeds.length ? speeds[0] : 0;
  mols.forEach(([mx, my]) => {
    const sx = cx + mx * scale, sy = cy + my * scale;
    if (sx < cx - cs || sx > cx + cs || sy < cy - cs || sy > cy + cs) return;
    const r = 4;
    const grd = ctx.createRadialGradient(sx - 1, sy - 1, 0.5, sx, sy, r);
    grd.addColorStop(0, `hsl(${hue + 30},90%,80%)`);
    grd.addColorStop(1, color);
    ctx.fillStyle = grd;
    ctx.beginPath(); ctx.arc(sx, sy, r, 0, Math.PI * 2); ctx.fill();
  });

  // Labels
  const progress = Math.min(1, simState.time / 5);
  ctx.fillStyle = color; ctx.font = '12px sans-serif'; ctx.textAlign = 'center';
  ctx.fillText(`T = ${temp.toFixed(0)} K`, cx, cy - cs - 28);
  ctx.fillText(`V = ${vol.toFixed(2)} V₀`, cx, cy - cs - 14);
  ctx.fillText(`Нагрев: ${(progress * 100).toFixed(0)}%`, cx, cy + cs + 20);

  // Progress bar
  ctx.fillStyle = 'rgba(255,255,255,0.1)';
  ctx.beginPath(); ctx.roundRect(cx - 60, cy + cs + 28, 120, 6, 3); ctx.fill();
  ctx.fillStyle = color;
  ctx.beginPath(); ctx.roundRect(cx - 60, cy + cs + 28, 120 * progress, 6, 3); ctx.fill();

  ctx.textAlign = 'left';
}

// ============== INFO PANEL ==============
function toggleInfo() {
  showInfoPanel = !showInfoPanel;
  updateInfoToggleBtn();
  updateInfoPanel();
}

function updateInfoToggleBtn() {
  const btn = document.getElementById('btn-info-toggle');
  if (!btn) return;
  if (showInfoPanel) btn.classList.add('active');
  else btn.classList.remove('active');
}

function updateInfoPanel() {
  const panel = document.getElementById('info-panel');
  if (!panel) return;
  if (!showInfoPanel || !simState) { panel.classList.remove('visible'); return; }
  panel.classList.add('visible');
  const content = document.getElementById('info-panel-content');
  content.innerHTML = buildInfoHTML();
}

function infoRow(label, value) {
  return `<div class="info-row"><span class="info-row-label">${label}</span><span class="info-row-value">${value}</span></div>`;
}
function infoLabel(text) { return `<div class="info-section-label">${text}</div>`; }

function buildInfoHTML() {
  if (!simState || !currentExp) return '';
  const s = simState, p = simParams;
  switch (currentExp.id) {
    case 'PENDULUM': return [
      infoRow('Время', `${s.time.toFixed(2)} с`),
      infoRow('Угол', `${(s.angle * 180 / Math.PI).toFixed(1)}°`),
      infoRow('Угл. скорость', `${s.angVel.toFixed(2)} рад/с`),
      infoRow('Период', `${s.period.toFixed(2)} с`),
      infoLabel('Энергия'),
      infoRow('Кинетическая', `${s.ke.toFixed(3)} Дж`),
      infoRow('Потенциальная', `${s.pe.toFixed(3)} Дж`),
      infoRow('Полная', `${(s.ke + s.pe).toFixed(3)} Дж`),
    ].join('');

    case 'FREE_FALL': return [
      infoRow('Время', `${s.time.toFixed(2)} с`),
      infoRow('Высота', `${s.height.toFixed(2)} м`),
      infoRow('Скорость', `${Math.abs(s.vel).toFixed(2)} м/с`),
      infoRow('Макс. скорость', `${s.maxVel.toFixed(2)} м/с`),
      infoRow('Теор. время падения', `${PhysicsCalc.fallTime(p.initialHeight).toFixed(2)} с`),
      s.hasLanded ? `<div class="info-chip-success">Приземление!</div>` : '',
    ].join('');

    case 'COLLISION': return [
      infoRow('Время', `${s.time.toFixed(2)} с`),
      infoLabel('Шар 1'),
      infoRow('Позиция', `${s.pos1.toFixed(2)} м`),
      infoRow('Скорость', `${s.vel1.toFixed(2)} м/с`),
      infoLabel('Шар 2'),
      infoRow('Позиция', `${s.pos2.toFixed(2)} м`),
      infoRow('Скорость', `${s.vel2.toFixed(2)} м/с`),
      infoLabel('Законы сохранения'),
      infoRow('Импульс до', `${s.momBefore.toFixed(2)} кг·м/с`),
      infoRow('Импульс после', `${s.momAfter.toFixed(2)} кг·м/с`),
      infoRow('Энергия до', `${s.enBefore.toFixed(2)} Дж`),
      infoRow('Энергия после', `${s.enAfter.toFixed(2)} Дж`),
      s.hasCollided ? `<div class="info-chip">${p.isElastic ? 'Упругое' : 'Неупругое'}</div>` : '',
    ].join('');

    case 'ELECTRIC_CIRCUIT': return [
      infoRow('Общее R', `${s.totalR.toFixed(1)} Ω`),
      infoRow('Общий ток', `${s.totalI.toFixed(3)} А`),
      infoRow('Мощность', `${s.power.toFixed(2)} Вт`),
      infoLabel('Напряжения'),
      infoRow('U₁', `${s.u1.toFixed(2)} В`),
      infoRow('U₂', `${s.u2.toFixed(2)} В`),
      infoRow('U₃', `${s.u3.toFixed(2)} В`),
      infoLabel('Токи'),
      infoRow('I₁', `${s.i1.toFixed(3)} А`),
      infoRow('I₂', `${s.i2.toFixed(3)} А`),
      infoRow('I₃', `${s.i3.toFixed(3)} А`),
    ].join('');

    case 'MAGNETIC_FIELD': return [
      infoRow('Время', `${s.time.toFixed(2)} с`),
      infoLabel('Напряжённость B'),
      ...Object.entries(s.fieldMap).map(([d, B]) =>
        infoRow(`r = ${(+d * 100).toFixed(0)} см`, `${(B * 1e6).toFixed(2)} мкТл`)
      ),
    ].join('');

    case 'LIGHT_REFRACTION': return [
      infoRow('Угол падения', `${(p.incidentAngle * 180 / Math.PI).toFixed(1)}°`),
      infoRow('n₁', p.n1.toFixed(2)),
      infoRow('n₂', p.n2.toFixed(2)),
      s.isTIR
        ? `<div class="info-chip-alert">Полное внутреннее отражение</div>` +
          (s.critAngle ? infoRow('Критический угол', `${(s.critAngle * 180 / Math.PI).toFixed(1)}°`) : '')
        : (s.refAngle !== null ? infoRow('Угол преломления', `${(s.refAngle * 180 / Math.PI).toFixed(1)}°`) : ''),
    ].join('');

    case 'LENS': {
      if (!isFinite(s.imageDist)) return infoRow('Изображение', 'на бесконечности');
      return [
        infoRow('Фокусное расст.', `${p.focalLength.toFixed(2)} м`),
        infoRow('Расст. до объекта', `${p.objectDistance.toFixed(2)} м`),
        infoRow('Расст. до изобр.', `${s.imageDist.toFixed(2)} м`),
        infoRow('Высота изобр.', `${s.imageH.toFixed(2)} м`),
        infoRow('Увеличение', `${Math.abs(s.mag).toFixed(2)}×`),
        `<div class="info-chip">${s.isVirtual ? 'Мнимое' : 'Действительное'}</div>`,
        `<div class="info-chip">${s.isInverted ? 'Перевёрнутое' : 'Прямое'}</div>`,
      ].join('');
    }

    case 'BROWNIAN_MOTION': return [
      infoRow('Время', `${s.time.toFixed(2)} с`),
      infoRow('Число частиц', `${s.positions.length}`),
    ].join('');

    case 'GAS_EXPANSION': return [
      infoRow('Время', `${s.time.toFixed(2)} с`),
      infoRow('Температура', `${s.temp.toFixed(0)} K (${(s.temp - 273).toFixed(0)}°C)`),
      infoRow('Объём', `${s.vol.toFixed(2)} V₀`),
      infoRow('Давление', `${s.pressure.toFixed(0)} Па`),
      s.speeds.length ? infoRow('Ср. скорость молекул', `${s.speeds[0].toFixed(0)} м/с`) : '',
    ].join('');

    default: return '';
  }
}

// ============== PARAMS PANEL ==============
function toggleParams() {
  paramsExpanded = !paramsExpanded;
  const content = document.getElementById('params-content');
  const btn = document.getElementById('params-toggle-btn');
  if (paramsExpanded) {
    content.classList.add('expanded');
    btn.classList.add('expanded');
  } else {
    content.classList.remove('expanded');
    btn.classList.remove('expanded');
  }
}

function renderParamsPanel() {
  const content = document.getElementById('params-content');
  content.innerHTML = buildParamsHTML();
}

function buildParamsHTML() {
  if (!currentExp) return '';
  const p = simParams;
  switch (currentExp.id) {
    case 'PENDULUM': return `
      ${slider('length', 'Длина (м)', p.length, 0.5, 3.0, 0.1, v => `${(+v).toFixed(1)} м`)}
      ${slider('initialAngle', 'Начальный угол', p.initialAngle, 0.1, 1.2, 0.01, v => `${(+v * 180 / Math.PI).toFixed(0)}°`)}
      ${slider('mass', 'Масса (кг)', p.mass, 0.1, 10.0, 0.1, v => `${(+v).toFixed(1)} кг`)}
      ${slider('damping', 'Затухание', p.damping, 0, 0.1, 0.001, v => `${(+v).toFixed(3)}`)}
    `;
    case 'FREE_FALL': return `
      ${slider('initialHeight', 'Начальная высота (м)', p.initialHeight, 1, 100, 1, v => `${(+v).toFixed(0)} м`)}
      ${slider('initialVelocity', 'Начальная скорость (м/с)', p.initialVelocity, -20, 20, 0.5, v => `${(+v).toFixed(1)} м/с`)}
      ${slider('mass', 'Масса (кг)', p.mass, 0.1, 100, 0.5, v => `${(+v).toFixed(1)} кг`)}
      ${toggle('showTrail', 'Показать след', p.showTrail)}
    `;
    case 'COLLISION': return `
      <div class="param-section-label">Шар 1</div>
      ${slider('mass1', 'Масса 1 (кг)', p.mass1, 0.1, 10, 0.1, v => `${(+v).toFixed(1)} кг`)}
      ${slider('velocity1', 'Скорость 1 (м/с)', p.velocity1, -10, 10, 0.5, v => `${(+v).toFixed(1)} м/с`)}
      <div class="param-section-label">Шар 2</div>
      ${slider('mass2', 'Масса 2 (кг)', p.mass2, 0.1, 10, 0.1, v => `${(+v).toFixed(1)} кг`)}
      ${slider('velocity2', 'Скорость 2 (м/с)', p.velocity2, -10, 10, 0.5, v => `${(+v).toFixed(1)} м/с`)}
      ${toggle('isElastic', 'Упругое столкновение', p.isElastic)}
    `;
    case 'ELECTRIC_CIRCUIT': return `
      ${slider('voltage', 'Напряжение (В)', p.voltage, 1, 24, 1, v => `${(+v).toFixed(0)} В`)}
      ${slider('resistance1', 'R₁ (Ом)', p.resistance1, 10, 1000, 10, v => `${(+v).toFixed(0)} Ω`)}
      ${slider('resistance2', 'R₂ (Ом)', p.resistance2, 10, 1000, 10, v => `${(+v).toFixed(0)} Ω`)}
      ${slider('resistance3', 'R₃ (Ом)', p.resistance3, 10, 1000, 10, v => `${(+v).toFixed(0)} Ω`)}
      ${toggle('isParallel', 'Параллельное соединение', p.isParallel)}
    `;
    case 'MAGNETIC_FIELD': return `
      ${slider('current', 'Сила тока (А)', p.current, 0.1, 20, 0.1, v => `${(+v).toFixed(1)} А`)}
      ${slider('wireLength', 'Длина провода (м)', p.wireLength, 0.1, 2, 0.1, v => `${(+v).toFixed(1)} м`)}
      ${slider('numberOfLines', 'Число линий поля', p.numberOfLines, 4, 16, 1, v => `${Math.round(+v)}`)}
      ${toggle('showFieldLines', 'Показать линии поля', p.showFieldLines)}
    `;
    case 'LIGHT_REFRACTION': return `
      ${slider('incidentAngle', 'Угол падения', p.incidentAngle, 0, 1.5, 0.01, v => `${(+v * 180 / Math.PI).toFixed(0)}°`)}
      ${slider('n1', 'n₁ (среда 1)', p.n1, 1.0, 2.5, 0.01, v => `${(+v).toFixed(2)}`)}
      ${slider('n2', 'n₂ (среда 2)', p.n2, 1.0, 2.5, 0.01, v => `${(+v).toFixed(2)}`)}
      ${toggle('showNormal', 'Показать нормаль', p.showNormal)}
      <div class="material-chips">
        <button class="material-chip${p.n1 === 1.0 ? ' active' : ''}" onclick="setN1(1.0)">Воздух</button>
        <button class="material-chip${p.n2 === 1.33 ? ' active' : ''}" onclick="setN2(1.33)">Вода</button>
        <button class="material-chip${p.n2 === 1.5 ? ' active' : ''}" onclick="setN2(1.5)">Стекло</button>
        <button class="material-chip${p.n2 === 2.42 ? ' active' : ''}" onclick="setN2(2.42)">Алмаз</button>
      </div>
    `;
    case 'LENS': return `
      ${slider('focalLength', 'Фокусное расстояние (м)', p.focalLength, 0.1, 2.0, 0.05, v => `${(+v).toFixed(2)} м`)}
      ${slider('objectDistance', 'Расст. до объекта (м)', p.objectDistance, 0.2, 5.0, 0.1, v => `${(+v).toFixed(2)} м`)}
      ${slider('objectHeight', 'Высота объекта (м)', p.objectHeight, 0.1, 1.0, 0.05, v => `${(+v).toFixed(2)} м`)}
      ${toggle('isConverging', 'Собирающая линза', p.isConverging)}
    `;
    case 'BROWNIAN_MOTION': return `
      ${slider('temperature', 'Температура (K)', p.temperature, 200, 500, 5, v => `${(+v).toFixed(0)} K (${((+v)-273).toFixed(0)}°C)`)}
      ${slider('viscosity', 'Вязкость (мПа·с)', p.viscosity * 1000, 0.5, 5, 0.1, v => { simParams.viscosity = +v / 1000; return `${(+v).toFixed(2)} мПа·с`; }, true)}
      ${slider('numberOfParticles', 'Число частиц', p.numberOfParticles, 5, 50, 1, v => `${Math.round(+v)}`)}
    `;
    case 'GAS_EXPANSION': return `
      ${slider('initialTemperature', 'Нач. температура (K)', p.initialTemperature, 200, 400, 5, v => `${(+v).toFixed(0)} K`)}
      ${slider('finalTemperature', 'Кон. температура (K)', p.finalTemperature, 300, 600, 5, v => `${(+v).toFixed(0)} K`)}
      ${slider('moles', 'Количество вещества (моль)', p.moles, 0.1, 5, 0.1, v => `${(+v).toFixed(1)} моль`)}
    `;
    default: return '';
  }
}

function slider(key, label, val, min, max, step, fmt, skipKey) {
  const id = `sl_${key}`;
  const displayVal = fmt(val);
  return `
    <div class="param-group">
      <div class="param-label">
        <span class="param-label-text">${label}</span>
        <span class="param-label-value" id="${id}_v">${displayVal}</span>
      </div>
      <input class="param-slider" type="range" id="${id}" min="${min}" max="${max}" step="${step}" value="${val}"
        oninput="onSlider('${key}','${id}',this.value,${JSON.stringify(fmt.toString())})"/>
    </div>`;
}

function toggle(key, label, val) {
  const id = `tg_${key}`;
  return `
    <div class="param-toggle">
      <span class="param-toggle-label">${label}</span>
      <label class="toggle-switch">
        <input type="checkbox" id="${id}" ${val ? 'checked' : ''} onchange="onToggle('${key}', this.checked)"/>
        <span class="toggle-slider"></span>
      </label>
    </div>`;
}

function onSlider(key, id, val, fmtStr) {
  const fmt = new Function('v', 'return ' + fmtStr.replace(/^.*?=>/, '').trim() + ';').bind(null);
  // Handle special viscosity case
  if (key === 'viscosity') {
    simParams.viscosity = +val / 1000;
  } else if (key === 'numberOfParticles' || key === 'numberOfLines') {
    simParams[key] = Math.round(+val);
  } else {
    simParams[key] = +val;
  }
  try {
    const display = fmt(val);
    document.getElementById(id + '_v').textContent = display;
  } catch(e) {}

  // Live update simulation if running for static experiments
  const liveUpdate = ['ELECTRIC_CIRCUIT', 'MAGNETIC_FIELD', 'LIGHT_REFRACTION', 'LENS'];
  if (simState && liveUpdate.includes(currentExp.id)) {
    // reinit static experiments live
    switch(currentExp.id) {
      case 'ELECTRIC_CIRCUIT': simState = { ...simState, ...SimEngine.initCircuit(simParams), isRunning: simState.isRunning }; break;
      case 'MAGNETIC_FIELD':   simState = { ...simState, ...SimEngine.initMagField(simParams), isRunning: simState.isRunning }; break;
      case 'LIGHT_REFRACTION': simState = { ...simState, ...SimEngine.initRefraction(simParams), isRunning: simState.isRunning, time: 0, progress: 0 }; break;
      case 'LENS':             simState = { ...simState, ...SimEngine.initLens(simParams), isRunning: simState.isRunning, time: 0, progress: 0 }; break;
    }
  }
}

function onToggle(key, val) {
  simParams[key] = val;
  if (simState && currentExp.id === 'ELECTRIC_CIRCUIT') {
    simState = { ...simState, ...SimEngine.initCircuit(simParams), isRunning: simState.isRunning };
  }
}

function setN1(v) { simParams.n1 = v; renderParamsPanel(); if (simState) { simState = { ...simState, ...SimEngine.initRefraction(simParams), isRunning: simState.isRunning, time: 0, progress: 0 }; } }
function setN2(v) { simParams.n2 = v; renderParamsPanel(); if (simState) { simState = { ...simState, ...SimEngine.initRefraction(simParams), isRunning: simState.isRunning, time: 0, progress: 0 }; } }

// ============== INIT ==============
window.addEventListener('resize', () => { if (canvas) resizeCanvas(); });
