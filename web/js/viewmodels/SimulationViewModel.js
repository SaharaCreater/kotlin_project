// SimulationViewModel — MVI-style ViewModel for physics simulation
class SimulationViewModel {
  constructor() {
    this._state = {
      experimentId: null,
      params: {},
      simState: null,
      isRunning: false,
      showInfo: true,
      paramsOpen: false,
    };
    this._listeners = [];
    this._animFrame = null;
    this._recorded = false;
  }

  get state() { return { ...this._state }; }
  subscribe(fn) { this._listeners.push(fn); return () => { this._listeners = this._listeners.filter(l => l !== fn); }; }
  _emit() { this._listeners.forEach(fn => { try { fn(this.state); } catch(e) { console.warn('Listener error:', e); } }); }
  _setState(partial) { this._state = { ...this._state, ...partial }; this._emit(); }

  load(experimentId) {
    this.stop();
    this._recorded = false;
    this._setState({
      experimentId,
      params: { ...(SimulationViewModel.DEFAULTS[experimentId] || {}) },
      simState: null,
      isRunning: false,
      showInfo: true,
      paramsOpen: false,
    });
  }

  setParam(key, value) {
    const params = { ...this._state.params, [key]: value };
    this._setState({ params });
    if (this._state.simState) {
      const liveIds = ['ELECTRIC_CIRCUIT', 'MAGNETIC_FIELD', 'LIGHT_REFRACTION', 'LENS'];
      if (liveIds.includes(this._state.experimentId)) this._reinit();
    }
  }

  _reinit() {
    const s = this._initState();
    if (s) this._setState({ simState: { ...s, isRunning: this._state.simState?.isRunning || false } });
  }

  _initState() {
    const { experimentId: id, params: p } = this._state;
    try {
      switch (id) {
        case 'PENDULUM':         return SimEngine.initPendulum(p);
        case 'FREE_FALL':        return SimEngine.initFreeFall(p);
        case 'COLLISION':        return SimEngine.initCollision(p);
        case 'ELECTRIC_CIRCUIT': return SimEngine.initCircuit(p);
        case 'MAGNETIC_FIELD':   return SimEngine.initMagField(p);
        case 'LIGHT_REFRACTION': return SimEngine.initRefraction(p);
        case 'LENS':             return SimEngine.initLens(p);
        case 'BROWNIAN_MOTION':  return SimEngine.initBrownian(p);
        case 'GAS_EXPANSION':    return SimEngine.initGas(p);
        default: return null;
      }
    } catch (e) { console.error('initState error:', e); return null; }
  }

  start() {
    const s = this._initState();
    if (!s) return;
    this._setState({ simState: s, isRunning: true });
    this._loop();
  }

  stop() {
    if (this._animFrame) { cancelAnimationFrame(this._animFrame); this._animFrame = null; }
    if (this._state.isRunning) this._setState({ isRunning: false });
  }

  togglePlay() {
    if (!this._state.simState) { this.start(); return; }
    if (this._state.isRunning) { this.stop(); }
    else { this._setState({ isRunning: true }); this._loop(); }
  }

  reset() { this.stop(); this.start(); this._recorded = false; }
  toggleInfo() { this._setState({ showInfo: !this._state.showInfo }); }
  toggleParams() { this._setState({ paramsOpen: !this._state.paramsOpen }); }

  _loop() {
    if (!this._state.isRunning) return;
    const { experimentId: id, params: p, simState: s } = this._state;
    if (!s) return;

    try {
      let next;
      switch (id) {
        case 'PENDULUM':         next = SimEngine.updatePendulum(p, s); break;
        case 'FREE_FALL':
          next = SimEngine.updateFreeFall(p, s);
          if (next.hasLanded) { this._setState({ simState: next, isRunning: false }); return; }
          break;
        case 'COLLISION':        next = SimEngine.updateCollision(p, s); break;
        case 'ELECTRIC_CIRCUIT': next = SimEngine.updateCircuit(p, s); break;
        case 'MAGNETIC_FIELD':   next = SimEngine.updateMagField(p, s); break;
        case 'LIGHT_REFRACTION': next = SimEngine.updateRefraction(p, s); break;
        case 'LENS':             next = SimEngine.updateLens(p, s); break;
        case 'BROWNIAN_MOTION':  next = SimEngine.updateBrownian(p, s); break;
        case 'GAS_EXPANSION':    next = SimEngine.updateGas(p, s); break;
        default: return;
      }
      if (!next) return;
      this._state.simState = next;
      this._emit();
    } catch (e) {
      console.error('Simulation loop error:', e);
      this._state.isRunning = false;
      return;
    }

    this._animFrame = requestAnimationFrame(() => this._loop());
  }

  destroy() {
    this.stop();
    this._listeners = [];
  }
}

// Static defaults defined as a plain object (avoids static class field parsing issues)
SimulationViewModel.DEFAULTS = {
  PENDULUM:         { length: 1.0, initialAngle: 0.5, mass: 1.0, damping: 0.02 },
  FREE_FALL:        { initialHeight: 10, initialVelocity: 0, mass: 1.0, showTrail: true },
  COLLISION:        { mass1: 2, mass2: 1, velocity1: 5, velocity2: -3, isElastic: true },
  ELECTRIC_CIRCUIT: { voltage: 12, resistance1: 100, resistance2: 200, resistance3: 150, isParallel: false },
  MAGNETIC_FIELD:   { current: 5, wireLength: 1, showFieldLines: true, numberOfLines: 8 },
  LIGHT_REFRACTION: { incidentAngle: 0.5, n1: 1.0, n2: 1.5, showNormal: true },
  LENS:             { focalLength: 0.5, objectDistance: 1.0, objectHeight: 0.3, isConverging: true },
  BROWNIAN_MOTION:  { temperature: 300, particleRadius: 1e-6, viscosity: 0.001, numberOfParticles: 20 },
  GAS_EXPANSION:    { initialTemperature: 300, finalTemperature: 450, initialVolume: 1, moles: 1, pressure: 101325 },
};
// Keep backwards-compatible alias
SimulationViewModel.DEFAULT_PARAMS = SimulationViewModel.DEFAULTS;
