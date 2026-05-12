// ExperimentDetailView — Fullscreen simulation with params, info, QR code
class ExperimentDetailView {
  constructor(expVm, simVm, id) {
    this.expVm = expVm;
    this.simVm = simVm;
    this.id = id;
    this._unsub = null;
    this._canvas = null;
    this._ctx = null;
    this._qrRendered = false;
    this._experiment = null;
  }

  async render(container) {
    // Load experiment data
    let exp;
    try {
      const data = await CacheService.cachedFetch('exp_' + this.id, () => ApiService.getExperiment(this.id));
      exp = new Experiment(data);
    } catch (e) {
      container.innerHTML = `<div class="page"><div class="empty-state"><span class="material-symbols-rounded">error</span><h3>Не найдено</h3><p>${e.message}</p></div></div>`;
      return;
    }
    this._experiment = exp;
    this.simVm.load(this.id);
    this._drawShell(container, exp);
    this._canvas = container.querySelector('#sim-canvas');
    this._ctx = this._canvas.getContext('2d');
    this._resizeCanvas();
    this._unsub = this.simVm.subscribe(s => this._onState(s, container));
    window.addEventListener('resize', () => this._resizeCanvas());
    this.simVm.start();
  }

  _drawShell(container, exp) {
    const catMeta = Experiment.CATEGORIES[exp.category] || {};
    container.innerHTML = `
      <div class="sim-screen" style="height:100vh">
        <!-- Canvas -->
        <div class="sim-canvas-wrap">
          <canvas id="sim-canvas"></canvas>

          <!-- Overlay -->
          <div class="sim-overlay">
            <!-- Top bar -->
            <div class="sim-topbar">
              <button class="icon-btn" onclick="App.navigate(-1)" title="Назад">
                <span class="material-symbols-rounded">arrow_back</span>
              </button>
              <div class="sim-title">${exp.name}</div>
              <button class="icon-btn" id="btn-info" title="Инфо">
                <span class="material-symbols-rounded">info</span>
              </button>
              <button class="icon-btn" id="btn-qr" title="QR код">
                <span class="material-symbols-rounded">qr_code</span>
              </button>
            </div>

            <!-- Running indicator -->
            <div id="running-dot" class="running-dot">
              <div class="running-dot-circle"></div>
              <span class="running-dot-label">Запущено</span>
            </div>

            <!-- Info panel -->
            <div id="info-panel" class="info-panel">
              <div class="info-panel-title">Параметры симуляции</div>
              <div id="info-rows"></div>
            </div>

            <!-- Controls -->
            <div class="sim-controls">
              <button class="icon-btn tonal" id="btn-reset" title="Сбросить">
                <span class="material-symbols-rounded">replay</span>
              </button>
              <button class="icon-btn play" id="btn-play" title="Пауза/Старт">
                <span class="material-symbols-rounded">pause</span>
              </button>
              <button class="icon-btn tonal" id="btn-params" title="Параметры">
                <span class="material-symbols-rounded">tune</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Params sheet -->
        <div class="params-sheet" id="params-sheet">
          <div class="params-handle-bar" id="params-handle">
            <div></div>
          </div>
          <div class="params-header">
            <span class="params-title">Параметры</span>
            <button class="btn btn-text" id="btn-apply" style="padding:6px 12px">Применить</button>
          </div>
          <div class="params-body" id="params-body">
            <div class="params-inner" id="params-inner">
              ${this._buildParamsHTML(this.id)}
            </div>
          </div>
        </div>
      </div>

      <!-- QR Dialog -->
      <div id="qr-dialog" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,.5);z-index:300;display:flex;align-items:center;justify-content:center">
        <div class="dialog" style="max-width:320px;text-align:center">
          <div class="dialog-title">QR-код эксперимента</div>
          <div class="dialog-content">Отсканируйте для быстрого доступа к «${exp.name}»</div>
          <div class="qr-container" style="padding:0 0 16px">
            <div class="qr-box" id="qr-box"></div>
            <p style="font:var(--md-body-small);color:var(--md-on-surface-variant)">${exp.name}</p>
          </div>
          <div class="dialog-actions">
            <button class="btn btn-tonal" id="qr-close">Закрыть</button>
          </div>
        </div>
      </div>`;

    this._bindEvents(container);
  }

