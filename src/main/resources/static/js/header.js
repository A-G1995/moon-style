(function () {
    if (window.__headerBooted) return;
    window.__headerBooted = true;

    const mount = document.getElementById('siteHeader');
    if (!mount) return;

    const getSession = () => ({
        sid: localStorage.getItem('sessionId'),
        uid: localStorage.getItem('userId'),
        isAdmin: localStorage.getItem('isAdmin') === 'true',
        email: localStorage.getItem('email') || '',
        fullName: localStorage.getItem('fullName') || ''
    });

    async function fetchCartCount(sid) {
        try {
            const r = await fetch('/cart', { headers: { 'X-Session-Id': sid } });
            if (!r.ok) return 0;
            const j = await r.json();
            return Array.isArray(j.items)
                ? j.items.reduce((a, it) => a + (it.quantity || 0), 0)
                : 0;
        } catch {
            return 0;
        }
    }

    function baseStyles() {
        return `
      <style>
        .ms-header{background:#001937;color:#fff;padding:10px 16px;display:flex;gap:12px;align-items:center;justify-content:space-between}
        .ms-brand{font-weight:700}
        .ms-nav{display:flex;gap:12px;align-items:center}
        .ms-nav a{color:#fff;text-decoration:none;margin:0 6px}
        .ms-chip{background:#fff;color:#001937;border-radius:999px;padding:4px 10px;font-size:13px}
        .ms-btn{background:#ff5a5f;color:#fff;border:none;border-radius:8px;padding:6px 10px;cursor:pointer}
        .ms-cart{background:#8a5cd6;color:#fff;border-radius:8px;padding:6px 10px;text-decoration:none}
        .ms-nav a:hover{opacity:.9}
      </style>
    `;
    }

    function renderLoggedOut() {
        mount.innerHTML = `
      ${baseStyles()}
      <div class="ms-header" id="msHeader">
        <div class="ms-brand">
          <a href="/all-products.html" style="color:#fff;text-decoration:none">Moon Style</a>
        </div>
        <nav class="ms-nav">
          <a href="/all-products.html">محصولات</a>
          <a href="/login.html">ورود</a>
          <a href="/signup.html">ثبت‌نام</a>
        </nav>
      </div>
    `;
    }

    async function renderLoggedIn(sess) {
        const count = await fetchCartCount(sess.sid);
        const name = sess.fullName || sess.email || 'کاربر';

        mount.innerHTML = `
      ${baseStyles()}
      <div class="ms-header" id="msHeader">
        <div class="ms-brand">
          <a href="/all-products.html" style="color:#fff;text-decoration:none">Moon Style</a>
        </div>
        <nav class="ms-nav">
          <a href="/all-products.html">محصولات</a>
          <a class="ms-cart" href="/cart.html">سبد خرید (${count})</a>
          <a href="/account.html" id="accountLink">حساب کاربری</a>
          ${sess.isAdmin ? '<a href="/panel.html">پنل ادمین</a>' : ''}
          <span class="ms-chip" title="${name}">${name}</span>
          <button id="msLogout" class="ms-btn">خروج</button>
        </nav>
      </div>
    `;

        document.getElementById('msLogout')?.addEventListener('click', () => {
            localStorage.removeItem('sessionId');
            localStorage.removeItem('userId');
            localStorage.removeItem('isAdmin');
            localStorage.removeItem('email');
            localStorage.removeItem('fullName');
            location.href = '/login.html';
        });
    }

    (async function init() {
        const sess = getSession();
        if (!sess.sid) { renderLoggedOut(); return; }
        await renderLoggedIn(sess);
    })();
})();

// به‌روزرسانی شمارنده‌ی سبد پس از هر تغییر
window.addEventListener('cart-updated', async () => {
    const sid = localStorage.getItem('sessionId');
    if (!sid) return;

    try {
        const r = await fetch('/cart', { headers: { 'X-Session-Id': sid } });
        if (!r.ok) return;
        const j = await r.json();
        const count = Array.isArray(j.items)
            ? j.items.reduce((a, it) => a + (it.quantity || 0), 0)
            : 0;

        const link = document.querySelector('#siteHeader .ms-cart');
        if (link) {
            link.textContent = `سبد خرید (${count})`;
            link.setAttribute('href', '/cart.html');
        }
    } catch (e) {
        console.debug('[header] cart-updated refresh skipped:', e);
    }
});

document.getElementById('msLogout')?.addEventListener('click', async () => {
    const sid = localStorage.getItem('sessionId');
    try {
        if (sid) {
            await fetch('/user/logout', { method:'POST', headers: { 'X-Session-Id': sid } });
        }
    } catch (e) {
        console.debug('[logout] server call failed (ignored)', e);
    }
    localStorage.removeItem('sessionId');
    localStorage.removeItem('userId');
    localStorage.removeItem('isAdmin');
    localStorage.removeItem('email');
    localStorage.removeItem('fullName');
    location.href = '/login.html';
});