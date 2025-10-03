// /js/all-products.js
(function () {
    if (window.__allProductsBooted) return;
    window.__allProductsBooted = true;

    const grid = document.getElementById('productsGrid') || document.querySelector('.grid-container');
    let errBox = document.getElementById('productsError');
    if (!grid) { console.error('[all-products] grid container not found'); return; }
    if (!errBox) {
        errBox = document.createElement('div');
        errBox.id = 'productsError';
        errBox.style.cssText = 'display:none;color:#c62828;background:#ffebee;border:1px solid #ffcdd2;padding:10px;border-radius:8px;margin:12px 0';
        grid.parentElement?.insertBefore(errBox, grid);
    }

    const LOADING_ID = '__products_loading__';
    const showLoading = () => {
        if (!document.getElementById(LOADING_ID)) {
            const el = document.createElement('div');
            el.id = LOADING_ID;
            el.style.cssText = 'padding:12px;font-size:14px;color:#555';
            el.textContent = 'در حال بارگذاری محصولات...';
            grid.appendChild(el);
        }
    };
    const hideLoading = () => document.getElementById(LOADING_ID)?.remove();

    function showError(msg){ errBox.textContent = msg; errBox.style.display = 'block'; }
    function clearError(){ errBox.style.display='none'; errBox.textContent=''; }

    async function fetchJson(url) {
        const res = await fetch(url, { headers: { Accept: 'application/json' } });
        if (!res.ok) {
            let msg = `HTTP ${res.status}`;
            try { const j = await res.json(); if (j?.message || j?.error) msg += ` - ${j.message || j.error}`; } catch {}
            throw new Error('خطا در دریافت محصولات: ' + msg);
        }
        try { return await res.json(); }
        catch { throw new Error('پاسخ سرور JSON نیست'); }
    }

    // فقط فیلترهای واقعی؛ بدون q و بدون page/size
    function buildQueryFromUrl() {
        const params = new URLSearchParams(location.search);
        const keys = ['q','color','size','category','priceMin','priceMax']; // بدون page/size
        const qs = [];
        for (const k of keys) {
            const v = params.get(k);
            if (v) qs.push(`${k}=${encodeURIComponent(v)}`);
        }
        return qs.length ? `?${qs.join('&')}` : '';
    }

    function productCardHtml(p) {
        const id = p.id;
        const title = p.title || '';
        const price = (p.price ?? '').toString();
        const img = p.imageUrl || '/images/placeholder.jpg';
        return `
      <div class="product-card">
        <a href="/product.html?id=${id}">
          <img src="${img}" alt="${title}" onerror="this.onerror=null;this.src='/images/placeholder.jpg';"/>
          <div class="product-name">${title}</div>
          <div class="product-price">${price} تومان</div>
        </a>
        <div class="icons" role="group" aria-label="actions">
          <a href="#" class="add-to-cart" data-id="${id}" title="افزودن به سبد">
            <i class="fas fa-shopping-cart"></i>
          </a>
        </div>
      </div>
    `;
    }

    function renderProducts(items) {
        grid.innerHTML = '';
        if (!Array.isArray(items) || items.length === 0) {
            grid.innerHTML = `
        <div style="padding:12px">
          هیچ محصولی با این فیلترها پیدا نشد.
          <button id="clearFiltersBtn" style="margin-inline-start:8px;padding:6px 10px;border:0;border-radius:8px;background:#eee;cursor:pointer">
            حذف فیلترها
          </button>
        </div>`;
            document.getElementById('clearFiltersBtn')?.addEventListener('click', () => { location.search = ''; });
            return;
        }
        const frag = document.createDocumentFragment();
        for (const p of items) {
            const wrap = document.createElement('div');
            wrap.innerHTML = productCardHtml(p);
            frag.appendChild(wrap.firstElementChild);
        }
        grid.appendChild(frag);
    }

    async function handleAddToCart(btn) {
        const sid = localStorage.getItem('sessionId');
        if (!sid) { alert('ابتدا وارد شوید'); location.href='/login.html'; return; }
        if (btn.dataset.busy === '1') return;
        btn.dataset.busy = '1'; btn.style.opacity = '0.6';
        try {
            const r = await fetch('/cart/items', {
                method:'POST',
                headers:{'Content-Type':'application/json','X-Session-Id':sid},
                body: JSON.stringify({ productId: Number(btn.dataset.id), quantity: 1 })
            });
            if (!r.ok) {
                let msg = 'خطا در افزودن به سبد';
                try { const j = await r.json(); if (j?.message) msg = j.message; } catch {}
                alert(msg); return;
            }
            window.dispatchEvent(new CustomEvent('cart-updated'));
            alert('به سبد افزوده شد');
        } catch(e) {
            console.error(e); alert('مشکل در ارتباط با سرور');
        } finally {
            btn.dataset.busy='0'; btn.style.opacity='';
        }
    }

    async function load() {
        clearError(); showLoading();
        try {
            const qs = buildQueryFromUrl();
            const url = '/products' + qs;        // اگر qs خالی باشد: همهٔ محصولات
            let data = await fetchJson(url);

            // اگر سرور Page برگرداند، آرایه‌اش را جدا کنیم؛ وگرنه همان data (آرایه)
            const items = Array.isArray(data) ? data : (Array.isArray(data?.content) ? data.content : []);

            // ❌ هیچ fallbackی به «همهٔ محصولات» در صورت نتیجه صفر وجود ندارد
            renderProducts(items);

            grid.addEventListener('click', (e)=>{
                const btn = e.target.closest('.add-to-cart');
                if (!btn) return;
                e.preventDefault();
                handleAddToCart(btn);
            });

        } catch (err) {
            showError(err.message || 'خطای نامشخص در بارگذاری');
        } finally {
            hideLoading();
        }
    }

    load();
})();
