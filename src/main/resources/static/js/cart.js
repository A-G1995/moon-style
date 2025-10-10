// /js/cart.js
(function(){
    if (window.__cartBooted) return; window.__cartBooted = true;

    const itemsBox   = document.getElementById('itemsBox');
    const emptyBox   = document.getElementById('emptyBox');
    const errBox     = document.getElementById('cartError');
    const subEl      = document.getElementById('subTotal');
    const shipEl     = document.getElementById('shipping');
    const grandEl    = document.getElementById('grandTotal');
    const btnCheckout= document.getElementById('btnCheckout');

    const sid = localStorage.getItem('sessionId');
    if (!sid) { alert('ابتدا وارد شوید'); location.href = '/login.html'; }

    function showErr(msg){ if (!errBox) return; errBox.textContent = msg; errBox.style.display='block'; }
    function clearErr(){ if (!errBox) return; errBox.textContent=''; errBox.style.display='none'; }
    function fmt(n){ try { return Number(n||0).toLocaleString('fa-IR'); } catch { return String(n||0); } }

    // رندر یک سطر آیتم سبد (سازگار با هر دو مدل DTO: فلت یا product{})
    function rowHtml(it){
        const pid   = it.productId ?? it.product?.id;                 // Long | Int
        const title = it.title ?? it.product?.title ?? '';
        const price = it.price ?? it.product?.price ?? 0;             // Long
        const qty   = it.quantity ?? 1;
        const img   = it.imageUrl ?? it.product?.imageUrl ?? '/images/placeholder.jpg';

        return `
      <div class="row" data-id="${pid}">
        <img src="${img}" alt="${title}" onerror="this.onerror=null;this.src='/images/placeholder.jpg';"/>
        <div class="title">${title}</div>
        <div class="price">${fmt(price)} تومان</div>
        <div class="qty">
          <input type="number" min="1" value="${qty}" class="qty-input" />
        </div>
        <button class="remove" title="حذف"><i class="fa fa-trash"></i></button>
      </div>
    `;
    }

    function renderCart(cart){
        itemsBox.innerHTML = '';
        const items = Array.isArray(cart?.items) ? cart.items : [];
        if (items.length === 0){
            emptyBox.style.display = 'block';
            subEl.textContent = '۰'; shipEl.textContent='۰'; grandEl.textContent='۰';
            return;
        }
        emptyBox.style.display = 'none';

        const frag = document.createDocumentFragment();
        items.forEach(it=>{
            const wrap = document.createElement('div');
            wrap.innerHTML = rowHtml(it);
            frag.appendChild(wrap.firstElementChild);
        });
        itemsBox.appendChild(frag);

        // اگر backend subtotal داده، همان را جمع بزن؛ وگرنه price*quantity
        const sub = items.reduce((a,it)=>{
            const line = (it.subtotal != null)
                ? it.subtotal
                : ((it.price ?? it.product?.price ?? 0) * (it.quantity ?? 0));
            return a + Number(line || 0);
        }, 0);

        const shipping = 0; // پروژه دانشگاهی: صفر
        subEl.textContent   = fmt(sub);
        shipEl.textContent  = fmt(shipping);
        grandEl.textContent = fmt(sub + shipping);
    }

    async function loadCart(){
        clearErr();
        try{
            const r = await fetch('/cart', { headers: { 'X-Session-Id': sid } });
            if (!r.ok){
                let msg = 'دریافت سبد ناموفق بود';
                try { const j = await r.json(); if (j?.message) msg = j.message; } catch {}
                showErr(msg); return;
            }
            const cart = await r.json(); // { items:[{ productId|product{}, title?, price?, quantity, subtotal? }], total? }
            renderCart(cart);
        }catch(e){
            console.error(e);
            showErr('خطا در برقراری ارتباط با سرور');
        }
    }

    // تغییر تعداد
    itemsBox.addEventListener('change', async (e)=>{
        const input = e.target.closest('.qty-input');
        if (!input) return;
        const row = e.target.closest('.row');
        const pid = Number(row?.dataset?.id);
        const qty = Math.max(1, Number(input.value||1));

        try{
            const r = await fetch('/cart/items', {
                method:'POST',
                headers:{ 'Content-Type':'application/json', 'X-Session-Id': sid },
                body: JSON.stringify({ productId: pid, quantity: qty })
            });
            if (!r.ok) { alert('به‌روزرسانی تعداد ناموفق بود'); return; }
            await loadCart();
            window.dispatchEvent(new CustomEvent('cart-updated'));
        }catch(e){ console.error(e); alert('خطا در اتصال'); }
    });

    // حذف آیتم
    itemsBox.addEventListener('click', async (e)=>{
        const btn = e.target.closest('.remove');
        if (!btn) return;
        const row = e.target.closest('.row');
        const pid = Number(row?.dataset?.id);
        try{
            const r = await fetch(`/cart/items/${pid}`, { method:'DELETE', headers:{ 'X-Session-Id': sid } });
            if (!r.ok) { alert('حذف ناموفق بود'); return; }
            await loadCart();
            window.dispatchEvent(new CustomEvent('cart-updated'));
        }catch(e){ console.error(e); alert('خطا در اتصال'); }
    });

    // --- پیشنهادها بعد از Checkout (میانگین قیمت اقلام) ---
    async function showRecommendations(amount, percent = 20, limit = 8) {
        try {
            const url = `/recommendations/spend?amount=${encodeURIComponent(amount)}&percent=${percent}&limit=${limit}`;
            const r = await fetch(url);
            if (!r.ok) return;

            const items = await r.json();
            const sec = document.getElementById('recoSection');
            const grid = document.getElementById('recoGrid');
            if (!sec || !grid) return;

            grid.innerHTML = '';
            if (!Array.isArray(items) || items.length === 0) {
                sec.style.display = 'none';
                return;
            }

            items.forEach(p => {
                const card = document.createElement('div');
                card.className = 'product-card';
                card.innerHTML = `
          <a href="/product.html?id=${p.id}">
            <img src="${p.imageUrl || '/images/placeholder.jpg'}" alt="${p.title || ''}"
                 style="width:100%;height:220px;object-fit:cover;border-radius:6px;border:1px solid #eee"
                 onerror="this.onerror=null;this.src='/images/placeholder.jpg';"/>
            <div class="product-name" style="font-size:13px;margin-top:6px">${p.title || ''}</div>
            <div class="product-price" style="font-weight:700;color:#d62828">${(p.price ?? '').toString()} تومان</div>
          </a>`;
                grid.appendChild(card);
            });

            sec.style.display = 'block';
        } catch (e) {
            console.debug('[reco] skipped:', e);
        }
    }

    async function checkout(){
        if (!btnCheckout) return;
        if (btnCheckout.dataset.busy === '1') return;
        btnCheckout.dataset.busy='1'; btnCheckout.style.opacity='.7';

        try{
            const r = await fetch('/orders/checkout', { method:'POST', headers:{ 'X-Session-Id': sid } });
            if (!r.ok){
                let msg = 'ثبت سفارش ناموفق بود';
                try { const j = await r.json(); if (j?.message) msg = j.message; } catch {}
                alert(msg); return;
            }
            const order = await r.json(); // { id, total, items:[{ price, quantity }], createdAt }

            alert('سفارش با موفقیت ثبت شد.');

            await loadCart();
            window.dispatchEvent(new CustomEvent('cart-updated'));

            // SNNA: میانگین قیمت اقلام خریداری‌شده
            const items = Array.isArray(order?.items) ? order.items : [];
            const totalQty = items.reduce((a,it)=> a + (it.quantity||0), 0);
            const avgAmount = totalQty > 0 ? Math.round(order.total / totalQty) : order.total;

            const percent = (avgAmount < 600000) ? 30 : 20;
            const limit   = 8;
            showRecommendations(avgAmount, percent, limit);

        }catch(e){
            console.error(e);
            alert('خطا در برقراری ارتباط با سرور');
        }finally{
            btnCheckout.dataset.busy='0'; btnCheckout.style.opacity='';
        }
    }

    btnCheckout?.addEventListener('click', checkout);

    // شروع
    loadCart();
})();
