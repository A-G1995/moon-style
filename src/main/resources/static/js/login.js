(function(){
    if (window.__loginBooted) return; window.__loginBooted = true;

    const form = document.getElementById('loginForm');
    const emailEl = document.getElementById('email');
    const passEl = document.getElementById('password');
    const errEl  = document.getElementById('loginError');

    function showErr(msg){ if (errEl){ errEl.textContent = msg; errEl.style.display='block'; } }
    function clearErr(){ if (errEl){ errEl.textContent=''; errEl.style.display='none'; } }

    function toEnglishDigits(s){
        if (!s) return s;
        const map = {'۰':'0','۱':'1','۲':'2','۳':'3','۴':'4','۵':'5','۶':'6','۷':'7','۸':'8','۹':'9',
            '٠':'0','١':'1','٢':'2','٣':'3','٤':'4','٥':'5','٦':'6','٧':'7','٨':'8','٩':'9'};
        return s.replace(/[۰-۹٠-٩]/g, d => map[d] || d);
    }

    form?.addEventListener('submit', async (e)=>{
        e.preventDefault();
        clearErr();

        const email = (emailEl?.value || '').trim();
        const password = toEnglishDigits(passEl?.value || '').trim();

        if (!email || !password){ showErr('ایمیل و رمز عبور را وارد کنید'); return; }

        try{
            const r = await fetch('/user/login', {
                method: 'POST',
                headers: { 'Content-Type':'application/json' },
                body: JSON.stringify({ email, password })
            });
            if (!r.ok){
                let msg = 'ورود ناموفق بود';
                try { const j = await r.json(); if (j?.message) msg = j.message; } catch {}
                showErr(msg); return;
            }
            const res = await r.json();

            // ⬅️ اینجاست که نام واقعی کاربر ذخیره می‌شود:
            localStorage.setItem('sessionId', res.sessionId);
            localStorage.setItem('userId', String(res.userId));
            localStorage.setItem('email', res.email || '');
            localStorage.setItem('fullName', res.fullName || '');
            localStorage.setItem('isAdmin', String(!!res.isAdmin));

            location.href = '/all-products.html';
        }catch(err){
            console.error(err);
            showErr('مشکل در اتصال به سرور');
        }
    });
})();
