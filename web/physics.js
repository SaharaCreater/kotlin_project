// Physics Engine — pure calculation functions
const Physics = {
  G: 9.81,
  DT: 1 / 60,

  // Pendulum
  pendulumStep(theta, omega, L, damping, dt = Physics.DT) {
    const alpha = -(Physics.G / L) * Math.sin(theta) - damping * omega;
    const newOmega = omega + alpha * dt;
    const newTheta = theta + newOmega * dt;
    return { theta: newTheta, omega: newOmega };
  },
  pendulumPeriod(L) { return 2 * Math.PI * Math.sqrt(L / Physics.G); },

  // Free fall
  freeFallStep(y, vy, dt = Physics.DT) {
    const ay = -Physics.G;
    const newVy = vy + ay * dt;
    const newY = y + newVy * dt;
    return { y: newY, vy: newVy };
  },

  // Collision (1D elastic/inelastic)
  collisionVelocities(m1, m2, v1, v2, elastic = true) {
    if (elastic) {
      const v1f = ((m1 - m2) * v1 + 2 * m2 * v2) / (m1 + m2);
      const v2f = ((m2 - m1) * v2 + 2 * m1 * v1) / (m1 + m2);
      return { v1f, v2f };
    } else {
      const vf = (m1 * v1 + m2 * v2) / (m1 + m2);
      return { v1f: vf, v2f: vf };
    }
  },
  kineticEnergy: (m, v) => 0.5 * m * v * v,
  momentum: (m, v) => m * v,

  // Electricity
  ohmVoltage: (I, R) => I * R,
  ohmCurrent: (V, R) => V / R,
  seriesResistance: (rs) => rs.reduce((a, r) => a + r, 0),
  parallelResistance: (rs) => 1 / rs.reduce((s, r) => s + 1 / r, 0),
  power: (V, I) => V * I,

  // Optics — Snell's law
  snell(theta1, n1, n2) {
    const sinTheta2 = (n1 / n2) * Math.sin(theta1);
    if (Math.abs(sinTheta2) > 1) return null; // total internal reflection
    return Math.asin(sinTheta2);
  },
  criticalAngle: (n1, n2) => n1 > n2 ? Math.asin(n2 / n1) : null,

  // Lens (thin lens equation): 1/f = 1/do + 1/di
  lensImage(f, do_) {
    if (Math.abs(do_ - f) < 0.001) return { di: Infinity, m: Infinity, isReal: true };
    const di = (f * do_) / (do_ - f);
    const m = -di / do_;
    return { di, m, isReal: di > 0 };
  },

  // Thermodynamics
  idealGasVolume: (n, R, T, P) => (n * R * T) / P,
  R_GAS: 8.314,
  celsiusToKelvin: (C) => C + 273.15,

  // Brownian motion step
  brownianStep(x, y, kT, r, eta, dt = Physics.DT) {
    const D = kT / (6 * Math.PI * eta * r);
    const std = Math.sqrt(2 * D * dt);
    return {
      x: x + std * (Math.random() * 2 - 1) * Math.sqrt(3),
      y: y + std * (Math.random() * 2 - 1) * Math.sqrt(3),
    };
  },
  BOLTZMANN: 1.380649e-23,
};
