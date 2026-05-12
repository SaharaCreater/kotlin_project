// ApiService — HTTP client wrapping all backend API calls
const ApiService = (() => {
  const BASE = '/api';

  async function request(method, path, body) {
    const headers = { 'Content-Type': 'application/json' };
    const token = AuthService.getToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(BASE + path, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });

    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.error || `HTTP ${res.status}`);
    return data;
  }

  const get = (path) => request('GET', path);
  const post = (path, body) => request('POST', path, body);
  const put = (path, body) => request('PUT', path, body);

  return {
    // Auth
    register: (email, name, password) => post('/auth/register', { email, name, password }),
    login: (email, password) => post('/auth/login', { email, password }),
    getMe: () => get('/auth/me'),
    updateProfile: (name) => put('/auth/profile', { name }),

    // Experiments
    getExperiments: (category) => get('/experiments' + (category ? `?category=${category}` : '')),
    getExperiment: (id) => get(`/experiments/${id}`),

    // Categories
    getCategories: () => get('/categories'),

    // Progress
    getProgress: () => get('/progress'),
    recordProgress: (expId) => post(`/progress/${expId}`, {}),
  };
})();
