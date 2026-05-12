// HomeView — Dashboard with hero, categories, recent experiments
class HomeView {
  constructor(expVm, authVm) { this.expVm = expVm; this.authVm = authVm; this._unsub = null; }

  async render(container) {
    container.innerHTML = `<div class="loading"><svg class="circular-progress" viewBox="25 25 50 50"><circle cx="50" cy="50" r="20" fill="none" stroke="var(--md-primary)" stroke-width="4" stroke-dasharray="31.4 94.2"/></svg></div>`;
    await this.expVm.loadAll();
    this._draw(container);
    this._unsub = this.expVm.subscribe(() => this._draw(container));
  }

  _draw(container) {
    const s = this.expVm.state;
    const user = this.authVm.state.user;
    const doneCount = s.progress.filter(p => p.completed).length;
    const totalCount = s.experiments.length;

    container.innerHTML = `
      <div class="top-app-bar">
        <div class="top-app-bar-title">Главная</div>
        <div class="top-app-bar-actions">
          <button class="icon-btn" title="Сканировать QR" onclick="App.navigate('/scanner')">
            <span class="material-symbols-rounded">qr_code_scanner</span>
          </button>
        </div>
      </div>
      <div class="page">
        <!-- Hero -->
        <div class="hero" style="margin-bottom:20px">
          <div class="hero-icon material-symbols-rounded">science</div>
          <div class="hero-title">Добро пожаловать${user ? ', ' + user.name.split(' ')[0] : ''}!</div>
          <div class="hero-sub">Исследуйте физику через интерактивные симуляции и AR-эксперименты</div>
          ${user ? `<div style="display:flex;align-items:center;gap:8px;margin-bottom:16px">
            <div class="linear-progress" style="flex:1">
              <div class="linear-progress-bar" style="width:${totalCount ? (doneCount/totalCount*100) : 0}%"></div>
            </div>
            <span style="font:var(--md-label-medium);color:rgba(255,255,255,.9)">${doneCount}/${totalCount}</span>
          </div>` : ''}
          <div style="display:flex;gap:8px;flex-wrap:wrap">
            <button class="btn btn-tonal" style="background:rgba(255,255,255,.2);color:#fff;border:1px solid rgba(255,255,255,.4)" onclick="App.navigate('/experiments')">
              <span class="material-symbols-rounded">explore</span>Все опыты
            </button>
            <button class="btn btn-tonal" style="background:rgba(255,255,255,.15);color:#fff;border:1px solid rgba(255,255,255,.3)" onclick="App.navigate('/scanner')">
              <span class="material-symbols-rounded">qr_code_scanner</span>Сканировать QR
            </button>
          </div>
        </div>

        <!-- Categories -->
        <h2 style="font:var(--md-title-large);margin-bottom:12px">Разделы физики</h2>
        ${s.loading ? this._skelGrid() : this._renderCats(s.categories)}

        <!-- Recent / All Experiments -->
        <div style="display:flex;align-items:center;justify-content:space-between;margin:20px 0 12px">
          <h2 style="font:var(--md-title-large)">Эксперименты</h2>
          <button class="btn btn-text" onclick="App.navigate('/experiments')">Все</button>
        </div>
        ${s.loading ? this._skelList() : this._renderExpList(s.experiments.slice(0, 6), s.progress)}
        ${s.error ? `<div class="empty-state"><span class="material-symbols-rounded">wifi_off</span><h3>Нет соединения</h3><p>${s.error}</p></div>` : ''}
      </div>`;
  }

  _renderCats(cats) {
    if (!cats.length) return '<div class="empty-state"><p>Загрузка...</p></div>';
    const colors = ['#1565C0','#E65100','#6A1B9A','#BF360C'];
    const bgs = ['#E3F2FD','#FFF3E0','#F3E5F5','#FBE9E7'];
    const icons = ['settings','bolt','lightbulb','local_fire_department'];
    return `<div class="grid-2" style="gap:10px;margin-bottom:8px">
      ${cats.map((c, i) => `
        <div class="cat-card" style="background:${bgs[i]||bgs[0]};color:${colors[i]||colors[0]}" onclick="App.navigate('/experiments?cat=${c.id}')">
          <div class="cat-card-icon"><span class="material-symbols-rounded" style="color:${colors[i]||colors[0]}">${icons[i]||'science'}</span></div>
          <div class="cat-card-name">${c.name}</div>
          <div class="cat-card-count">${c.count} опытов</div>
        </div>`).join('')}
    </div>`;
  }

  _renderExpList(exps, progress) {
    if (!exps.length) return '<div class="empty-state"><span class="material-symbols-rounded">science</span><p>Нет экспериментов</p></div>';
    return `<div style="display:flex;flex-direction:column;gap:8px">
      ${exps.map(e => {
        const prog = progress.find(p => p.experiment_id === e.id);
        const cat = Experiment.CATEGORIES[e.category] || Experiment.CATEGORIES.MECHANICS;
        return `
          <div class="exp-card" onclick="App.navigate('/experiment/${e.id}')">
            <div class="exp-card-icon" style="background:${cat.bgColor}">
              <span class="material-symbols-rounded" style="color:${cat.color}">${e.icon}</span>
            </div>
            <div class="exp-card-body">
              <div class="exp-card-name">${e.name}</div>
              <div class="exp-card-desc">${e.description}</div>
              <div class="exp-card-meta">
                <span class="difficulty ${e.difficulty}">${e.difficultyLabel}</span>
                <span style="font:var(--md-label-small);color:var(--md-on-surface-variant)">${cat.name}</span>
              </div>
            </div>
            <div class="exp-card-trail">
              ${prog?.completed
                ? '<span class="material-symbols-rounded" style="color:#2E7D32">check_circle</span>'
                : '<span class="material-symbols-rounded">chevron_right</span>'}
            </div>
          </div>`;
      }).join('')}
    </div>`;
  }

  _skelGrid() {
    return `<div class="grid-2" style="gap:10px;margin-bottom:8px">${Array(4).fill('<div class="skeleton" style="height:100px;border-radius:16px"></div>').join('')}</div>`;
  }
  _skelList() {
    return `<div style="display:flex;flex-direction:column;gap:8px">${Array(4).fill('<div class="skeleton" style="height:72px;border-radius:16px"></div>').join('')}</div>`;
  }

  destroy() { if (this._unsub) this._unsub(); }
}
