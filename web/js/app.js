// App — Main entry point, wires up MVVM and routing
const App = (() => {
  // ===== ViewModels (shared singletons) =====
  const authVm = new AuthViewModel();
  const expVm = new ExperimentsViewModel();

  // ===== Navigation config =====
  const NAV_ITEMS = [
    { path: '/home',        label: 'Главная',     icon: 'home'         },
    { path: '/experiments', label: 'Опыты',       icon: 'science'      },
    { path: '/scanner',     label: 'Сканер',      icon: 'qr_code_scanner' },
    { path: '/profile',     label: 'Профиль',     icon: 'person'       },
  ];

  // ===== Router =====
  let _router;

  function init() {
    _router = new Router([
      {
        path: '/',
        factory: () => ({ render: (c) => { navigate(AuthService.isLoggedIn() ? '/home' : '/login'); }, destroy: () => {} }),
        alwaysRefresh: true,
      },
      {
        path: '/login',
        factory: () => new LoginView(authVm),
      },
      {
        path: '/home',
        factory: () => { _requireAuth(); return new HomeView(expVm, authVm); },
        alwaysRefresh: true,
      },
      {
        path: '/experiments',
        factory: (p) => { _requireAuth(); return new ExperimentsView(expVm, p); },
        alwaysRefresh: true,
      },
      {
        path: '/experiment/:id',
        factory: (p) => { _requireAuth(); return new ExperimentDetailView(expVm, new SimulationViewModel(), p.id); },
        alwaysRefresh: true,
      },
      {
        path: '/scanner',
        factory: () => { _requireAuth(); return new ScannerView(); },
        alwaysRefresh: true,
      },
      {
        path: '/profile',
        factory: () => { _requireAuth(); return new ProfileView(authVm, expVm); },
        alwaysRefresh: true,
      },
    ]);

    _buildNav();
    _router.init(document.querySelector('#view-container'));

    // Show nav if authenticated
    authVm.subscribe(s => _syncNav(s));
    _syncNav(authVm.state);

    // Auth state changes
    if (!location.hash || location.hash === '#/') {
      navigate(AuthService.isLoggedIn() ? '/home' : '/login');
    }
  }

  function navigate(path) { _router.navigate(path); }

  function _requireAuth() {
    if (!AuthService.isLoggedIn()) {
      _router.navigate('/login', true);
      throw new Error('Not authenticated'); // caught by router._activate try-catch
    }
  }

  function showToast(msg, duration = 3000) {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), duration);
  }

  function _buildNav() {
    const railItems = document.getElementById('nav-rail-items');
    const bottomItems = document.getElementById('nav-bottom-items');

    NAV_ITEMS.forEach(item => {
      // Rail item
      const ri = document.createElement('button');
      ri.className = 'nav-item';
      ri.dataset.path = item.path;
      ri.innerHTML = `<div class="nav-indicator"><span class="material-symbols-rounded">${item.icon}</span></div>
                      <span class="nav-label">${item.label}</span>`;
      ri.onclick = () => navigate(item.path);
      railItems.appendChild(ri);

      // Bottom nav item
      const bi = document.createElement('button');
      bi.className = 'nav-item';
      bi.dataset.path = item.path;
      bi.innerHTML = `<div class="nav-indicator"><span class="material-symbols-rounded">${item.icon}</span></div>
                      <span class="nav-label">${item.label}</span>`;
      bi.onclick = () => navigate(item.path);
      bottomItems.appendChild(bi);
    });

    // Update active state on hash change
    const _navSync = () => { _syncNav(authVm.state); };
    window.addEventListener('hashchange', _navSync);
    window.addEventListener('popstate', _navSync);
  }

  function _updateNavActive() {
    const hash = location.hash.replace(/^#/, '') || '/';
    const currentBase = '/' + hash.split('/')[1];
    document.querySelectorAll('.nav-item[data-path]').forEach(el => {
      el.classList.toggle('active', el.dataset.path === currentBase);
    });
    // Update avatar
    const user = authVm.state.user;
    if (user) {
      const initials = user.name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
      document.querySelectorAll('.nav-avatar').forEach(a => {
        a.textContent = initials;
        a.style.background = user.avatar_color || '#6750A4';
      });
    }
  }

  function _syncNav(s) {
    const rail = document.getElementById('nav-rail');
    const bottom = document.getElementById('nav-bottom');
    const hash = location.hash.replace(/^#/, '') || '/';
    const isLoginPage = hash === '/login' || hash === '/';
    const show = !!s.user && !isLoginPage;
    if (rail) {
      rail.style.display = show ? 'flex' : 'none';
      rail.classList.toggle('nav-hidden', !show);
    }
    if (bottom) {
      bottom.style.display = show ? 'flex' : 'none';
      bottom.classList.toggle('nav-hidden', !show);
    }
    const main = document.getElementById('main-content');
    if (main) main.style.marginLeft = '';
    _updateNavActive();
  }

  // Close dialog on overlay click (already bound in ProfileView but as fallback)
  document.querySelector('#dialog-overlay')?.addEventListener('click', (e) => {
    if (e.target.id === 'dialog-overlay') document.querySelector('#dialog-overlay').style.display = 'none';
  });

  return { init, navigate, showToast };
})();

// Boot
document.addEventListener('DOMContentLoaded', () => App.init());
