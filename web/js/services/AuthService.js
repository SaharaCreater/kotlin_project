// AuthService — manages JWT token and current user
const AuthService = (() => {
  const TOKEN_KEY = 'dopp_token';
  const USER_KEY = 'dopp_user';

  function getToken() { return localStorage.getItem(TOKEN_KEY); }
  function getUser() {
    try { return JSON.parse(localStorage.getItem(USER_KEY)); } catch { return null; }
  }
  function setSession(token, user) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }
  function clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }
  function isLoggedIn() { return !!getToken() && !!getUser(); }

  return { getToken, getUser, setSession, clearSession, isLoggedIn };
})();
