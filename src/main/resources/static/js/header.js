(function(){
    if (window.__headerBooted) return;
    window.__headerBooted = true;

    const mount = document.getElementById('siteHeader');
    if (!mount) return;

    const getSession = () => ({
        sid: localStorage.getItem('sessionId'),
        uid: localStorage.getItem('userId'),
        isAdmin: localStorage.getItem('isAdmin') === 'true',
        // store these at login if you have them:
        email: localStorage.getItem('email') || '',
        fullName: localStorage.getItem('fullName') || ''
    });

    const el = (tag, attrs={}, html='') => {
        const e = document.createElement(tag);
        for (const [k,v] of Object.entries(attrs)) e.setAttribute(k, v);
        e.innerHTML = html;
        return e;
    };

    async function fetchCartCount(sid){
        try {
            const r = await fetch('/cart', { headers: { 'X-Session-Id': sid } });
            if (!r.ok) return 0;
            const j = await r.json();
            return Array.isArray(j.items) ? j.items.reduce((a,it)=>a + (it.quantity||0), 0) : 0;
        } catch { return 0; }
    }

    function renderLoggedOut(){
        mount.innerHTML = `
      <style>
        .ms-header{background:#001937;color:#fff;padding:10px 16px;display:flex;gap:12px;align-items:center;justify-content:space-between}
        .ms-brand{font-weight:700}
        .ms-nav a{color:#fff;text-decoration:none;margin:0 6px}
        .ms-btn{background:#fff;color:#001937;border:none;border-radius:8px;padding:6px 10px;cursor:pointer}
      </style>
      <div class="ms-header">
        <div class="ms-brand"><a href="/all-products.html" style="color:#fff;text-decoration:none">Moon Style</a></div>
        <div class="ms-nav">
          <a href="/all-products.html">محصولات</a>
          <a href="/login.html">ورود</a>
          <a href="/signup.html">ثبت‌نام</a>
        </div>
      </div>
    `;
    }

    async function renderLoggedIn(sess){
        const count = await fetchCartCount(sess.sid);
        const name = sess.fullName || sess.email || 'کاربر';

        mount.innerHTML = `
      <style>
        .ms-header{background:#001937;color:#fff;padding:10px 16px;display:flex;gap:12px;align-items:center;justify-content:space-between}
        .ms-brand{font-weight:700}
        .ms-nav{display:flex;gap:12px;align-items:center}
        .ms-nav a{color:#fff;text-decoration:none;margin:0 6px}
        .ms-chip{background:#fff;color:#001937;border-radius:999px;padding:4px 10px;font-size:13px}
        .ms-btn{background:#ff5a5f;color:#fff;border:none;border-radius:8px;padding:6px 10px;cursor:pointer}
        .ms-cart{background:#8a5cd6;color:#1873e1;border-radius:8px;padding:6px 10px;text-decoration:none}
      </style>
      <div class="ms-header">
        <div class="ms-brand"><a href="/all-products.html" style="color:#fff;text-decoration:none">Moon Style</a></div>
        <div class="ms-nav">
          <a class="ms-cart" href="/cart.html">سبد خرید (${count})</a>
          <a href="/account.html">حساب کاربری</a>
          ${sess.isAdmin ? '<a href="/panel.html">پنل ادمین</a>' : ''}
          <span class="ms-chip" title="${name}">${name}</span>
          <button id="msLogout" class="ms-btn">خروج</button>
        </div>
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

    (async function init(){
        const sess = getSession();
        if (!sess.sid) { renderLoggedOut(); return; }
        await renderLoggedIn(sess);
    })();
})();