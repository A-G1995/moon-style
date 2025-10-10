// /js/login.js
(function () {
    if (window.__loginBooted) return; window.__loginBooted = true;

    const form = document.getElementById('loginForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            email: form.email?.value?.trim(),
            password: form.password?.value?.trim()
        };

        if (!payload.email || !payload.password) {
            alert('ایمیل و رمز عبور را وارد کنید');
            return;
        }

        try {
            const res = await fetch('/user/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                let msg = 'ورود ناموفق بود';
                try { const j = await res.json(); if (j?.message) msg = j.message; } catch {}
                alert(msg);
                return;
            }

            const data = await res.json(); // {sessionId,userId,isAdmin,email,fullName}
            localStorage.setItem('sessionId', data.sessionId);
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('isAdmin', data.isAdmin);
            localStorage.setItem('email', data.email || '');
            localStorage.setItem('fullName', data.fullName || '');

            // ✅ اگر ادمین بود، مستقیم پنل ادمین
            if (data.isAdmin) {
                location.href = '/admin.html';
            } else {
                location.href = '/all-products.html';
            }
        } catch (err) {
            console.error(err);
            alert('خطا در ارتباط با سرور');
        }
    });
})();
