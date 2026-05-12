// ScannerView — QR Code Scanner using html5-qrcode
class ScannerView {
  constructor() { this._scanner = null; }

  render(container) {
    container.innerHTML = `
      <div class="top-app-bar">
        <button class="icon-btn" onclick="history.back()"><span class="material-symbols-rounded">arrow_back</span></button>
        <div class="top-app-bar-title">Сканер QR</div>
      </div>
      <div class="page" style="display:flex;flex-direction:column;align-items:center;gap:20px;padding-top:24px">
        <div style="text-align:center;margin-bottom:8px">
          <span class="material-symbols-rounded" style="font-size:48px;color:var(--md-primary)">qr_code_scanner</span>
          <h2 style="font:var(--md-headline-small);margin-top:8px">Сканировать QR-код</h2>
          <p style="font:var(--md-body-medium);color:var(--md-on-surface-variant);margin-top:4px">Наведите камеру на QR-код эксперимента</p>
        </div>

        <!-- Scanner -->
        <div style="width:100%;max-width:400px">
          <div class="scanner-viewfinder card-elevated" id="scanner-container" style="border-radius:16px;overflow:hidden;min-height:300px;background:#000">
            <div id="reader"></div>
          </div>
          <div class="scanner-corner tl"></div>
          <div class="scanner-corner tr"></div>
          <div class="scanner-corner bl"></div>
          <div class="scanner-corner br"></div>
        </div>

        <!-- Status -->
        <div id="scan-status" style="text-align:center;color:var(--md-on-surface-variant);font:var(--md-body-medium)">
          Инициализация камеры...
        </div>

        <!-- Result -->
        <div id="scan-result" style="display:none;width:100%;max-width:400px">
          <div class="card card-elevated" style="padding:20px;text-align:center">
            <span class="material-symbols-rounded" style="font-size:40px;color:#2E7D32">check_circle</span>
            <h3 style="font:var(--md-title-medium);margin:12px 0 8px" id="result-name"></h3>
            <p style="font:var(--md-body-medium);color:var(--md-on-surface-variant);margin-bottom:16px" id="result-desc"></p>
            <button class="btn btn-filled" id="result-open" style="width:100%;justify-content:center">
              <span class="material-symbols-rounded">science</span>Открыть эксперимент
            </button>
            <button class="btn btn-outlined" id="scan-again" style="width:100%;justify-content:center;margin-top:8px">
              <span class="material-symbols-rounded">qr_code_scanner</span>Сканировать снова
            </button>
          </div>
        </div>

        <!-- Manual entry -->
        <div style="width:100%;max-width:400px">
          <div class="divider" style="margin:8px 0"></div>
          <p style="font:var(--md-body-medium);color:var(--md-on-surface-variant);text-align:center;margin-bottom:12px">или выберите эксперимент вручную</p>
          <button class="btn btn-tonal" onclick="App.navigate('/experiments')" style="width:100%;justify-content:center">
            <span class="material-symbols-rounded">list</span>Список экспериментов
          </button>
        </div>
      </div>`;

    this._startScanner(container);
  }

  async _startScanner(container) {
    const statusEl = container.querySelector('#scan-status');
    if (typeof Html5Qrcode === 'undefined') {
      if (statusEl) statusEl.textContent = 'Библиотека QR не загружена. Обновите страницу.';
      return;
    }
    try {
      this._scanner = new Html5Qrcode('reader');
      const devices = await Html5Qrcode.getCameras();
      if (!devices || !devices.length) {
        if (statusEl) statusEl.innerHTML = '<span class="material-symbols-rounded" style="color:var(--md-error);vertical-align:middle">videocam_off</span> Камера недоступна';
        return;
      }
      const cameraId = devices[devices.length - 1].id; // prefer back camera
      if (statusEl) statusEl.textContent = 'Ожидание QR-кода...';

      await this._scanner.start(cameraId, { fps: 10, qrbox: 250 }, (text) => {
        this._onResult(text, container);
      }, () => {});
    } catch (e) {
      if (statusEl) statusEl.innerHTML = `<span class="material-symbols-rounded" style="color:var(--md-error);vertical-align:middle">error</span> ${e.message || 'Ошибка камеры'}`;
    }
  }

  async _onResult(text, container) {
    try {
      if (this._scanner) { await this._scanner.stop(); this._scanner = null; }
    } catch {}

    // Parse experiment ID from URL
    let expId = null;
    try {
      const url = new URL(text);
      const hash = url.hash; // /#/experiment/PENDULUM
      const match = hash.match(/\/experiment\/([^/?#]+)/);
      if (match) expId = match[1];
    } catch {
      // maybe it's just an experiment ID
      expId = text.trim();
    }

    const statusEl = container.querySelector('#scan-status');
    const resultEl = container.querySelector('#scan-result');

    if (!expId) {
      if (statusEl) statusEl.innerHTML = `<span style="color:var(--md-error)">QR-код не распознан как эксперимент</span>`;
      return;
    }

    try {
      const data = await ApiService.getExperiment(expId);
      if (statusEl) statusEl.style.display = 'none';
      if (resultEl) {
        resultEl.style.display = 'block';
        container.querySelector('#result-name').textContent = data.name;
        container.querySelector('#result-desc').textContent = data.description;
        container.querySelector('#result-open').onclick = () => App.navigate('/experiment/' + expId);
        container.querySelector('#scan-again').onclick = () => { this.destroy(); this.render(container); };
      }
    } catch {
      if (statusEl) statusEl.innerHTML = `<span style="color:var(--md-error)">Эксперимент не найден: ${expId}</span>`;
    }
  }

  destroy() {
    if (this._scanner) {
      this._scanner.stop().catch(() => {});
      this._scanner = null;
    }
  }
}
