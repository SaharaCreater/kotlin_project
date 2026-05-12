// Router — hash-based client-side navigation
class Router {
  constructor(routes) {
    this._routes = routes;
    this._current = null;
    this._container = null;
    this._view = null;
    window.addEventListener('hashchange', () => this._handleRoute());
    window.addEventListener('popstate', () => this._handleRoute());
  }

  init(container) {
    this._container = container;
    this._handleRoute();
  }

  navigate(path, replace = false) {
    if (path === -1) { history.back(); return; }
    // Preserve hash routing
    const newHash = '#' + path;
    if (replace) history.replaceState(null, '', newHash);
    else history.pushState(null, '', newHash);
    this._handleRoute();
  }

  _handleRoute() {
    const hash = location.hash.replace(/^#/, '') || '/';
    const path = hash.split('?')[0];
    const queryStr = hash.includes('?') ? hash.split('?')[1] : '';
    const params = Object.fromEntries(new URLSearchParams(queryStr));

    // Match route
    for (const route of this._routes) {
      const match = this._matchPath(route.path, path);
      if (match !== null) {
        this._activate(route, { ...match, ...params });
        return;
      }
    }
    // Fallback
    this._activate(this._routes.find(r => r.path === '/404') || this._routes[0], params);
  }

  _matchPath(pattern, path) {
    const patParts = pattern.split('/');
    const pathParts = path.split('/');
    if (patParts.length !== pathParts.length) return null;
    const params = {};
    for (let i = 0; i < patParts.length; i++) {
      if (patParts[i].startsWith(':')) params[patParts[i].slice(1)] = decodeURIComponent(pathParts[i]);
      else if (patParts[i] !== pathParts[i]) return null;
    }
    return params;
  }

  _activate(route, params) {
    if (this._current === route.path && !route.alwaysRefresh) return;
    this._current = route.path;

    // Destroy current view
    if (this._view?.destroy) this._view.destroy();
    this._view = null;

    // Scroll to top
    if (this._container) this._container.scrollTop = 0;

    // Create and render new view
    const view = route.factory(params);
    this._view = view;
    view.render(this._container, params);
  }
}
