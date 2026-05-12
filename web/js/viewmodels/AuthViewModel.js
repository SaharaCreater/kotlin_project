// AuthViewModel — MVVM ViewModel for authentication
class AuthViewModel {
  constructor() {
    this._state = {
      loading: false,
      error: null,
      user: AuthService.getUser(),
    };
    this._listeners = [];
  }

  get state() { return { ...this._state }; }

  subscribe(fn) {
    this._listeners.push(fn);
    return () => { this._listeners = this._listeners.filter(l => l !== fn); };
  }

  _emit() { this._listeners.forEach(fn => fn(this.state)); }

  _setState(partial) {
    this._state = { ...this._state, ...partial };
    this._emit();
  }

  async login(email, password) {
    if (!email || !password) { this._setState({ error: 'Заполните все поля' }); return false; }
    this._setState({ loading: true, error: null });
    try {
      const { token, user } = await ApiService.login(email, password);
      AuthService.setSession(token, user);
      await CacheService.clear();
      this._setState({ loading: false, user });
      return true;
    } catch (e) {
      this._setState({ loading: false, error: e.message });
      return false;
    }
  }

  async register(email, name, password) {
    if (!email || !name || !password) { this._setState({ error: 'Заполните все поля' }); return false; }
    if (password.length < 6) { this._setState({ error: 'Пароль минимум 6 символов' }); return false; }
    this._setState({ loading: true, error: null });
    try {
      const { token, user } = await ApiService.register(email, name, password);
      AuthService.setSession(token, user);
      this._setState({ loading: false, user });
      return true;
    } catch (e) {
      this._setState({ loading: false, error: e.message });
      return false;
    }
  }

  logout() {
    AuthService.clearSession();
    CacheService.clear();
    this._setState({ user: null, error: null });
  }

  clearError() { this._setState({ error: null }); }
}
