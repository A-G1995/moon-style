// /js/account.js
(function () {
    if (window.__accountBooted) return; window.__accountBooted = true;

    const sid = localStorage.getItem('sessionId');
    if (!sid) { alert('ابتدا وارد شوید'); location.href = '/login.html'; return; }

    // ---- Elements: Profile
    const v_fullName = document.getElementById('v_fullName');
    const v_email    = document.getElementById('v_email');
    const v_phone    = document.getElementById('v_phone');
    const v_national = document.getElementById('v_national');
    const v_birth    = document.getElementById('v_birth');

    const profileError = document.getElementById('profileError');
    const profileView  = document.getElementById('profileView');
    const profileForm  = document.getElementById('profileForm');
    const btnEdit      = document.getElementById('btnEdit');
    const btnCancel    = document.getElementById('btnCancel');

    const f_fullName = document.getElementById('f_fullName');
    const f_email    = document.getElementById('f_email');
    const f_phone    = document.getElementById('f_phone');
    const f_national = document.getElementById('f_national');
    const f_birth    = document.getElementById('f_birth');

    // ---- Elements: Purchases (left column)
    const purchasesList  = document.getElementById('purchasesList');
    const purchasesEmpty = document.getElementById('purchasesEmpty');

    // Helpers
    const showPErr = (msg) => { profileError.textContent = msg; profileError.style.display = 'block'; };
    const clearPErr= () => { profileError.textContent = ''; profileError.style.display = 'none'; };
    const fmt = (n) => { try { return Number(n||0).toLocaleString('fa-IR'); } catch { return String(n||0); } };

    // ---- Profile API
    async function loadProfile(){
        clearPErr();
        try{
            const r = await fetch('/user/me', { headers: { 'X-Session-Id': sid } });
            if (!r.ok){ showPErr('دریافت پروفایل ناموفق بود'); return; }
            const u = await r.json(); // { fullName,email,phoneNumber,nationalNumber,birthDate }
            v_fullName.textContent = u.fullName || '—';
            v_email.textContent    = u.email || '—';
            v_phone.textContent    = u.phoneNumber || '—';
            v_national.textContent = u.nationalNumber || '—';
            v_birth.textContent    = u.birthDate || '—';

            f_fullName.value = u.fullName || '';
            f_email.value    = u.email || '';
            f_phone.value    = u.phoneNumber || '';
            f_national.value = u.nationalNumber || '';
            f_birth.value    = u.birthDate || '';
        }catch(e){
            console.error(e); showPErr('خطا در اتصال به سرور');
        }
    }

    async function saveProfile(payload){
        clearPErr();
        try{
            const r = await fetch('/user/me', {
                method:'PUT',
                headers:{ 'Content-Type':'application/json', 'X-Session-Id': sid },
                body: JSON.stringify(payload)
            });
            if (!r.ok){ showPErr('ویرایش ناموفق بود'); return false; }
            await loadProfile();
            localStorage.setItem('fullName', payload.fullName || '');
            return true;
        }catch(e){
            console.error(e); showPErr('خطا در اتصال به سرور'); return false;
        }
    }

    // ---- Purchases (aggregate last orders)
    async function loadPurchases(){
        try{
            const r = await fetch('/orders', { headers: { 'X-Session-Id': sid } });
            if (!r.ok){ purchasesEmpty.style.display='block'; return; }
            const orders = await r.json(); // List<OrderDto>, items = []
            if (!Array.isArray(orders) || orders.length === 0){ purchasesEmpty.style.display='block'; return; }

            const ids = orders.slice(0, 5).map(o=>o.id);
            const details = await Promise.all(
                ids.map(id => fetch(`/orders/${id}`, { headers: { 'X-Session-Id': sid } }).then(x => x.ok ? x.json() : null))
            );

            const agg = new Map(); // key: pid|title -> { productId, title, qty }
            details.filter(Boolean).forEach(d=>{
                (d.items || []).forEach(it=>{
                    const key = `${it.productId}|${it.title}`;
                    const cur = agg.get(key) || { productId: it.productId, title: it.title, qty: 0 };
                    cur.qty += (it.quantity || 0);
                    agg.set(key, cur);
                });
            });

            const items = Array.from(agg.values());
            if (items.length === 0){ purchasesEmpty.style.display='block'; return; }

            items.sort((a,b)=> b.qty - a.qty);
            purchasesList.innerHTML = '';
            items.slice(0,10).forEach(it=>{
                const li = document.createElement('li');
                li.innerHTML = `
          <span class="buy-title">
            <a href="/product.html?id=${it.productId}" style="text-decoration:none;color:#111">${it.title}</a>
          </span>
          <span class="chip">${fmt(it.qty)} عدد</span>
        `;
                purchasesList.appendChild(li);
            });
            purchasesEmpty.style.display='none';
        }catch(e){
            console.debug('[purchases] skipped', e);
            purchasesEmpty.style.display='block';
        }
    }

    // ---- Events
    btnEdit?.addEventListener('click', ()=>{
        profileView.style.display = 'none';
        profileForm.style.display = 'block';
    });
    btnCancel?.addEventListener('click', ()=>{
        profileForm.style.display = 'none';
        profileView.style.display = 'block';
    });
    profileForm?.addEventListener('submit', async (e)=>{
        e.preventDefault();
        const payload = {
            fullName: (f_fullName.value||'').trim(),
            email: (f_email.value||'').trim(),
            phoneNumber: (f_phone.value||'').trim(),
            nationalNumber: (f_national.value||'').trim(),
            birthDate: f_birth.value || null
        };
        const ok = await saveProfile(payload);
        if (ok){
            profileForm.style.display = 'none';
            profileView.style.display = 'block';
        }
    });

    // ---- Init
    loadProfile();
    loadPurchases();
})();
