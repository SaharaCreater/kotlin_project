// SimEngine — simulation state machines (pure functions, no rendering)
const SimEngine = {

  // ===== PENDULUM =====
  initPendulum({ length, initialAngle, mass, damping }) {
    return {
      theta: initialAngle,
      omega: 0,
      length, mass, damping,
      time: 0,
      trail: [],
    };
  },
  updatePendulum({ length, damping }, s) {
    const { theta, omega } = Physics.pendulumStep(s.theta, s.omega, length, damping);
    const trail = [...(s.trail || []), { theta: s.theta }].slice(-120);
    return { ...s, theta, omega, time: s.time + Physics.DT, trail };
  },

  // ===== FREE FALL =====
  initFreeFall({ initialHeight, initialVelocity }) {
    return {
      y: initialHeight,
      vy: initialVelocity || 0,
      initialHeight,
      time: 0,
      trail: [],
      hasLanded: false,
    };
  },
  updateFreeFall(p, s) {
    if (s.hasLanded) return s;
    const { y, vy } = Physics.freeFallStep(s.y, s.vy);
    const trail = [...s.trail, { y: s.y }].slice(-200);
    if (y <= 0) return { ...s, y: 0, vy: 0, trail, hasLanded: true, time: s.time + Physics.DT };
    return { ...s, y, vy, trail, time: s.time + Physics.DT };
  },

  // ===== COLLISION =====
  initCollision({ mass1, mass2, velocity1, velocity2, isElastic }) {
    return {
      x1: 0.2, x2: 0.75, v1: velocity1, v2: velocity2,
      mass1, mass2, isElastic,
      hasCollided: false, time: 0,
      ke: Physics.kineticEnergy(mass1, velocity1) + Physics.kineticEnergy(mass2, velocity2),
    };
  },
  updateCollision(p, s) {
    const dt = Physics.DT;
    let { x1, x2, v1, v2, mass1, mass2, isElastic, hasCollided } = s;
    const r1 = 0.06, r2 = 0.06;
    x1 += v1 * dt;
    x2 += v2 * dt;
    // Wall bounce
    if (x1 - r1 <= 0) { x1 = r1; v1 = -v1 * 0.98; }
    if (x2 + r2 >= 1) { x2 = 1 - r2; v2 = -v2 * 0.98; }
    // Collision detection
    if (!hasCollided && Math.abs(x2 - x1) <= r1 + r2 && v1 > v2) {
      const res = Physics.collisionVelocities(mass1, mass2, v1, v2, isElastic);
      v1 = res.v1f; v2 = res.v2f;
      hasCollided = true;
    }
    const ke = Physics.kineticEnergy(mass1, v1) + Physics.kineticEnergy(mass2, v2);
    return { ...s, x1, x2, v1, v2, hasCollided, time: s.time + dt, ke };
  },

  // ===== ELECTRIC CIRCUIT =====
  initCircuit({ voltage, resistance1, resistance2, resistance3, isParallel }) {
    const rs = [resistance1, resistance2, resistance3];
    const Rtotal = isParallel ? Physics.parallelResistance(rs) : Physics.seriesResistance(rs);
    const I = voltage / Rtotal;
    return { voltage, resistance1, resistance2, resistance3, isParallel, Rtotal, I, power: Physics.power(voltage, I), time: 0, phase: 0 };
  },
  updateCircuit(p, s) {
    return { ...s, time: s.time + Physics.DT, phase: (s.phase + 0.05) % (2 * Math.PI) };
  },

  // ===== MAGNETIC FIELD =====
  initMagField({ current, showFieldLines, numberOfLines }) {
    return { current, showFieldLines, numberOfLines, time: 0, phase: 0 };
  },
  updateMagField(p, s) {
    return { ...s, time: s.time + Physics.DT, phase: (s.phase + 0.03) % (2 * Math.PI) };
  },

  // ===== LIGHT REFRACTION =====
  initRefraction({ incidentAngle, n1, n2, showNormal }) {
    const refracted = Physics.snell(incidentAngle, n1, n2);
    const critical = Physics.criticalAngle(n1, n2);
    return { incidentAngle, n1, n2, showNormal, refractedAngle: refracted, criticalAngle: critical, time: 0 };
  },
  updateRefraction(p, s) {
    const refracted = Physics.snell(p.incidentAngle, p.n1, p.n2);
    return { ...s, incidentAngle: p.incidentAngle, n1: p.n1, n2: p.n2, refractedAngle: refracted, time: s.time + Physics.DT };
  },

  // ===== LENS =====
  initLens({ focalLength, objectDistance, objectHeight, isConverging }) {
    const f = isConverging ? focalLength : -focalLength;
    const img = Physics.lensImage(f, objectDistance);
    return { focalLength: f, objectDistance, objectHeight, isConverging, ...img, time: 0 };
  },
  updateLens(p, s) {
    const f = p.isConverging ? p.focalLength : -p.focalLength;
    const img = Physics.lensImage(f, p.objectDistance);
    return { ...s, focalLength: f, objectDistance: p.objectDistance, objectHeight: p.objectHeight, ...img, time: s.time + Physics.DT };
  },

  // ===== BROWNIAN MOTION =====
  initBrownian({ temperature, particleRadius, viscosity, numberOfParticles }) {
    const particles = Array.from({ length: numberOfParticles }, (_, i) => ({
      id: i,
      x: 0.1 + Math.random() * 0.8,
      y: 0.1 + Math.random() * 0.8,
      trail: [],
    }));
    return { temperature, particleRadius, viscosity, numberOfParticles, particles, time: 0 };
  },
  updateBrownian(p, s) {
    const kT = Physics.BOLTZMANN * s.temperature;
    const particles = s.particles.map(pt => {
      const { x, y } = Physics.brownianStep(pt.x, pt.y, kT, s.particleRadius, s.viscosity, Physics.DT * 1e10);
      const nx = Math.max(0.02, Math.min(0.98, x));
      const ny = Math.max(0.02, Math.min(0.98, y));
      const trail = [...(pt.trail || []), { x: pt.x, y: pt.y }].slice(-30);
      return { ...pt, x: nx, y: ny, trail };
    });
    return { ...s, particles, time: s.time + Physics.DT };
  },

  // ===== GAS EXPANSION =====
  initGas({ initialTemperature, finalTemperature, initialVolume, moles, pressure }) {
    return {
      T: initialTemperature, T0: initialTemperature, Tf: finalTemperature,
      V: initialVolume, V0: initialVolume,
      n: moles, P: pressure, time: 0, progress: 0,
      particles: Array.from({ length: 40 }, () => ({
        x: 0.5 + (Math.random() - 0.5) * 0.4,
        y: 0.5 + (Math.random() - 0.5) * 0.4,
        vx: (Math.random() - 0.5) * 0.5,
        vy: (Math.random() - 0.5) * 0.5,
      })),
    };
  },
  updateGas(p, s) {
    const progress = Math.min(1, s.progress + 0.003);
    const T = s.T0 + (s.Tf - s.T0) * progress;
    const Vf = (s.n * Physics.R_GAS * s.Tf) / s.P;
    const V = s.V0 + (Vf - s.V0) * progress;
    const speed = 0.5 + (T / s.T0) * 1.5;
    const particles = s.particles.map(pt => {
      let { x, y, vx, vy } = pt;
      // Normalize velocity to match temperature speed
      const mag = Math.sqrt(vx * vx + vy * vy) || 0.01;
      vx = (vx / mag) * speed * (0.8 + Math.random() * 0.4) * 0.01;
      vy = (vy / mag) * speed * (0.8 + Math.random() * 0.4) * 0.01;
      x += vx; y += vy;
      const boundary = 0.2 + (0.3 * (V / s.V0));
      if (x < 0.5 - boundary || x > 0.5 + boundary) vx = -vx;
      if (y < 0.5 - boundary || y > 0.5 + boundary) vy = -vy;
      x = Math.max(0.5 - boundary, Math.min(0.5 + boundary, x));
      y = Math.max(0.5 - boundary, Math.min(0.5 + boundary, y));
      return { ...pt, x, y, vx, vy };
    });
    return { ...s, T, V, progress, particles, time: s.time + Physics.DT };
  },
};

