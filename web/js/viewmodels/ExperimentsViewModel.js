// ExperimentsViewModel — MVVM ViewModel for experiment list and categories
class ExperimentsViewModel {
  constructor() {
    this._state = {
      experiments: [],
      categories: [],
      progress: [],
      loading: false,
      error: null,
      filter: null, // category id
      searchQuery: '',
    };
    this._listeners = [];
  }

  get state() { return { ...this._state }; }
  get filteredExperiments() {
    let list = this._state.experiments;
    if (this._state.filter) list = list.filter(e => e.category === this._state.filter);
    if (this._state.searchQuery) {
      const q = this._state.searchQuery.toLowerCase();
      list = list.filter(e => e.name.toLowerCase().includes(q) || e.description.toLowerCase().includes(q));
    }
    return list;
  }

  subscribe(fn) {
    this._listeners.push(fn);
    return () => { this._listeners = this._listeners.filter(l => l !== fn); };
  }
  _emit() { this._listeners.forEach(fn => fn(this.state)); }
  _setState(partial) { this._state = { ...this._state, ...partial }; this._emit(); }

  setFilter(cat) { this._setState({ filter: this._state.filter === cat ? null : cat }); }
  setSearch(q) { this._setState({ searchQuery: q }); }

  async loadAll() {
    this._setState({ loading: true, error: null });
    try {
      const [experiments, categories] = await Promise.all([
        CacheService.cachedFetch('experiments_all', () => ApiService.getExperiments()),
        CacheService.cachedFetch('categories', () => ApiService.getCategories()),
      ]);
      const progress = AuthService.isLoggedIn()
        ? await CacheService.cachedFetch('progress', () => ApiService.getProgress(), 60000)
        : [];
      this._setState({
        loading: false,
        experiments: experiments.map(e => new Experiment(e)),
        categories,
        progress,
      });
    } catch (e) {
      this._setState({ loading: false, error: e.message });
    }
  }

  async refreshProgress() {
    if (!AuthService.isLoggedIn()) return;
    try {
      const progress = await ApiService.getProgress();
      await CacheService.set('progress', progress, 60000);
      this._setState({ progress });
    } catch {}
  }

  getProgressFor(expId) {
    return this._state.progress.find(p => p.experiment_id === expId) || null;
  }

  async recordRun(expId) {
    if (!AuthService.isLoggedIn()) return;
    try {
      await ApiService.recordProgress(expId);
      await CacheService.del('progress');
      await this.refreshProgress();
    } catch {}
  }
}
