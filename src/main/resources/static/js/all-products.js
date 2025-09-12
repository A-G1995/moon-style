// /js/all-products.js
(function () {
    // prevent double init if script loaded twice
    if (window.__allProductsBooted) return;
    window.__allProductsBooted = true;

    // Choose a stable container (prefer #productsGrid, fallback .grid-container)
    const grid = document.getElementById('productsGrid') || document.querySelector('.grid-container');
    const errBox = document.getElementById('productsError');

    if (!grid) {
        console.error('[all-products] grid container not found (#productsGrid or .grid-container)');
        return;
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

    const showError = (msg) => {
        console.error('[all-products] ' + msg);
        if (errBox) {
            errBox.textContent = msg;
            errBox.style.display = 'block';
        } else {
            const el = document.createElement('div');
            el.style.cssText = 'padding:12px;color:#c62828';
            el.textContent = msg;
            grid.appendChild(el);
        }
    };

    async function fetchJson(url) {
        const res = await fetch(url, { headers: { Accept: 'application/json' } });
        if (!res.ok) {
            let msg = `HTTP ${res.status}`;
            try {
                const j = await res.json();
                if (j && (j.message || j.error)) msg += ` - ${j.message || j.error}`;
            } catch {}
            throw new Error('خطا در دریافت محصولات: ' + msg);
        }
        try {
            return await res.json();
        } catch {
            throw new Error('پاسخ سرور JSON نیست (احتمالاً HTML برگشته است).');
        }
    }

    function buildQueryFromUrl() {
        const params = new URLSearchParams(location.search);
        const keys = ['q', 'color', 'size', 'category', 'priceMin', 'priceMax', 'sort', 'page', 'size'];
        const qs = [];
        for (const k of keys) {
            const v = params.get(k);
            if (v) qs.push(`${k}=${encodeURIComponent(v)}`);
        }
        return qs.length ? `?${qs.join('&')}` : '';
    }

    function renderProducts(items) {
        grid.innerHTML = ''; // clear only when ready to render

        if (!items.length) {
            grid.innerHTML = '<div style="padding:12px">محصولی یافت نشد</div>';
            return;
        }

        for (const p of items) {
            const card = document.createElement('div');
            card.className = 'product-card';
            card.innerHTML = `
        <a href="/product.html?id=${p.id}">
          <img src="${p.imageUrl || '/images/placeholder.jpg.jpg'}" alt="${p.title || ''}" />
          <div class="product-name">${p.title || ''}</div>
          <div class="product-price">${(p.price ?? '').toString()} تومان</div>
        </a>
        <div class="icons" role="group" aria-label="actions">
          <a href="#" class="add-to-cart" data-id="${p.id}" title="افزودن به سبد">
            <i class="fas fa-shopping-cart"></i>
          </a>
        </div>
      `;
            grid.appendChild(card);
        }
    }

    async function load() {
        showLoading();
        try {
            const url = '/products' + buildQueryFromUrl();
            console.log('[all-products] fetching:', url);
            const data = await fetchJson(url);

            // accept both array and PageResponse shapes
            const items = Array.isArray(data) ? data : (data.content || []);
            if (!Array.isArray(items)) throw new Error('ساختار داده نامعتبر است (نه آرایه و نه PageResponse).');

            renderProducts(items);

            // delegated add-to-cart
            grid.addEventListener('click', async (e) => {
                const btn = e.target.closest('.add-to-cart');
                if (!btn) return;
                e.preventDefault();

                const sid = localStorage.getItem('sessionId');
                if (!sid) { alert('ابتدا وارد شوید'); location.href = '/login.html'; return; }

                try {
                    const r = await fetch('/cart/items', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json', 'X-Session-Id': sid },
                        body: JSON.stringify({ productId: Number(btn.dataset.id), quantity: 1 })
                    });
                    if (!r.ok) {
                        let msg = 'خطا در افزودن به سبد';
                        try { const j = await r.json(); if (j?.message) msg = j.message; } catch {}
                        alert(msg); return;
                    }
                    alert('به سبد افزوده شد');
                } catch (err) {
                    console.error(err);
                    alert('مشکل در ارتباط با سرور هنگام افزودن به سبد');
                }
            });

        } catch (err) {
            showError(err.message || 'خطای نامشخص در بارگذاری');
        } finally {
            hideLoading();
        }
    }

    load();
})();