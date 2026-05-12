const GRAVITY = 9.81;
const BOLTZMANN = 1.380649e-23;
const PI = Math.PI;

const PhysicsCalc = {
  pendulumAngle(theta0, L, t, damping = 0.02) {
    const omega = Math.sqrt(GRAVITY / L);
    return theta0 * Math.cos(omega * t) * Math.exp(-damping * t);
  },
  pendulumAngVel(theta0, L, t, damping = 0.02) {
    const omega = Math.sqrt(GRAVITY / L);
    return -theta0 * omega * Math.sin(omega * t) * Math.exp(-damping * t);
  },
  pendulumPeriod(L) { return 2 * PI * Math.sqrt(L / GRAVITY); },

  freeFallPos(h0, v0, t) { return h0 + v0 * t - 0.5 * GRAVITY * t * t; },
  freeFallVel(v0, t) { return v0 - GRAVITY * t; },
  fallTime(h) { return Math.sqrt(2 * h / GRAVITY); },

  elasticCollision(m1, v1, m2, v2) {
    const M = m1 + m2;
    return [(((m1 - m2) / M) * v1 + (2 * m2 / M) * v2),
            ((2 * m1 / M) * v1 + ((m2 - m1) / M) * v2)];
  },
  inelasticCollision(m1, v1, m2, v2) { return (m1 * v1 + m2 * v2) / (m1 + m2); },
  kineticEnergy(m, v) { return 0.5 * m * v * v; },
  momentum(m, v) { return m * v; },

  seriesR(...rs) { return rs.reduce((a, b) => a + b, 0); },
  parallelR(...rs) {
    const s = rs.reduce((a, r) => a + (r > 0 ? 1 / r : 0), 0);
    return s > 0 ? 1 / s : 0;
  },
  ohmsI(V, R) { return R > 0 ? V / R : 0; },
  ohmsV(I, R) { return I * R; },
  power(V, I) { return V * I; },

  magneticB(I, r) {
    const mu0 = 4 * PI * 1e-7;
    return r > 0 ? mu0 * I / (2 * PI * r) : 0;
  },

  snellAngle(theta1, n1, n2) {
    const ratio = (n1 / n2) * Math.sin(theta1);
    return Math.abs(ratio) <= 1 ? Math.asin(ratio) : null;
  },
  criticalAngle(n1, n2) { return n1 > n2 ? Math.asin(n2 / n1) : null; },

  imageDistance(do_, f) {
    if (do_ === f) return Infinity;
    return (f * do_) / (do_ - f);
  },
  magnification(do_, di) { return do_ !== 0 ? -di / do_ : 0; },

  idealGasP(n, T, V) { return V > 0 ? n * 8.314 * T / V : 0; },
  rmsSpeed(T, m) { return Math.sqrt(3 * BOLTZMANN * T / m); },
  brownianD(T, eta, r) { return BOLTZMANN * T / (6 * PI * eta * r); },
};

function randGaussian() {
  let u = 0, v = 0;
  while (u === 0) u = Math.random();
  while (v === 0) v = Math.random();
  return Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * PI * v);
}