// ===== SimRenderer — draws simulation to canvas =====
const SimRenderer = {

  draw(ctx, W, H, id, state, params) {
    ctx.clearRect(0, 0, W, H);
    // Dark background
    const grad = ctx.createLinearGradient(0, 0, 0, H);
    grad.addColorStop(0, '#0d0d2e');
    grad.addColorStop(1, '#1a0a2e');
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, W, H);

    const fn = SimRenderer['_draw' + id.split('_').map(w => w[0] + w.slice(1).toLowerCase()).join('')];
    if (fn) fn.call(SimRenderer, ctx, W, H, state, params);
    else SimRenderer._drawGeneric(ctx, W, H, id);
  },

  // ===== PENDULUM =====
  _drawPendulum(ctx, W, H, s) {
    const cx = W / 2, cy = H * 0.18;
    const pxPerM = Math.min(W, H) * 0.38;
    const pendLen = s.length * pxPerM;
    const bobX = cx + pendLen * Math.sin(s.theta);
    const bobY = cy + pendLen * Math.cos(s.theta);

    // Trail
    if (s.trail?.length > 1) {
      ctx.save();
      for (let i = 1; i < s.trail.length; i++) {
        const a = i / s.trail.length;
        ctx.globalAlpha = a * 0.5;
        const tx = cx + pendLen * Math.sin(s.trail[i].theta);
        const ty = cy + pendLen * Math.cos(s.trail[i].theta);
        ctx.beginPath();
        ctx.arc(tx, ty, 5 * a, 0, Math.PI * 2);
        ctx.fillStyle = '#D0BCFF';
        ctx.fill();
      }
      ctx.restore();
    }

    // Ceiling mount
    ctx.fillStyle = '#444';
    ctx.fillRect(cx - 20, cy - 8, 40, 8);

    // Rod
    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.lineTo(bobX, bobY);
    ctx.strokeStyle = '#888';
    ctx.lineWidth = 2;
    ctx.stroke();

    // Bob
    const grad = ctx.createRadialGradient(bobX - 5, bobY - 5, 2, bobX, bobY, 18);
    grad.addColorStop(0, '#E8DEF8');
    grad.addColorStop(1, '#6750A4');
    ctx.beginPath();
    ctx.arc(bobX, bobY, 18, 0, Math.PI * 2);
    ctx.fillStyle = grad;
    ctx.fill();
    ctx.strokeStyle = '#D0BCFF';
    ctx.lineWidth = 2;
    ctx.stroke();

    // Info text
    this._text(ctx, `θ = ${(s.theta * 180 / Math.PI).toFixed(1)}°`, W / 2, H - 80, '#D0BCFF', 14, 'center');
    this._text(ctx, `ω = ${s.omega.toFixed(3)} рад/с`, W / 2, H - 60, '#E8DEF8', 13, 'center');
    this._text(ctx, `T ≈ ${Physics.pendulumPeriod(s.length).toFixed(2)} с`, W / 2, H - 40, '#CAC4D0', 12, 'center');
  },

  // ===== FREE FALL =====
  _drawFreeFall(ctx, W, H, s) {
    const floorY = H - 40;
    const pxPerM = (H - 80) / (s.initialHeight || 10);
    const ballY = floorY - s.y * pxPerM;
    const cx = W / 2;

    // Floor
    ctx.fillStyle = '#3a3060';
    ctx.fillRect(0, floorY, W, 4);
    // Scale
    for (let m = 0; m <= (s.initialHeight || 10); m++) {
      const sy = floorY - m * pxPerM;
      ctx.fillStyle = '#555';
      ctx.fillRect(cx - 40, sy, 10, 1);
      this._text(ctx, `${m}м`, cx - 52, sy, '#888', 10, 'right');
    }

    // Trail
    s.trail?.forEach((t, i) => {
      const a = i / s.trail.length;
      const ty = floorY - t.y * pxPerM;
      ctx.globalAlpha = a * 0.4;
      ctx.beginPath();
      ctx.arc(cx, ty, 6 * a, 0, Math.PI * 2);
      ctx.fillStyle = '#FFB74D';
      ctx.fill();
      ctx.globalAlpha = 1;
    });

    // Ball
    const grad = ctx.createRadialGradient(cx - 4, ballY - 4, 2, cx, ballY, 16);
    grad.addColorStop(0, '#FFE082');
    grad.addColorStop(1, '#FF6F00');
    ctx.beginPath();
    ctx.arc(cx, ballY, 16, 0, Math.PI * 2);
    ctx.fillStyle = grad;
    ctx.fill();

    this._text(ctx, `h = ${s.y.toFixed(2)} м`, W / 2, H - 80, '#FFE082', 14, 'center');
    this._text(ctx, `v = ${Math.abs(s.vy).toFixed(2)} м/с`, W / 2, H - 60, '#FFB74D', 13, 'center');
    this._text(ctx, `t = ${s.time.toFixed(2)} с`, W / 2, H - 40, '#FFCC80', 12, 'center');
    if (s.hasLanded) this._text(ctx, 'Приземлился!', W / 2, H / 2, '#4CAF50', 20, 'center');
  },

  // ===== COLLISION =====
  _drawCollision(ctx, W, H, s) {
    const y = H / 2;
    const ballY = y;
    const r1 = 0.06 * W, r2 = 0.06 * W;

    // Floor line
    ctx.strokeStyle = '#3a3060';
    ctx.lineWidth = 2;
    ctx.setLineDash([8, 4]);
    ctx.beginPath(); ctx.moveTo(0, y + 20); ctx.lineTo(W, y + 20); ctx.stroke();
    ctx.setLineDash([]);

    // Walls
    ctx.fillStyle = '#444';
    ctx.fillRect(0, y - 60, 6, 80);
    ctx.fillRect(W - 6, y - 60, 6, 80);

    // Ball 1
    const g1 = ctx.createRadialGradient(s.x1 * W - 6, ballY - 6, 3, s.x1 * W, ballY, r1);
    g1.addColorStop(0, '#90CAF9'); g1.addColorStop(1, '#1565C0');
    ctx.beginPath(); ctx.arc(s.x1 * W, ballY, r1, 0, Math.PI * 2);
    ctx.fillStyle = g1; ctx.fill();
    this._text(ctx, `m₁=${s.mass1}кг`, s.x1 * W, ballY + r1 + 16, '#90CAF9', 11, 'center');
    this._text(ctx, `v₁=${s.v1.toFixed(1)}`, s.x1 * W, ballY - r1 - 6, '#90CAF9', 11, 'center');

    // Ball 2
    const g2 = ctx.createRadialGradient(s.x2 * W - 6, ballY - 6, 3, s.x2 * W, ballY, r2);
    g2.addColorStop(0, '#EF9A9A'); g2.addColorStop(1, '#B71C1C');
    ctx.beginPath(); ctx.arc(s.x2 * W, ballY, r2, 0, Math.PI * 2);
    ctx.fillStyle = g2; ctx.fill();
    this._text(ctx, `m₂=${s.mass2}кг`, s.x2 * W, ballY + r2 + 16, '#EF9A9A', 11, 'center');
    this._text(ctx, `v₂=${s.v2.toFixed(1)}`, s.x2 * W, ballY - r2 - 6, '#EF9A9A', 11, 'center');

    this._text(ctx, `Eк = ${s.ke.toFixed(2)} Дж`, W / 2, H - 60, '#E8DEF8', 13, 'center');
    this._text(ctx, s.hasCollided ? '💥 Столкновение!' : `p = ${(Physics.momentum(s.mass1, s.v1) + Physics.momentum(s.mass2, s.v2)).toFixed(2)} кг·м/с`, W / 2, H - 40, '#FFE082', 12, 'center');
  },

  // ===== ELECTRIC CIRCUIT =====
  _drawElectricCircuit(ctx, W, H, s) {
    const cx = W / 2, cy = H / 2;
    const w = Math.min(W * 0.7, 320), h = Math.min(H * 0.4, 180);
    const l = cx - w / 2, r = cx + w / 2, t = cy - h / 2, b = cy + h / 2;

    // Wire glow
    ctx.shadowBlur = 8;
    ctx.shadowColor = '#FFB300';
    ctx.strokeStyle = '#FFA000';
    ctx.lineWidth = 3;
    ctx.beginPath();
    ctx.moveTo(l, t); ctx.lineTo(r, t);
    ctx.lineTo(r, b); ctx.lineTo(l, b); ctx.lineTo(l, t);
    ctx.stroke();
    ctx.shadowBlur = 0;

    // Battery
    const batX = l + 8; const bcy = cy;
    ctx.fillStyle = '#4CAF50';
    ctx.fillRect(batX, bcy - 20, 12, 40);
    ctx.fillStyle = '#81C784'; ctx.fillRect(batX + 2, bcy - 14, 8, 8);
    ctx.fillStyle = '#A5D6A7'; ctx.fillRect(batX + 2, bcy + 6, 8, 8);
    this._text(ctx, `${s.voltage}В`, batX + 6, bcy + 36, '#81C784', 12, 'center');

    // Draw resistors on top or bottom
    const rCount = 3;
    const rKeys = ['resistance1','resistance2','resistance3'];
    const rSpacing = w / (rCount + 1);
    const rY = s.isParallel ? [cy - 50, cy, cy + 50] : [t - 1, t - 1, t - 1];
    const rXs = s.isParallel
      ? [cx, cx, cx]
      : [l + rSpacing, l + rSpacing * 2, l + rSpacing * 3];

    if (s.isParallel) {
      // 3 parallel branches
      [0,1,2].forEach((i) => {
        const rx = cx - 30, ry = cy - 60 + i * 60;
        this._drawResistor(ctx, rx, ry + 20, 60, s[rKeys[i]], s.I / rCount);
        ctx.strokeStyle = '#FFA000'; ctx.lineWidth = 2;
        ctx.beginPath(); ctx.moveTo(l + 20, ry + 20); ctx.lineTo(rx, ry + 20); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(rx + 60, ry + 20); ctx.lineTo(r, ry + 20); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(l + 20, t); ctx.lineTo(l + 20, b); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(r, t); ctx.lineTo(r, b); ctx.stroke();
      });
    } else {
      rXs.forEach((rx, i) => {
        this._drawResistor(ctx, rx - 30, t, 60, s[rKeys[i]], s.I);
      });
    }

    // Flowing electrons
    const ePos = (s.phase / (2 * Math.PI));
    const totalLen = 2 * (w + h);
    [0, 0.25, 0.5, 0.75].forEach(offset => {
      const pos = ((ePos + offset) % 1) * totalLen;
      const pt = this._posOnRect(l, t, w, h, pos);
      ctx.beginPath();
      ctx.arc(pt.x, pt.y, 4, 0, Math.PI * 2);
      ctx.fillStyle = '#FFE082';
      ctx.shadowBlur = 8; ctx.shadowColor = '#FFE082';
      ctx.fill(); ctx.shadowBlur = 0;
    });

    this._text(ctx, `I = ${s.I.toFixed(3)} А`, W / 2, b + 30, '#FFE082', 14, 'center');
    this._text(ctx, `Rобщ = ${s.Rtotal.toFixed(1)} Ом  |  P = ${s.power.toFixed(2)} Вт`, W / 2, b + 50, '#FFB300', 12, 'center');
    this._text(ctx, s.isParallel ? 'Параллельное соединение' : 'Последовательное соединение', W / 2, b + 70, '#CAC4D0', 11, 'center');
  },

  _drawResistor(ctx, x, y, w, V, I) {
    ctx.strokeStyle = '#FF9800'; ctx.lineWidth = 2;
    ctx.beginPath(); ctx.moveTo(x, y); ctx.lineTo(x + 8, y);
    const seg = (w - 16) / 8;
    for (let i = 0; i < 8; i++) {
      ctx.lineTo(x + 8 + i * seg + seg / 2, y + (i % 2 === 0 ? -10 : 10));
      ctx.lineTo(x + 8 + (i + 1) * seg, y);
    }
    ctx.lineTo(x + w, y); ctx.stroke();
    this._text(ctx, `${V}Ω`, x + w / 2, y + 22, '#FFCC02', 10, 'center');
  },

  _posOnRect(l, t, w, h, dist) {
    const perimeter = 2 * (w + h);
    const d = ((dist % perimeter) + perimeter) % perimeter;
    if (d < w) return { x: l + d, y: t };
    if (d < w + h) return { x: l + w, y: t + (d - w) };
    if (d < 2 * w + h) return { x: l + w - (d - w - h), y: t + h };
    return { x: l, y: t + h - (d - 2 * w - h) };
  },

  // ===== MAGNETIC FIELD =====
  _drawMagneticField(ctx, W, H, s) {
    const cx = W / 2, cy = H / 2;

    // Wire (cross-section)
    ctx.beginPath(); ctx.arc(cx, cy, 14, 0, Math.PI * 2);
    ctx.fillStyle = '#B0BEC5'; ctx.fill();
    ctx.strokeStyle = '#607D8B'; ctx.lineWidth = 2; ctx.stroke();
    ctx.strokeStyle = '#455A64'; ctx.lineWidth = 2;
    ctx.beginPath(); ctx.moveTo(cx - 8, cy - 8); ctx.lineTo(cx + 8, cy + 8); ctx.stroke();
    ctx.beginPath(); ctx.moveTo(cx + 8, cy - 8); ctx.lineTo(cx - 8, cy + 8); ctx.stroke();

    // Field lines
    const nLines = s.numberOfLines || 8;
    const maxR = Math.min(W, H) * 0.42;
    for (let i = 1; i <= nLines; i++) {
      const radius = (maxR / nLines) * i;
      const alpha = 1 - i / (nLines + 2);
      ctx.beginPath();
      ctx.arc(cx, cy, radius, 0, Math.PI * 2);
      ctx.strokeStyle = `rgba(100,181,246,${alpha})`;
      ctx.lineWidth = 1.5;
      ctx.setLineDash([]);
      ctx.stroke();

      // Animated arrow on each ring
      const angle = s.phase + (i * Math.PI * 0.7);
      const ax = cx + radius * Math.cos(angle);
      const ay = cy + radius * Math.sin(angle);
      this._arrowHead(ctx, ax, ay, angle + Math.PI / 2, 8, `rgba(100,181,246,${alpha})`);
    }

    // Field direction labels
    const B = (4 * Math.PI * 1e-7 * s.current) / (2 * Math.PI * 0.1);
    this._text(ctx, `I = ${s.current} А`, W / 2, H - 70, '#81D4FA', 13, 'center');
    this._text(ctx, `B(r=10см) ≈ ${(B * 1e6).toFixed(2)} мкТл`, W / 2, H - 50, '#B3E5FC', 12, 'center');
    this._text(ctx, 'Ток ⊗ (в экран)', W / 2, cy + 28, '#90A4AE', 11, 'center');
  },

  _arrowHead(ctx, x, y, angle, size, color) {
    ctx.save();
    ctx.translate(x, y); ctx.rotate(angle);
    ctx.beginPath();
    ctx.moveTo(0, -size); ctx.lineTo(size / 2, size / 2); ctx.lineTo(-size / 2, size / 2);
    ctx.closePath(); ctx.fillStyle = color; ctx.fill();
    ctx.restore();
  },

  // ===== LIGHT REFRACTION =====
  _drawLightRefraction(ctx, W, H, s) {
    const cx = W / 2, cy = H / 2;
    const rayLen = Math.min(W, H) * 0.42;

    // Media separation
    ctx.fillStyle = 'rgba(26,26,60,0.9)';
    ctx.fillRect(0, cy, W, H / 2);
    ctx.fillStyle = 'rgba(21,101,192,0.2)';
    ctx.fillRect(0, cy, W, H / 2);

    // Normal line
    if (s.showNormal) {
      ctx.setLineDash([6, 6]);
      ctx.strokeStyle = 'rgba(255,255,255,0.3)';
      ctx.lineWidth = 1;
      ctx.beginPath(); ctx.moveTo(cx, cy - rayLen * 0.8); ctx.lineTo(cx, cy + rayLen * 0.8); ctx.stroke();
      ctx.setLineDash([]);
    }

    // Incident ray
    const ia = s.incidentAngle;
    const ix = cx - rayLen * Math.sin(ia);
    const iy = cy - rayLen * Math.cos(ia);
    ctx.strokeStyle = '#FFF176'; ctx.lineWidth = 2;
    ctx.shadowBlur = 8; ctx.shadowColor = '#FFF176';
    ctx.beginPath(); ctx.moveTo(ix, iy); ctx.lineTo(cx, cy); ctx.stroke();

    if (s.refractedAngle !== null) {
      // Refracted ray
      const ra = s.refractedAngle;
      const rx = cx + rayLen * Math.sin(ra);
      const ry = cy + rayLen * Math.cos(ra);
      ctx.strokeStyle = '#80DEEA'; ctx.lineWidth = 2; ctx.shadowColor = '#80DEEA';
      ctx.beginPath(); ctx.moveTo(cx, cy); ctx.lineTo(rx, ry); ctx.stroke();
      ctx.shadowBlur = 0;

      // Reflected ray
      const rix = cx + rayLen * Math.sin(ia);
      const riy = cy - rayLen * Math.cos(ia);
      ctx.strokeStyle = 'rgba(255,241,118,0.4)'; ctx.lineWidth = 1;
      ctx.beginPath(); ctx.moveTo(cx, cy); ctx.lineTo(rix, riy); ctx.stroke();
    } else {
      ctx.shadowBlur = 0;
      // Total internal reflection
      const rix = cx + rayLen * Math.sin(ia);
      const riy = cy - rayLen * Math.cos(ia);
      ctx.strokeStyle = '#FFF176'; ctx.lineWidth = 2; ctx.shadowBlur = 8; ctx.shadowColor = '#FFF176';
      ctx.beginPath(); ctx.moveTo(cx, cy); ctx.lineTo(rix, riy); ctx.stroke();
      ctx.shadowBlur = 0;
      this._text(ctx, 'Полное внутреннее отражение!', W / 2, cy - 30, '#FF7043', 13, 'center');
    }

    // Labels
    this._text(ctx, `n₁ = ${s.n1.toFixed(2)} (воздух)`, W / 2, 50, '#FFF9C4', 12, 'center');
    this._text(ctx, `n₂ = ${s.n2.toFixed(2)} (стекло)`, W / 2, H - 50, '#B2EBF2', 12, 'center');
    this._text(ctx, `α₁ = ${(ia * 180 / Math.PI).toFixed(1)}°`, 80, cy - 30, '#FFF176', 12, 'center');
    if (s.refractedAngle !== null) this._text(ctx, `α₂ = ${(s.refractedAngle * 180 / Math.PI).toFixed(1)}°`, 80, cy + 40, '#80DEEA', 12, 'center');
  },

  // ===== LENS =====
  _drawLens(ctx, W, H, s) {
    const cx = W / 2, cy = H / 2;
    const scale = Math.min(W, H) * 0.35;

    // Optical axis
    ctx.setLineDash([4, 4]);
    ctx.strokeStyle = 'rgba(255,255,255,0.2)';
    ctx.lineWidth = 1;
    ctx.beginPath(); ctx.moveTo(0, cy); ctx.lineTo(W, cy); ctx.stroke();
    ctx.setLineDash([]);

    // Lens
    ctx.strokeStyle = s.isConverging ? '#80DEEA' : '#EF9A9A';
    ctx.lineWidth = 3;
    ctx.shadowBlur = 10; ctx.shadowColor = ctx.strokeStyle;
    ctx.beginPath();
    if (s.isConverging) {
      ctx.moveTo(cx, cy - scale * 0.5);
      ctx.quadraticCurveTo(cx + 20, cy, cx, cy + scale * 0.5);
      ctx.moveTo(cx, cy - scale * 0.5);
      ctx.quadraticCurveTo(cx - 20, cy, cx, cy + scale * 0.5);
    } else {
      ctx.moveTo(cx, cy - scale * 0.5);
      ctx.quadraticCurveTo(cx - 20, cy, cx, cy + scale * 0.5);
      ctx.moveTo(cx, cy - scale * 0.5);
      ctx.quadraticCurveTo(cx + 20, cy, cx, cy + scale * 0.5);
    }
    ctx.stroke(); ctx.shadowBlur = 0;

    // Focal points
    const fpx = cx + s.focalLength * scale;
    const fpx2 = cx - s.focalLength * scale;
    [fpx, fpx2].forEach(fx => {
      ctx.beginPath(); ctx.arc(fx, cy, 4, 0, Math.PI * 2);
      ctx.fillStyle = '#FFD54F'; ctx.fill();
    });
    this._text(ctx, 'F', fpx + 8, cy - 10, '#FFD54F', 11, 'left');
    this._text(ctx, 'F', fpx2 + 8, cy - 10, '#FFD54F', 11, 'left');

    // Object arrow
    const ox = cx - s.objectDistance * scale;
    const oh = s.objectHeight * scale * 0.5;
    ctx.strokeStyle = '#A5D6A7'; ctx.lineWidth = 2;
    ctx.beginPath(); ctx.moveTo(ox, cy); ctx.lineTo(ox, cy - oh); ctx.stroke();
    this._arrowHead(ctx, ox, cy - oh, -Math.PI / 2, 8, '#A5D6A7');

    // Image arrow
    if (isFinite(s.di) && Math.abs(s.di) < 3 * s.objectDistance) {
      const ix = cx + s.di * scale;
      const ih = s.m * oh;
      ctx.strokeStyle = s.isReal ? '#EF9A9A' : 'rgba(239,154,154,0.4)';
      ctx.lineWidth = 2;
      ctx.setLineDash(s.isReal ? [] : [4, 4]);
      ctx.beginPath(); ctx.moveTo(ix, cy); ctx.lineTo(ix, cy - ih); ctx.stroke();
      ctx.setLineDash([]);
      this._arrowHead(ctx, ix, cy - ih, ih > 0 ? -Math.PI / 2 : Math.PI / 2, 8, ctx.strokeStyle);
      this._text(ctx, s.isReal ? 'Действительное' : 'Мнимое', ix, cy + 20, '#EF9A9A', 10, 'center');
    }

    this._text(ctx, `f = ${s.focalLength.toFixed(2)} м  |  do = ${s.objectDistance.toFixed(2)} м`, W / 2, H - 60, '#E8DEF8', 12, 'center');
    this._text(ctx, `di = ${isFinite(s.di) ? s.di.toFixed(2) + ' м' : '∞'}  |  M = ${isFinite(s.m) ? s.m.toFixed(2) : '∞'}`, W / 2, H - 40, '#B0BEC5', 11, 'center');
  },

  // ===== BROWNIAN MOTION =====
  _drawBrownianMotion(ctx, W, H, s) {
    const scale = (px) => px * W;
    const scaleY = (py) => py * H;

    // Fluid background
    ctx.fillStyle = 'rgba(21,101,192,0.06)';
    ctx.fillRect(0, 0, W, H);

    // Small background molecules
    for (let i = 0; i < 80; i++) {
      const x = (Math.sin(i * 137.5 + s.time * 0.3) * 0.5 + 0.5) * W;
      const y = (Math.cos(i * 97.3 + s.time * 0.2) * 0.5 + 0.5) * H;
      ctx.beginPath(); ctx.arc(x, y, 2, 0, Math.PI * 2);
      ctx.fillStyle = 'rgba(100,181,246,0.3)'; ctx.fill();
    }

    // Brownian particles
    s.particles?.forEach((p, pi) => {
      const hue = (pi * 40) % 360;
      // Trail
      p.trail?.forEach((t, i) => {
        ctx.globalAlpha = (i / p.trail.length) * 0.5;
        ctx.beginPath(); ctx.arc(scale(t.x), scaleY(t.y), 2, 0, Math.PI * 2);
        ctx.fillStyle = `hsl(${hue},70%,70%)`; ctx.fill();
      });
      ctx.globalAlpha = 1;
      // Particle
      ctx.beginPath(); ctx.arc(scale(p.x), scaleY(p.y), 7, 0, Math.PI * 2);
      ctx.fillStyle = `hsl(${hue},80%,60%)`; ctx.fill();
      ctx.strokeStyle = '#fff'; ctx.lineWidth = 1; ctx.stroke();
    });

    const D = Physics.BOLTZMANN * s.temperature / (6 * Math.PI * s.viscosity * s.particleRadius);
    this._text(ctx, `T = ${s.temperature} К`, W / 2, H - 70, '#E8DEF8', 13, 'center');
    this._text(ctx, `D = ${D.toExponential(2)} м²/с`, W / 2, H - 50, '#B0BEC5', 11, 'center');
    this._text(ctx, `η = ${s.viscosity} Па·с`, W / 2, H - 30, '#90A4AE', 10, 'center');
  },

  // ===== GAS EXPANSION =====
  _drawGasExpansion(ctx, W, H, s) {
    const cx = W / 2, cy = H / 2;
    const boundary = (0.2 + 0.3 * (s.V / (s.V0 || 1))) * Math.min(W, H);

    // Container
    ctx.strokeStyle = '#90A4AE'; ctx.lineWidth = 3;
    ctx.beginPath();
    ctx.rect(cx - boundary, cy - boundary, boundary * 2, boundary * 2);
    ctx.stroke();

    // Piston (right side) moves with expansion
    const pistonX = cx + boundary;
    ctx.fillStyle = '#546E7A'; ctx.fillRect(pistonX, cy - boundary, 8, boundary * 2);
    ctx.fillStyle = '#78909C'; ctx.fillRect(pistonX + 2, cy - boundary + 2, 4, boundary * 2 - 4);

    // Particles
    s.particles?.forEach((p, i) => {
      const hue = 30 + (s.T / s.T0) * 60;
      const px = p.x * W, py = p.y * H;
      if (px >= cx - boundary && px <= cx + boundary && py >= cy - boundary && py <= cy + boundary) {
        ctx.beginPath(); ctx.arc(px, py, 4, 0, Math.PI * 2);
        ctx.fillStyle = `hsl(${hue},80%,60%)`; ctx.fill();
      }
    });

    // Temperature color indicator
    const tempPct = (s.T - s.T0) / Math.max(1, s.Tf - s.T0);
    ctx.fillStyle = `rgba(244,67,54,${tempPct * 0.15})`;
    ctx.fillRect(cx - boundary, cy - boundary, boundary * 2, boundary * 2);

    this._text(ctx, `T = ${s.T.toFixed(1)} К`, W / 2, H - 80, '#FFCCBC', 14, 'center');
    this._text(ctx, `V = ${s.V.toFixed(3)} м³`, W / 2, H - 60, '#FFAB91', 13, 'center');
    this._text(ctx, `Прогресс: ${(s.progress * 100).toFixed(0)}%`, W / 2, H - 40, '#FF8A65', 12, 'center');
  },

  _drawGeneric(ctx, W, H, id) {
    ctx.fillStyle = '#E8DEF8';
    ctx.font = '16px Roboto, sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText(`Симуляция: ${id}`, W / 2, H / 2);
  },

  _text(ctx, text, x, y, color, size, align = 'left') {
    ctx.fillStyle = color;
    ctx.font = `${size}px Roboto, sans-serif`;
    ctx.textAlign = align;
    ctx.fillText(text, x, y);
  },

  // ===== Info rows for info panel =====
  getInfoRows(id, s) {
    switch (id) {
      case 'PENDULUM': return [
        { label: 'Угол отклонения', value: `${(s.theta * 180 / Math.PI).toFixed(2)}°` },
        { label: 'Угл. скорость', value: `${s.omega.toFixed(4)} рад/с` },
        { label: 'Период', value: `${Physics.pendulumPeriod(s.length).toFixed(3)} с` },
        { label: 'Время', value: `${s.time.toFixed(1)} с` },
      ];
      case 'FREE_FALL': return [
        { label: 'Высота', value: `${s.y.toFixed(3)} м` },
        { label: 'Скорость', value: `${Math.abs(s.vy).toFixed(3)} м/с` },
        { label: 'Ускорение', value: `${Physics.G} м/с²` },
        { label: 'Время', value: `${s.time.toFixed(2)} с` },
        { label: 'Eк', value: `${Physics.kineticEnergy(1, s.vy).toFixed(2)} Дж` },
      ];
      case 'COLLISION': return [
        { label: 'v₁', value: `${s.v1.toFixed(3)} м/с` },
        { label: 'v₂', value: `${s.v2.toFixed(3)} м/с` },
        { label: 'p₁', value: `${Physics.momentum(s.mass1, s.v1).toFixed(3)} кг·м/с` },
        { label: 'p₂', value: `${Physics.momentum(s.mass2, s.v2).toFixed(3)} кг·м/с` },
        { label: 'Eк', value: `${s.ke.toFixed(3)} Дж` },
      ];
      case 'ELECTRIC_CIRCUIT': return [
        { label: 'Напряжение', value: `${s.voltage} В` },
        { label: 'Ток', value: `${s.I.toFixed(4)} А` },
        { label: 'R общ', value: `${s.Rtotal.toFixed(2)} Ом` },
        { label: 'Мощность', value: `${s.power.toFixed(3)} Вт` },
        { label: 'Соединение', value: s.isParallel ? 'Параллельное' : 'Последовательное' },
      ];
      case 'MAGNETIC_FIELD': return [
        { label: 'Ток', value: `${s.current} А` },
        { label: 'B (10 см)', value: `${((4e-7 * Math.PI * s.current) / (2 * Math.PI * 0.1) * 1e6).toFixed(2)} мкТл` },
      ];
      case 'LIGHT_REFRACTION': return [
        { label: 'Угол падения', value: `${(s.incidentAngle * 180 / Math.PI).toFixed(1)}°` },
        { label: 'Угол преломления', value: s.refractedAngle != null ? `${(s.refractedAngle * 180 / Math.PI).toFixed(1)}°` : 'ПВО' },
        { label: 'n₁', value: s.n1.toFixed(3) },
        { label: 'n₂', value: s.n2.toFixed(3) },
        { label: 'Кр. угол', value: s.criticalAngle ? `${(s.criticalAngle * 180 / Math.PI).toFixed(1)}°` : '—' },
      ];
      case 'LENS': return [
        { label: 'Фокусное расст.', value: `${s.focalLength.toFixed(3)} м` },
        { label: 'Расст. предмета', value: `${s.objectDistance.toFixed(3)} м` },
        { label: 'Расст. изобр.', value: isFinite(s.di) ? `${s.di.toFixed(3)} м` : '∞' },
        { label: 'Увеличение', value: isFinite(s.m) ? s.m.toFixed(3) : '∞' },
        { label: 'Тип изобр.', value: s.isReal ? 'Действительное' : 'Мнимое' },
      ];
      case 'BROWNIAN_MOTION': return [
        { label: 'Температура', value: `${s.temperature} К` },
        { label: 'Вязкость', value: `${s.viscosity} Па·с` },
        { label: 'Частиц', value: s.particles?.length || 0 },
      ];
      case 'GAS_EXPANSION': return [
        { label: 'Температура', value: `${s.T?.toFixed(1)} К` },
        { label: 'Объём', value: `${s.V?.toFixed(4)} м³` },
        { label: 'Прогресс', value: `${((s.progress || 0) * 100).toFixed(0)}%` },
        { label: 'Давление', value: `${((s.P || 0) / 1000).toFixed(2)} кПа` },
      ];
      default: return [];
    }
  },

  // ===== Param definitions for params sheet =====
  getParamDefs(id) {
    switch (id) {
      case 'PENDULUM': return [
        { key: 'length',       label: 'Длина (м)',         min: 0.1, max: 3,   step: 0.05, unit: 'м' },
        { key: 'initialAngle', label: 'Начальный угол',    min: 0.05, max: 1.4, step: 0.05, unit: 'рад' },
        { key: 'mass',         label: 'Масса (кг)',        min: 0.1, max: 5,   step: 0.1,  unit: 'кг' },
        { key: 'damping',      label: 'Затухание',         min: 0,   max: 0.2,  step: 0.005, unit: '' },
      ];
      case 'FREE_FALL': return [
        { key: 'initialHeight',   label: 'Высота (м)',    min: 1, max: 50, step: 0.5, unit: 'м' },
        { key: 'initialVelocity', label: 'Нач. скорость', min: -10, max: 10, step: 0.5, unit: 'м/с' },
        { key: 'mass',            label: 'Масса (кг)',    min: 0.1, max: 20, step: 0.5, unit: 'кг' },
        { key: 'showTrail',       label: 'Трассировка',   type: 'bool' },
      ];
      case 'COLLISION': return [
        { key: 'mass1',     label: 'Масса 1 (кг)',    min: 0.5, max: 10, step: 0.5, unit: 'кг' },
        { key: 'mass2',     label: 'Масса 2 (кг)',    min: 0.5, max: 10, step: 0.5, unit: 'кг' },
        { key: 'velocity1', label: 'Скорость 1 (м/с)', min: -10, max: 15, step: 0.5, unit: 'м/с' },
        { key: 'velocity2', label: 'Скорость 2 (м/с)', min: -10, max: 10, step: 0.5, unit: 'м/с' },
        { key: 'isElastic', label: 'Упругий удар',    type: 'bool' },
      ];
      case 'ELECTRIC_CIRCUIT': return [
        { key: 'voltage',     label: 'Напряжение (В)', min: 1, max: 100, step: 1, unit: 'В' },
        { key: 'resistance1', label: 'R1 (Ом)',        min: 10, max: 500, step: 10, unit: 'Ом' },
        { key: 'resistance2', label: 'R2 (Ом)',        min: 10, max: 500, step: 10, unit: 'Ом' },
        { key: 'resistance3', label: 'R3 (Ом)',        min: 10, max: 500, step: 10, unit: 'Ом' },
        { key: 'isParallel',  label: 'Параллельно',   type: 'bool' },
      ];
      case 'MAGNETIC_FIELD': return [
        { key: 'current',       label: 'Ток (А)',       min: 0.1, max: 20, step: 0.5, unit: 'А' },
        { key: 'numberOfLines', label: 'Линий поля',   min: 4, max: 16, step: 1, unit: '' },
        { key: 'showFieldLines', label: 'Линии поля',  type: 'bool' },
      ];
      case 'LIGHT_REFRACTION': return [
        { key: 'incidentAngle', label: 'Угол падения', min: 0.05, max: 1.55, step: 0.05, unit: 'рад' },
        { key: 'n1', label: 'n₁ (среда 1)', min: 1.0, max: 2.5, step: 0.05, unit: '' },
        { key: 'n2', label: 'n₂ (среда 2)', min: 1.0, max: 2.5, step: 0.05, unit: '' },
        { key: 'showNormal', label: 'Нормаль', type: 'bool' },
      ];
      case 'LENS': return [
        { key: 'focalLength',    label: 'Фокус (м)',          min: 0.1, max: 2, step: 0.05, unit: 'м' },
        { key: 'objectDistance', label: 'Расст. предмета (м)', min: 0.1, max: 3, step: 0.05, unit: 'м' },
        { key: 'objectHeight',   label: 'Высота предмета',    min: 0.1, max: 1, step: 0.05, unit: 'м' },
        { key: 'isConverging',   label: 'Собирающая линза',   type: 'bool' },
      ];
      case 'BROWNIAN_MOTION': return [
        { key: 'temperature',        label: 'Температура (К)', min: 100, max: 1000, step: 10, unit: 'К' },
        { key: 'numberOfParticles',  label: 'Частиц',          min: 5,   max: 40,   step: 1,  unit: '' },
      ];
      case 'GAS_EXPANSION': return [
        { key: 'initialTemperature', label: 'Нач. темп. (К)',  min: 100, max: 400, step: 10, unit: 'К' },
        { key: 'finalTemperature',   label: 'Кон. темп. (К)',  min: 200, max: 1000, step: 10, unit: 'К' },
        { key: 'moles',              label: 'Молей (n)',        min: 0.1, max: 5,   step: 0.1, unit: 'моль' },
      ];
      default: return [];
    }
  },
};
