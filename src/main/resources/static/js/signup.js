// src/main/resources/static/js/signup.js
(function () {
    const form = document.getElementById('signup-form');
    const emailEl = document.getElementById('email');
    const fullNameEl = document.getElementById('fullName');
    const passEl = document.getElementById('password');
    const phoneEl = document.getElementById('phoneNumber');
    const nationalEl = document.getElementById('nationalNumber');
    const errBox = document.getElementById('errorBox');

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
        const fullName = (fullNameEl.value || '').trim();
        const password = passEl.value || '';
        const phoneNumber = phoneEl.value || '';
        const nationalNumber = nationalEl.value || '';

        if (!email || !fullName || !password || !phoneNumber || !nationalNumber) {
            showError('لطفاً همه فیلدها را پر کنید.');
            return;
        }

        try {
            const res = await fetch('/user/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, fullName, password, phoneNumber, nationalNumber })
            });

            if (!res.ok) {
                let msg = 'ثبت‌نام ناموفق بود';
                try {
                    const j = await res.json();
                    if (j && j.message) msg = j.message;
                } catch {}
                showError(msg);
                return;
            }

            const data = await res.json();
            // Expected: { userId, sessionId, isAdmin }
            localStorage.setItem('sessionId', data.sessionId);
            localStorage.setItem('userId', String(data.userId));
            localStorage.setItem('isAdmin', String(!!data.isAdmin));

            // redirect after signup: directly logged in
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