// /js/product.js
(function(){
    // محافظ دوباره‌لود شدن
    if (window.__productBooted) return;
    window.__productBooted = true;

    const $ = (sel) => document.querySelector(sel);
    const err = $('#err');
    function showErr(msg){ err.textContent = msg; err.style.display = 'block'; }
    function clearErr(){ err.style.display = 'none'; err.textContent = ''; }

    // گرفتن id از URL
    const params = new URLSearchParams(location.search);
    const id = Number(params.get('id'));
    if (!id) { document.body.innerHTML = '<div style="padding:20px">شناسه محصول نامعتبر است.</div>'; return; }

    // کمک‌تابع: واکشی با پیام‌های خوانا
    async function fetchJson(url, opts){
        const res = await fetch(url, opts);
        if (!res.ok) {
            let msg = `HTTP ${res.status}`;
            try { const j = await res.json(); if (j?.message || j?.error) msg += ` - ${j.message || j.error}`; } catch {}
            throw new Error(msg);
        }
        try { return await res.json(); }
        catch { throw new Error('پاسخ JSON معتبر نیست'); }
    }

    // بارگذاری محصول
    async function load(){
        clearErr();
        const img = $('.product-image');
        const name = $('.product-name');
        const price = $('.price');
        const color = $('.color');
        const size = $('.size');
        const category = $('.category');
        const stock = $('.stock');
        const priceBox = $('#priceBox');

        // نشانگر بارگذاری ملایم
        name.textContent = 'در حال بارگذاری...';

        try{
            const p = await fetchJson('/products/' + id);

            // مقداردهی
            name.textContent = p.title || '';
            price.textContent = (p.price ?? '') + ' تومان';
            priceBox.textContent = (p.price ?? '') + ' تومان';
            color.textContent = p.color || '-';
            size.textContent = p.size || '-';
            category.textContent = p.category || '-';
            stock.textContent = (p.stockQty ?? '-') + '';

            if (img) {
                img.src = p.imageUrl || '/images/placeholder.jpg';
                img.onerror = function(){ this.onerror=null; this.src='/images/placeholder.jpg'; };
                img.alt = p.title || '';
            }

            // افزودن به سبد
            $('#addBtn')?.addEventListener('click', async ()=>{
                const sid = localStorage.getItem('sessionId');
                if (!sid) { alert('ابتدا وارد شوید'); location.href='/login.html'; return; }

                const btn = $('#addBtn');
                if (btn.dataset.busy === '1') return;
                btn.dataset.busy = '1'; btn.style.opacity = '0.6';

                try{
                    const r = await fetch('/cart/items', {
                        method:'POST',
                        headers:{'Content-Type':'application/json','X-Session-Id':sid},
                        body: JSON.stringify({ productId: id, quantity: 1 })
                    });
                    if(!r.ok){
                        let msg = 'خطا در افزودن به سبد';
                        try{ const j = await r.json(); if(j?.message) msg = j.message; }catch{}
                        alert(msg); return;
                    }
                    // برای آپدیت شمارنده سبد در هدر
                    window.dispatchEvent(new CustomEvent('cart-updated'));
                    alert('به سبد افزوده شد');
                }catch(e){
                    console.error(e);
                    alert('مشکل در ارتباط با سرور');
                }finally{
                    btn.dataset.busy = '0'; btn.style.opacity = '';
                }
            });

        }catch(e){
            console.error('[product] load error:', e);
            showErr('خطا در دریافت اطلاعات محصول (' + (e.message || 'نامشخص') + ')');
            // پیام «یافت نشد» دوستانه‌تر
            if (String(e.message||'').includes('HTTP 404')) {
                $('.container').innerHTML = '<div class="card" style="grid-column:1/-1;padding:24px">محصول یافت نشد.</div>';
            }
        }
    }

    load();
})();
