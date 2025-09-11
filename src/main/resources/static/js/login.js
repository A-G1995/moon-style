// src/main/resources/static/js/login.js
(function () {
    const form = document.getElementById('login-form');
    const emailEl = document.getElementById('email');
    const passEl = document.getElementById('password');
    const errBox = document.getElementById('errorBox');

    // marker so inline fallback doesn't double-handle
    window.__loginHandlerAttached = true;

    function showError(msg) {
        if (!errBox) return alert(msg);
        errBox.textContent = msg;
        errBox.style.display = 'block';
    }
    function hideError() {
        if (errBox) errBox.style.display = 'none';
    }

    if (!form) return;
    form.addEventListener('submit', async function (e) {
        e.preventDefault();
        hideError();

        const email = (emailEl.value || '').trim();
        const password = passEl.value || '';

        if (!email || !password) {
            showError('ایمیل و رمز عبور الزامی است.');
            return;
        }

        try {
            const res = await fetch('/user/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            if (!res.ok) {
                let msg = 'ورود ناموفق بود';
                try {
                    const j = await res.json();
                    if (j && j.message) msg = j.message;
                } catch {}
                showError(msg);
                return;
            }

            const data = await res.json();
            // Expected backend response: { userId, sessionId, isAdmin }
            localStorage.setItem('sessionId', data.sessionId);
            localStorage.setItem('userId', String(data.userId));
            localStorage.setItem('isAdmin', String(!!data.isAdmin));

            // redirect
            if (data.isAdmin) {
                window.location.href = '/panel.html';
            } else {
                window.location.href = '/all-products.html';
            }
        } catch (err) {
            console.error(err);
            showError('خطا در ارتباط با سرور');
        }
    });
})();