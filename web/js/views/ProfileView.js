// ProfileView — User profile, stats, progress, logout
class ProfileView {
  constructor(authVm, expVm) {
    this.authVm = authVm;
    this.expVm = expVm;
    this._unsub = null;
  }

  async render(container) {
    if (!AuthService.isLoggedIn()) { App.navigate('/login'); return; }
    await this.expVm.loadAll();
    this._draw(container);
    this._unsub = this.expVm.subscribe(() => this._draw(container));
  }

  _draw(container) {
    const user = this.authVm.state.user;
    const s = this.expVm.state;
    const progress = s.progress;
    const experiments = s.experiments;

    const completedCount = progress.filter(p => p.completed).length;
    const totalRuns = progress.reduce((sum, p) => sum + (p.run_count || 0), 0);
    const totalCount = experiments.length;
    const pct = totalCount ? Math.round(completedCount / totalCount * 100) : 0;

    // Stats by category
    const catStats = {};
    experiments.forEach(e => {
      if (!catStats[e.category]) catStats[e.category] = { total: 0, done: 0, name: Experiment.CATEGORIES[e.category]?.name || e.category, color: Experiment.CATEGORIES[e.category]?.color || '#000' };
      catStats[e.category].total++;
      if (progress.find(p => p.experiment_id === e.id && p.completed)) catStats[e.category].done++;
    });

    const initials = user ? (user.name || '?').split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase() : '?';

    container.innerHTML = `
      <div class="top-app-bar">
        <div class="top-app-bar-title">Профиль</div>
        <div class="top-app-bar-actions">
          <button class="icon-btn" id="edit-btn" title="Редактировать">
            <span class="material-symbols-rounded">edit</span>
          </button>
        </div>
      </div>

      <!-- Profile header -->
      <div class="profile-header">
        <div class="profile-avatar" style="background:${user?.avatar_color || '#6750A4'}">${initials}</div>
        <div class="profile-name">${user?.name || '—'}</div>
        <div class="profile-email">${user?.email || ''}</div>
        <div style="margin-top:12px;font:var(--md-body-small);color:var(--md-on-primary-container);opacity:.8">
          Участник с ${user?.created_at ? new Date(user.created_at).toLocaleDateString('ru') : '...'}
        </div>
      </div>

      <div class="page" style="padding-top:16px">
        <!-- Progress summary -->
        <div class="card card-outlined" style="padding:20px;margin-bottom:16px">
          <h3 style="font:var(--md-title-medium);margin-bottom:16px">Общий прогресс</h3>
          <div style="display:flex;gap:16px;margin-bottom:16px">
            <div style="flex:1;text-align:center">
              <div style="font:var(--md-display-small);color:var(--md-primary)">${pct}%</div>
              <div style="font:var(--md-body-small);color:var(--md-on-surface-variant)">выполнено</div>
            </div>
            <div style="flex:1;text-align:center">
              <div style="font:var(--md-display-small);color:var(--md-primary)">${completedCount}</div>
              <div style="font:var(--md-body-small);color:var(--md-on-surface-variant)">опытов</div>
            </div>
            <div style="flex:1;text-align:center">
              <div style="font:var(--md-display-small);color:var(--md-primary)">${totalRuns}</div>
              <div style="font:var(--md-body-small);color:var(--md-on-surface-variant)">запусков</div>
            </div>
          </div>
          <div class="linear-progress" style="height:8px;border-radius:4px">
            <div class="linear-progress-bar" style="width:${pct}%;border-radius:4px;transition:width .8s ease"></div>
          </div>
          <div style="text-align:right;margin-top:6px;font:var(--md-label-small);color:var(--md-on-surface-variant)">${completedCount} из ${totalCount}</div>
        </div>

        <!-- Category progress -->
        <h3 style="font:var(--md-title-medium);margin-bottom:12px">По разделам</h3>
        <div style="display:flex;flex-direction:column;gap:10px;margin-bottom:16px">
          ${Object.entries(catStats).map(([, c]) => {
            const cpct = c.total ? Math.round(c.done / c.total * 100) : 0;
            return `<div class="card card-outlined" style="padding:14px">
              <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
                <span style="font:var(--md-label-large);color:${c.color}">${c.name}</span>
                <span style="font:var(--md-label-medium);color:var(--md-on-surface-variant)">${c.done}/${c.total}</span>
              </div>
              <div class="linear-progress" style="height:6px">
                <div class="linear-progress-bar" style="width:${cpct}%;background:${c.color}"></div>
              </div>
            </div>`;
          }).join('')}
        </div>

        <!-- Recent activity -->
        ${progress.length ? `
        <h3 style="font:var(--md-title-medium);margin-bottom:12px">Недавние опыты</h3>
        <div style="display:flex;flex-direction:column;gap:4px;margin-bottom:16px">
          ${progress.sort((a,b) => (b.last_run_at||0) - (a.last_run_at||0)).slice(0,5).map(p => {
            const exp = s.experiments.find(e => e.id === p.experiment_id);
            if (!exp) return '';
            const cat = Experiment.CATEGORIES[exp.category] || {};
            return `<div class="list-item" onclick="App.navigate('/experiment/${exp.id}')">
              <div class="list-item-lead" style="background:${cat.bgColor||'#eee'}">
                <span class="material-symbols-rounded" style="font-size:20px;color:${cat.color||'#000'}">${exp.icon}</span>
              </div>
              <div class="list-item-content">
                <div class="list-item-headline">${exp.name}</div>
                <div class="list-item-support">${p.run_count} запус. · ${p.last_run_at ? new Date(p.last_run_at).toLocaleDateString('ru') : ''}</div>
              </div>
              <div class="list-item-trail"><span class="material-symbols-rounded">chevron_right</span></div>
            </div>`;
          }).join('')}
        </div>` : ''}

        <!-- Danger zone -->
        <div class="card card-outlined" style="padding:16px;margin-bottom:24px;border-color:var(--md-error-container)">
          <h4 style="font:var(--md-title-small);color:var(--md-error);margin-bottom:8px">Выйти из аккаунта</h4>
          <p style="font:var(--md-body-small);color:var(--md-on-surface-variant);margin-bottom:12px">Вы можете снова войти в любое время</p>
          <button class="btn btn-outlined" id="logout-btn" style="border-color:var(--md-error);color:var(--md-error)">
            <span class="material-symbols-rounded">logout</span>Выйти
          </button>
        </div>
      </div>`;

    container.querySelector('#logout-btn').addEventListener('click', () => {
      this.authVm.logout();
      App.navigate('/login');
    });

    container.querySelector('#edit-btn').addEventListener('click', () => {
      this._showEditDialog(container, user);
    });
  }