  _bindEvents(container) {
    const simVm = this.simVm;

    container.querySelector('#btn-play').addEventListener('click', () => simVm.togglePlay());
    container.querySelector('#btn-reset').addEventListener('click', () => simVm.reset());
    container.querySelector('#btn-info').addEventListener('click', () => simVm.toggleInfo());

    // Params sheet
    const sheet = container.querySelector('#params-sheet');
    const body = container.querySelector('#params-body');
    container.querySelector('#btn-params').addEventListener('click', () => {
      simVm.toggleParams();
      body.classList.toggle('open', simVm.state.paramsOpen);
    });
    container.querySelector('#params-handle').addEventListener('click', () => {
      simVm.toggleParams();
      body.classList.toggle('open', simVm.state.paramsOpen);
    });
    container.querySelector('#btn-apply').addEventListener('click', () => {
      this._readParamInputs(container);
      simVm.reset();
      simVm._setState({ paramsOpen: false });
      body.classList.remove('open');
    });

    // QR
    container.querySelector('#btn-qr').addEventListener('click', () => {
      const dlg = container.querySelector('#qr-dialog');
      dlg.style.display = 'flex';
      if (!this._qrRendered) {
        const url = location.origin + '/#/experiment/' + this.id;
        try {
          new QRCode(container.querySelector('#qr-box'), { text: url, width: 200, height: 200, colorDark: '#1C1B1F' });
          this._qrRendered = true;
        } catch(e) { container.querySelector('#qr-box').innerHTML = `<p style="font-size:11px;word-break:break-all">${url}</p>`; }
      }
    });
    container.querySelector('#qr-close').addEventListener('click', () => {
      container.querySelector('#qr-dialog').style.display = 'none';
    });

    // Slider/input live updates
    container.querySelector('#params-inner').addEventListener('input', (e) => {
      const el = e.target;
      if (el.dataset.param) {
        const val = el.type === 'checkbox' ? el.checked : parseFloat(el.value) || el.value;
        // Update display
        const disp = container.querySelector(`[data-display="${el.dataset.param}"]`);
        if (disp) disp.textContent = this._fmtVal(el.dataset.param, val);
      }
    });
  }

  _readParamInputs(container) {
    container.querySelectorAll('[data-param]').forEach(el => {
      const val = el.type === 'checkbox' ? el.checked : parseFloat(el.value) || el.value;
      this.simVm.setParam(el.dataset.param, val);
    });
  }

  _onState(s, container) {
    if (!container.isConnected) return;

    // Sync play button
    const playBtn = container.querySelector('#btn-play .material-symbols-rounded');
    if (playBtn) playBtn.textContent = s.isRunning ? 'pause' : 'play_arrow';

    // Running dot
    const dot = container.querySelector('#running-dot');
    if (dot) dot.classList.toggle('show', s.isRunning);

    // Info panel
    const infoPanel = container.querySelector('#info-panel');
    if (infoPanel) {
      infoPanel.classList.toggle('visible', s.showInfo && !!s.simState);
      if (s.simState) {
        const rows = container.querySelector('#info-rows');
        if (rows) rows.innerHTML = this._buildInfoRows(s.simState);
      }
    }

    // Draw
    if (this._canvas && this._ctx && s.simState) {
      this._draw(s.simState);
    }

    // Record progress once
    if (s.isRunning && !this._recorded && AuthService.isLoggedIn()) {
      this._recorded = true;
      this.expVm.recordRun(this.id);
    }
  }

  _resizeCanvas() {
    if (!this._canvas) return;
    const wrap = this._canvas.parentElement;
    this._canvas.width = wrap.clientWidth;
    this._canvas.height = wrap.clientHeight;
  }

  _draw(state) {
    const ctx = this._ctx, W = this._canvas.width, H = this._canvas.height;
    SimRenderer.draw(ctx, W, H, this.id, state, this.simVm.state.params);
  }

  _buildInfoRows(state) {
    const rows = SimRenderer.getInfoRows(this.id, state);
    return rows.map(r => r.section
      ? `<div class="info-section">${r.label}</div>`
      : `<div class="info-row">
          <span class="info-label">${r.label}</span>
          <span class="info-value">${r.value}</span>
         </div>`).join('');
  }

  _fmtVal(key, val) {
    if (typeof val === 'boolean') return val ? 'Да' : 'Нет';
    if (typeof val === 'number') return val.toFixed(val < 1 ? 3 : 2);
    return String(val);
  }

  _buildParamsHTML(id) {
    const defs = SimRenderer.getParamDefs(id) || [];
    return defs.map(d => {
      const curVal = SimulationViewModel.DEFAULT_PARAMS[id]?.[d.key];
      if (d.type === 'bool') {
        return `<div class="param-toggle-row">
          <span class="param-row-label">${d.label}</span>
          <label class="md-switch"><input type="checkbox" data-param="${d.key}" ${curVal ? 'checked' : ''}>
            <span class="md-switch-track"><span class="md-switch-thumb"></span></span>
          </label></div>`;
      }
      if (d.section) return `<div class="param-section">${d.label}</div>`;
      const v = curVal !== undefined ? curVal : d.min;
      return `<div class="param-row">
        <div class="param-row-header">
          <span class="param-row-label">${d.label}</span>
          <span class="param-row-value" data-display="${d.key}">${this._fmtVal(d.key, v)} ${d.unit || ''}</span>
        </div>
        <input class="md-slider" type="range" data-param="${d.key}"
          min="${d.min}" max="${d.max}" step="${d.step || 0.01}" value="${v}"/>
      </div>`;
    }).join('');
  }

  destroy() {
    if (this._unsub) this._unsub();
    this.simVm.destroy();
    window.removeEventListener('resize', () => this._resizeCanvas());
  }
}
