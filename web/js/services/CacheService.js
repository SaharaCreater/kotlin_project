// CacheService — IndexedDB-based caching layer (SQLite equivalent in browser)
const CacheService = (() => {
  const DB_NAME = 'dopp_cache';
  const DB_VERSION = 1;
  const STORE = 'cache';
  const TTL = 5 * 60 * 1000; // 5 min

  let db = null;

  async function openDB() {
    if (db) return db;
    return new Promise((resolve, reject) => {
      const req = indexedDB.open(DB_NAME, DB_VERSION);
      req.onupgradeneeded = (e) => {
        const d = e.target.result;
        if (!d.objectStoreNames.contains(STORE)) {
          const store = d.createObjectStore(STORE, { keyPath: 'key' });
          store.createIndex('expires', 'expires', { unique: false });
        }
      };
      req.onsuccess = (e) => { db = e.target.result; resolve(db); };
      req.onerror = () => reject(req.error);
    });
  }

  async function set(key, value, ttl = TTL) {
    const d = await openDB();
    return new Promise((resolve, reject) => {
      const tx = d.transaction(STORE, 'readwrite');
      tx.objectStore(STORE).put({ key, value, expires: Date.now() + ttl });
      tx.oncomplete = resolve;
      tx.onerror = () => reject(tx.error);
    });
  }

  async function get(key) {
    try {
      const d = await openDB();
      return new Promise((resolve) => {
        const tx = d.transaction(STORE, 'readonly');
        const req = tx.objectStore(STORE).get(key);
        req.onsuccess = () => {
          const entry = req.result;
          if (!entry) return resolve(null);
          if (Date.now() > entry.expires) { del(key); return resolve(null); }
          resolve(entry.value);
        };
        req.onerror = () => resolve(null);
      });
    } catch { return null; }
  }

  async function del(key) {
    const d = await openDB();
    return new Promise((resolve) => {
      const tx = d.transaction(STORE, 'readwrite');
      tx.objectStore(STORE).delete(key);
      tx.oncomplete = resolve;
      tx.onerror = resolve;
    });
  }

  async function clear() {
    const d = await openDB();
    return new Promise((resolve) => {
      const tx = d.transaction(STORE, 'readwrite');
      tx.objectStore(STORE).clear();
      tx.oncomplete = resolve;
    });
  }

  // Cached fetch helper
  async function cachedFetch(key, fetchFn, ttl = TTL) {
    const cached = await get(key);
    if (cached !== null) return cached;
    const fresh = await fetchFn();
    await set(key, fresh, ttl);
    return fresh;
  }

  return { set, get, del, clear, cachedFetch };
})();