  _showEditDialog(container, user) {
    const overlay = document.querySelector('#dialog-overlay');
    const dlg = document.querySelector('#dialog');
    dlg.innerHTML = `
      <div class="dialog-title">Редактировать профиль</div>
      <div class="text-field" style="margin-bottom:16px">
        <input id="edit-name" class="text-field-input" type="text" placeholder=" " value="${user?.name || ''}"/>
        <label class="text-field-label">Имя</label>
      </div>
      <div id="edit-error" class="text-field-error" style="display:none;margin-bottom:12px"></div>
      <div class="dialog-actions">
        <button class="btn btn-text" id="edit-cancel">Отмена</button>
        <button class="btn btn-filled" id="edit-save">Сохранить</button>
      </div>`;
    overlay.style.display = 'flex';

    dlg.querySelector('#edit-cancel').onclick = () => { overlay.style.display = 'none'; };
    dlg.querySelector('#edit-save').onclick = async () => {
      const name = dlg.querySelector('#edit-name').value.trim();
      if (!name) { dlg.querySelector('#edit-error').textContent = 'Введите имя'; dlg.querySelector('#edit-error').style.display = 'block'; return; }
      try {
        const updated = await ApiService.updateProfile(name);
        AuthService.setSession(AuthService.getToken(), { ...AuthService.getUser(), ...updated });
        overlay.style.display = 'none';
        this.authVm._setState({ user: updated });
        App.showToast('Профиль обновлён');
        this._draw(container);
      } catch (e) {
        dlg.querySelector('#edit-error').textContent = e.message;
        dlg.querySelector('#edit-error').style.display = 'block';
      }
    };
    overlay.onclick = (e) => { if (e.target === overlay) overlay.style.display = 'none'; };
  }

  destroy() { if (this._unsub) this._unsub(); }
}
