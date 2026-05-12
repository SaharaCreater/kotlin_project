// LoginView — Auth screen (login + register tabs)
class LoginView {
  constructor(vm) { this.vm = vm; this._unsub = null; }

  render(container) {
    container.innerHTML = `
      <div class="auth-screen">
        <div class="auth-card">
          <div class="auth-logo">
            <span class="material-symbols-rounded">science</span>
            <h1>DoPP — Физика в AR</h1>
            <p>Интерактивные эксперименты по физике</p>
          </div>
          <div class="auth-tabs">
            <div class="auth-tab active" data-tab="login">Войти</div>
            <div class="auth-tab" data-tab="register">Регистрация</div>
          </div>
          <!-- Login Form -->
          <div id="login-form" class="auth-form">
            <div class="text-field">
              <input id="l-email" class="text-field-input" type="email" placeholder=" " autocomplete="email"/>
              <label class="text-field-label">Email</label>
            </div>
            <div class="text-field">
              <input id="l-pass" class="text-field-input" type="password" placeholder=" " autocomplete="current-password"/>
              <label class="text-field-label">Пароль</label>
              <button class="text-field-suffix" type="button" id="toggle-l-pass">
                <span class="material-symbols-rounded" style="font-size:20px">visibility</span>
              </button>
            </div>
            <div id="login-error" class="text-field-error" style="display:none"></div>
            <button class="btn btn-filled" id="login-btn" style="width:100%;justify-content:center;margin-top:8px">
              <span class="material-symbols-rounded">login</span>Войти
            </button>
          </div>
          <!-- Register Form -->
          <div id="register-form" class="auth-form" style="display:none">
            <div class="text-field">
              <input id="r-name" class="text-field-input" type="text" placeholder=" " autocomplete="name"/>
              <label class="text-field-label">Имя</label>
            </div>
            <div class="text-field">
              <input id="r-email" class="text-field-input" type="email" placeholder=" " autocomplete="email"/>
              <label class="text-field-label">Email</label>
            </div>
            <div class="text-field">
              <input id="r-pass" class="text-field-input" type="password" placeholder=" " autocomplete="new-password"/>
              <label class="text-field-label">Пароль (мин. 6 символов)</label>
              <button class="text-field-suffix" type="button" id="toggle-r-pass">
                <span class="material-symbols-rounded" style="font-size:20px">visibility</span>
              </button>
            </div>
            <div id="register-error" class="text-field-error" style="display:none"></div>
            <button class="btn btn-filled" id="register-btn" style="width:100%;justify-content:center;margin-top:8px">
              <span class="material-symbols-rounded">person_add</span>Создать аккаунт
            </button>
          </div>
        </div>
      </div>`;

    this._bindEvents(container);
    this._unsub = this.vm.subscribe(s => this._onState(s, container));
  }

  _bindEvents(c) {
    // Tabs
    c.querySelectorAll('.auth-tab').forEach(tab => {
      tab.addEventListener('click', () => {
        c.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        const isLogin = tab.dataset.tab === 'login';
        c.querySelector('#login-form').style.display = isLogin ? 'flex' : 'none';
        c.querySelector('#register-form').style.display = isLogin ? 'none' : 'flex';
        this.vm.clearError();
      });
    });

    // Password toggle
    const togglePass = (btnId, inputId) => {
      c.querySelector('#' + btnId)?.addEventListener('click', () => {
        const inp = c.querySelector('#' + inputId);
        const btn = c.querySelector('#' + btnId + ' .material-symbols-rounded');
        if (inp.type === 'password') { inp.type = 'text'; btn.textContent = 'visibility_off'; }
        else { inp.type = 'password'; btn.textContent = 'visibility'; }
      });
    };
    togglePass('toggle-l-pass', 'l-pass');
    togglePass('toggle-r-pass', 'r-pass');

    // Login
    c.querySelector('#login-btn').addEventListener('click', async () => {
      const email = c.querySelector('#l-email').value.trim();
      const pass = c.querySelector('#l-pass').value;
      const ok = await this.vm.login(email, pass);
      if (ok) App.navigate('/home');
    });
    c.querySelector('#l-pass').addEventListener('keydown', (e) => {
      if (e.key === 'Enter') c.querySelector('#login-btn').click();
    });

    // Register
    c.querySelector('#register-btn').addEventListener('click', async () => {
      const name = c.querySelector('#r-name').value.trim();
      const email = c.querySelector('#r-email').value.trim();
      const pass = c.querySelector('#r-pass').value;
      const ok = await this.vm.register(email, name, pass);
      if (ok) App.navigate('/home');
    });
  }

  _onState(s, c) {
    const loginBtn = c.querySelector('#login-btn');
    const regBtn = c.querySelector('#register-btn');
    if (loginBtn) {
      loginBtn.disabled = s.loading;
      loginBtn.innerHTML = s.loading
        ? '<span class="material-symbols-rounded" style="animation:spin 1s linear infinite">refresh</span>Загрузка...'
        : '<span class="material-symbols-rounded">login</span>Войти';
    }
    if (regBtn) {
      regBtn.disabled = s.loading;
      regBtn.innerHTML = s.loading
        ? '<span class="material-symbols-rounded" style="animation:spin 1s linear infinite">refresh</span>Загрузка...'
        : '<span class="material-symbols-rounded">person_add</span>Создать аккаунт';
    }
    const lerr = c.querySelector('#login-error');
    const rerr = c.querySelector('#register-error');
    const isRegisterTab = c.querySelector('.auth-tab[data-tab="register"]')?.classList.contains('active');
    if (s.error) {
      if (isRegisterTab && rerr) { rerr.textContent = s.error; rerr.style.display = 'block'; }
      else if (lerr) { lerr.textContent = s.error; lerr.style.display = 'block'; }
    } else {
      if (lerr) lerr.style.display = 'none';
      if (rerr) rerr.style.display = 'none';
    }
  }

  destroy() { if (this._unsub) this._unsub(); }
}
