const DT = 0.016;

const SimEngine = {
  // ===== PENDULUM =====
  initPendulum(p) {
    return { isRunning: true, time: 0, angle: p.initialAngle, angVel: 0,
             period: PhysicsCalc.pendulumPeriod(p.length), ke: 0, pe: 0 };
  },
  updatePendulum(p, s) {
    if (!s.isRunning) return s;
    const t = s.time + DT;
    const angle = PhysicsCalc.pendulumAngle(p.initialAngle, p.length, t, p.damping);
    const angVel = PhysicsCalc.pendulumAngVel(p.initialAngle, p.length, t, p.damping);
    const h = p.length * (1 - Math.cos(angle));
    const pe = p.mass * GRAVITY * h;
    const v = p.length * Math.abs(angVel);
    const ke = PhysicsCalc.kineticEnergy(p.mass, v);
    return { ...s, time: t, angle, angVel, period: PhysicsCalc.pendulumPeriod(p.length), ke, pe };
  },

  // ===== FREE FALL =====
  initFreeFall(p) {
    return { isRunning: true, time: 0, height: p.initialHeight,
             vel: p.initialVelocity, hasLanded: false, maxVel: Math.abs(p.initialVelocity) };
  },
  updateFreeFall(p, s) {
    if (!s.isRunning || s.hasLanded) return s;
    const t = s.time + DT;
    const h = PhysicsCalc.freeFallPos(p.initialHeight, p.initialVelocity, t);
    const v = PhysicsCalc.freeFallVel(p.initialVelocity, t);
    const landed = h <= 0;
    return { ...s, time: t, height: Math.max(0, h),
             vel: landed ? 0 : v, hasLanded: landed,
             maxVel: Math.max(s.maxVel, Math.abs(v)) };
  },

  // ===== COLLISION =====
  initCollision(p) {
    const mom = PhysicsCalc.momentum(p.mass1, p.velocity1) + PhysicsCalc.momentum(p.mass2, p.velocity2);
    const en = PhysicsCalc.kineticEnergy(p.mass1, p.velocity1) + PhysicsCalc.kineticEnergy(p.mass2, p.velocity2);
    return { isRunning: true, time: 0, pos1: -2, pos2: 2,
             vel1: p.velocity1, vel2: p.velocity2, hasCollided: false,
             momBefore: mom, momAfter: mom, enBefore: en, enAfter: en };
  },
  updateCollision(p, s) {
    if (!s.isRunning) return s;
    const t = s.time + DT;
    let pos1 = s.pos1 + s.vel1 * DT;
    let pos2 = s.pos2 + s.vel2 * DT;
    let vel1 = s.vel1, vel2 = s.vel2;
    let hasCollided = s.hasCollided;
    let momAfter = s.momAfter, enAfter = s.enAfter;
    const R = 0.3;
    if (!hasCollided && Math.abs(pos1 - pos2) <= 2 * R) {
      hasCollided = true;
      if (p.isElastic) {
        [vel1, vel2] = PhysicsCalc.elasticCollision(p.mass1, s.vel1, p.mass2, s.vel2);
      } else {
        vel1 = vel2 = PhysicsCalc.inelasticCollision(p.mass1, s.vel1, p.mass2, s.vel2);
      }
      momAfter = PhysicsCalc.momentum(p.mass1, vel1) + PhysicsCalc.momentum(p.mass2, vel2);
      enAfter = PhysicsCalc.kineticEnergy(p.mass1, vel1) + PhysicsCalc.kineticEnergy(p.mass2, vel2);
    }
    return { ...s, time: t, pos1, pos2, vel1, vel2, hasCollided, momAfter, enAfter };
  },

  // ===== CIRCUIT =====
  initCircuit(p) { return { isRunning: true, time: 0, phase: 0, totalR: 0, totalI: 0, i1: 0, i2: 0, i3: 0, u1: 0, u2: 0, u3: 0, power: 0 }; },
  updateCircuit(p, s) {
    if (!s.isRunning) return s;
    const t = s.time + DT;
    const phase = (s.phase + DT * 2) % (2 * PI);
    const totalR = p.isParallel
      ? PhysicsCalc.parallelR(p.resistance1, p.resistance2, p.resistance3)
      : PhysicsCalc.seriesR(p.resistance1, p.resistance2, p.resistance3);
    const totalI = PhysicsCalc.ohmsI(p.voltage, totalR);
    let i1, i2, i3, u1, u2, u3;
    if (p.isParallel) {
      i1 = PhysicsCalc.ohmsI(p.voltage, p.resistance1);
      i2 = PhysicsCalc.ohmsI(p.voltage, p.resistance2);
      i3 = PhysicsCalc.ohmsI(p.voltage, p.resistance3);
      u1 = u2 = u3 = p.voltage;
    } else {
      i1 = i2 = i3 = totalI;
      u1 = PhysicsCalc.ohmsV(totalI, p.resistance1);
      u2 = PhysicsCalc.ohmsV(totalI, p.resistance2);
      u3 = PhysicsCalc.ohmsV(totalI, p.resistance3);
    }
    const power = PhysicsCalc.power(p.voltage, totalI);
    return { ...s, time: t, phase, totalR, totalI, i1, i2, i3, u1, u2, u3, power };
  },

  // ===== MAGNETIC FIELD =====
  initMagField(p) { return { isRunning: true, time: 0, animPhase: 0, fieldMap: {} }; },
  updateMagField(p, s) {
    if (!s.isRunning) return s;
    const t = s.time + DT;
    const animPhase = (s.animPhase + DT * p.current * 0.5) % (2 * PI);
    const distances = [0.01, 0.02, 0.05, 0.1, 0.2, 0.5];
    const fieldMap = {};
    distances.forEach(d => { fieldMap[d] = PhysicsCalc.magneticB(p.current, d); });
    return { ...s, time: t, animPhase, fieldMap };
  },

  // ===== REFRACTION =====
  initRefraction(p) { return { isRunning: true, time: 0, progress: 0, refAngle: null, isTIR: false, critAngle: null }; },
  updateRefraction(p, s) {
    if (!s.isRunning) return s;
    const t = s.time + DT;
    const progress = Math.min(1, s.progress + DT * 0.5);
    const refAngle = PhysicsCalc.snellAngle(p.incidentAngle, p.n1, p.n2);
    const critAngle = PhysicsCalc.criticalAngle(p.n1, p.n2);
    return { ...s, time: t, progress, refAngle, isTIR: refAngle === null, critAngle };
  },

  // ===== LENS =====
  initLens(p) { return { isRunning: true, time: 0, progress: 0, imageDist: 0, imageH: 0, mag: 0, isVirtual: false, isInverted: false }; },
  updateLens(p, s) {
    if (!s.isRunning) return s;
    const t = s.time + DT;
    const progress = Math.min(1, s.progress + DT * 0.3);
    const f = p.isConverging ? p.focalLength : -p.focalLength;
    const di = PhysicsCalc.imageDistance(p.objectDistance, f);
    const mag = PhysicsCalc.magnification(p.objectDistance, di);
    const imageH = Math.abs(p.objectHeight * mag);
    return { ...s, time: t, progress, imageDist: di, imageH, mag,
             isVirtual: di < 0, isInverted: mag < 0 };
  },

  // ===== BROWNIAN MOTION =====
  initBrownian(p) {
    const positions = Array.from({ length: p.numberOfParticles }, () => [Math.random() * 2 - 1, Math.random() * 2 - 1]);
    const trails = positions.map(pos => [pos]);
    return { isRunning: true, time: 0, positions, trails };
  },
  updateBrownian(p, s) {
    if (!s.isRunning) return s;
    const t = s.time + DT;
    const scale = 1e5;
    const D = PhysicsCalc.brownianD(p.temperature, p.viscosity, p.particleRadius);
    const sigma = Math.sqrt(2 * D * DT) * scale;
    const positions = s.positions.map(([x, y]) => {
      const nx = Math.max(-1, Math.min(1, x + randGaussian() * sigma));
      const ny = Math.max(-1, Math.min(1, y + randGaussian() * sigma));
      return [nx, ny];
    });
    const trails = s.trails.map((trail, i) => [...trail, positions[i]].slice(-50));
    return { ...s, time: t, positions, trails };
  },

  // ===== GAS EXPANSION =====
  initGas(p) {
    const n = 30;
    const mols = Array.from({ length: n }, () => [Math.random() * 2 - 1, Math.random() * 2 - 1]);
    return { isRunning: true, time: 0, temp: p.initialTemperature, vol: p.initialVolume,
             pressure: p.pressure, mols, speeds: new Array(n).fill(0) };
  },
  updateGas(p, s) {
    if (!s.isRunning) return s;
    const t = s.time + DT;
    const progress = Math.min(1, t / 5);
    const temp = p.initialTemperature + (p.finalTemperature - p.initialTemperature) * progress;
    const vol = p.initialVolume * (temp / p.initialTemperature);
    const speed = PhysicsCalc.rmsSpeed(temp, 4.65e-26);
    const speedScale = speed / 500;
    const containerSize = Math.sqrt(vol / p.initialVolume);
    const mols = s.mols.map(([x, y]) => {
      const nx = Math.max(-containerSize, Math.min(containerSize, x + (Math.random() - 0.5) * speedScale * DT));
      const ny = Math.max(-containerSize, Math.min(containerSize, y + (Math.random() - 0.5) * speedScale * DT));
      return [nx, ny];
    });
    const speeds = new Array(mols.length).fill(speed);
    return { ...s, time: t, temp, vol, pressure: p.pressure, mols, speeds };
  },
};
