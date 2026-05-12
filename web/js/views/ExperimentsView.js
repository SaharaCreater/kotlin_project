// ExperimentsView — Filterable, searchable dynamic list (RecyclerView equivalent)
class ExperimentsView {
  constructor(vm, params) {
    this.vm = vm;
    this.params = params || {};
    this._unsub = null;
    this._listEl = null;
  }

  async render(container) {
    await this.vm.loadAll();
    if (this.params.cat) this.vm.setFilter(this.params.cat);
    this._draw(container);
    this._unsub = this.vm.subscribe(() => this._updateList());
  }

  _draw(container) {
    const s = this.vm.state;
    container.innerHTML = `
      <div class="top-app-bar">
        <button class="icon-btn" onclick="history.back()"><span class="material-symbols-rounded">arrow_back</span></button>
        <div class="top-app-bar-title">Эксперименты</div>
        <div class="top-app-bar-actions">
          <button class="icon-btn" id="search-toggle"><span class="material-symbols-rounded">search</span></button>
        </div>
      </div>
      <!-- Search bar (hidden by default) -->
      <div id="search-bar" style="display:none;padding:8px 16px;background:var(--md-surface)">
        <div class="text-field">
          <input id="search-inp" class="text-field-input" type="search" placeholder=" " style="padding-right:48px"/>
          <label class="text-field-label">Поиск эксперимента</label>
          <button class="text-field-suffix" id="search-clear"><span class="material-symbols-rounded" style="font-size:20px">close</span></button>
        </div>
      </div>
      <!-- Filter chips -->
      <div class="chips-row" id="chips-row">
        <div class="chip ${!s.filter ? 'selected' : ''}" data-cat="">
          <span class="material-symbols-rounded">apps</span>Все
        </div>
        ${(s.categories || []).map(c => `
          <div class="chip ${s.filter === c.id ? 'selected' : ''}" data-cat="${c.id}">
            ${c.name}
          </div>`).join('')}
      </div>
      <div class="divider"></div>
      <!-- Dynamic list container -->
      <div id="exp-list" class="page" style="padding-top:12px"></div>`;

    this._listEl = container.querySelector('#exp-list');
    this._renderList();
    this._bindEvents(container);
  }

  _bindEvents(container) {
    // Search toggle
    container.querySelector('#search-toggle').addEventListener('click', () => {
      const bar = container.querySelector('#search-bar');
      const isOpen = bar.style.display !== 'none';
      bar.style.display = isOpen ? 'none' : 'block';
      if (!isOpen) container.querySelector('#search-inp').focus();
      else { this.vm.setSearch(''); container.querySelector('#search-inp').value = ''; }
    });

    // Search input
    container.querySelector('#search-inp').addEventListener('input', (e) => {
      this.vm.setSearch(e.target.value);
    });
    container.querySelector('#search-clear').addEventListener('click', () => {
      container.querySelector('#search-inp').value = '';
      this.vm.setSearch('');
    });

    // Filter chips
    container.querySelector('#chips-row').addEventListener('click', (e) => {
      const chip = e.target.closest('.chip');
      if (!chip) return;
      const cat = chip.dataset.cat;
      this.vm.setFilter(cat || null);
      container.querySelectorAll('.chip').forEach(c => c.classList.remove('selected'));
      chip.classList.add('selected');
    });
  }

  _updateList() {
    if (!this._listEl) return;
    this._renderList();
  }

  _renderList() {
    const s = this.vm.state;
    if (s.loading) {
      this._listEl.innerHTML = `<div style="display:flex;flex-direction:column;gap:8px">
        ${Array(6).fill('<div class="skeleton" style="height:80px;border-radius:16px"></div>').join('')}
      </div>`;
      return;
    }

    const list = this.vm.filteredExperiments;
    if (!list.length) {
      this._listEl.innerHTML = `<div class="empty-state">
        <span class="material-symbols-rounded">search_off</span>
        <h3>Ничего не найдено</h3>
        <p>Попробуйте изменить фильтр или поисковый запрос</p>
      </div>`;
      return;
    }

    // Virtual-list style rendering — group by category
    const grouped = {};
    list.forEach(e => {
      if (!grouped[e.category]) grouped[e.category] = [];
      grouped[e.category].push(e);
    });

    let html = '';
    for (const [cat, exps] of Object.entries(grouped)) {
      const catMeta = Experiment.CATEGORIES[cat] || { name: cat, color: '#000', bgColor: '#eee' };
      const filtered = s.filter !== null;
      if (!filtered) {
        html += `<div style="display:flex;align-items:center;gap:8px;margin:16px 0 8px">
          <div style="width:32px;height:32px;border-radius:8px;background:${catMeta.bgColor};display:flex;align-items:center;justify-content:center">
            <span class="material-symbols-rounded" style="font-size:18px;color:${catMeta.color}">${Experiment.CATEGORIES[cat]?.icon||'science'}</span>
          </div>
          <span style="font:var(--md-title-small);color:${catMeta.color}">${catMeta.name}</span>
          <span style="font:var(--md-label-small);color:var(--md-on-surface-variant)">${exps.length} опытов</span>
        </div>`;
      }
      html += `<div style="display:flex;flex-direction:column;gap:8px;margin-bottom:8px">
        ${exps.map(e => this._cardHTML(e, s.progress)).join('')}
      </div>`;
    }
    this._listEl.innerHTML = html;

    // Bind card clicks
    this._listEl.querySelectorAll('[data-exp-id]').forEach(el => {
      el.addEventListener('click', () => App.navigate('/experiment/' + el.dataset.expId));
    });
  }

  _cardHTML(e, progress) {
    const prog = progress.find(p => p.experiment_id === e.id);
    const cat = Experiment.CATEGORIES[e.category] || { color: '#000', bgColor: '#eee' };
    return `
      <div class="exp-card" data-exp-id="${e.id}">
        <div class="exp-card-icon" style="background:${cat.bgColor}">
          <span class="material-symbols-rounded" style="color:${cat.color}">${e.icon}</span>
        </div>
        <div class="exp-card-body">
          <div class="exp-card-name">${e.name}</div>
          <div class="exp-card-desc">${e.description}</div>
          <div class="exp-card-meta">
            <span class="difficulty ${e.difficulty}">${e.difficultyLabel}</span>
            ${prog?.run_count ? `<span style="font:var(--md-label-small);color:var(--md-on-surface-variant)">
              <span class="material-symbols-rounded" style="font-size:12px">history</span> ${prog.run_count} раз${prog.run_count >= 5 ? '' : prog.run_count >= 2 ? 'а' : ''}
            </span>` : ''}
          </div>
        </div>
        <div class="exp-card-trail">
          ${prog?.completed
            ? '<span class="material-symbols-rounded" style="color:#2E7D32">check_circle</span>'
            : '<span class="material-symbols-rounded">chevron_right</span>'}
        </div>
      </div>`;
  }

  destroy() { if (this._unsub) this._unsub(); }
}
