// /js/all-products.js
(function () {
    // جلوگیری از اجرای دوباره در صورت لود دوباره‌ی اسکریپت
    if (window.__allProductsBooted) return;
    window.__allProductsBooted = true;

    // کانتینرها
    const grid = document.getElementById('productsGrid') || document.querySelector('.grid-container');
    let errBox = document.getElementById('productsError');
    if (!grid) {
        console.error('[all-products] grid container not found (#productsGrid or .grid-container)');
        return;
    }
    if (!errBox) {
        // اگر جعبه خطا نیست، یکی بسازیم (غیراجباری ولی مفید)
        errBox = document.createElement('div');
        errBox.id = 'productsError';
        errBox.style.cssText = 'display:none;color:#c62828;background:#ffebee;border:1px solid #ffcdd2;padding:10px;border-radius:8px;margin:12px 0';
        grid.parentElement?.insertBefore(errBox, grid);
    }

    // حالت بارگذاری
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

    // نمایش خطا
    function showError(msg) {
        console.error('[all-products] ' + msg);
        errBox.textContent = msg;
        errBox.style.display = 'block';
    }
    function clearError() {
        errBox.style.display = 'none';
        errBox.textContent = '';
    }

    // واکشی JSON با خطای قابل‌خواندن
    async function fetchJson(url) {
        const res = await fetch(url, { headers: { Accept: 'application/json' } });
        if (!res.ok) {
            let msg = `HTTP ${res.status}`;
            try {
                const j = await res.json();
                if (j && (j.message || j.error)) msg += ` - ${j.message || j.error}`;
            } catch { /* ممکن است HTML باشد */ }
            throw new Error('خطا در دریافت محصولات: ' + msg);
        }
        try {
            return await res.json();
        } catch {
            throw new Error('پاسخ سرور JSON نیست (احتمالاً HTML برگشته است).');
        }
    }

    // ساخت کوئری از URL
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

    // کمک‌تابع: ساخت کارت محصول
    function productCardHtml(p) {
        const id = p.id;
        const title = p.title || '';
        const price = (p.price ?? '').toString();
        const img = p.imageUrl || '/images/placeholder.jpg';
        // onerror برای fallback اگر تصویر محصول خراب بود
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

    // رندر محصولات
    function renderProducts(items) {
        grid.innerHTML = ''; // فقط وقتی داده داریم خالی کن
        if (!items.length) {
            grid.innerHTML = '<div style="padding:12px">محصولی یافت نشد</div>';
            return;
        }
        const frag = document.createDocumentFragment();
        for (const p of items) {
            const wrapper = document.createElement('div');
            wrapper.innerHTML = productCardHtml(p);
            // انتقال تک‌فرزند به فرگمنت (برای عملکرد بهتر)
            frag.appendChild(wrapper.firstElementChild);
        }
        grid.appendChild(frag);
    }

    // افزودن به سبد (با قفل کردن دکمه تا اتمام درخواست)
    async function handleAddToCart(btn) {
        const sid = localStorage.getItem('sessionId');
        if (!sid) { alert('ابتدا وارد شوید'); location.href = '/login.html'; return; }

        if (btn.dataset.busy === '1') return; // ضد دابل‌کلیک
        btn.dataset.busy = '1';
        btn.style.opacity = '0.6';

        try {
            const r = await fetch('/cart/items', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'X-Session-Id': sid },
                body: JSON.stringify({ productId: Number(btn.dataset.id), quantity: 1 })
            });
            if (!r.ok) {
                let msg = 'خطا در افزودن به سبد';
                try { const j = await r.json(); if (j?.message) msg = j.message; } catch {}
                alert(msg);
                return;
            }
            // به هدر اطلاع بده تا شمارنده‌ی سبد را آپدیت کند
            window.dispatchEvent(new CustomEvent('cart-updated'));
            alert('به سبد افزوده شد');
        } catch (err) {
            console.error(err);
            alert('مشکل در ارتباط با سرور هنگام افزودن به سبد');
        } finally {
            btn.dataset.busy = '0';
            btn.style.opacity = '';
        }
    }

    // بارگذاری با fallback
    async function load() {
        clearError();
        showLoading();

        try {
            const qs = buildQueryFromUrl();
            let url = '/products' + qs;
            console.log('[all-products] fetching:', url);

            const data = await fetchJson(url);
            let items = Array.isArray(data) ? data : (data.content || []);
            if (!Array.isArray(items)) throw new Error('ساختار داده نامعتبر است (نه آرایه و نه PageResponse).');

            // اگر با فیلترها چیزی برنگشت، یک بار بدون فیلتر هم تست کن
            if (items.length === 0 && qs) {
                console.log('[all-products] empty with filters; trying without filters');
                const data2 = await fetchJson('/products');
                items = Array.isArray(data2) ? data2 : (data2.content || []);
                if (!Array.isArray(items)) items = [];
            }

            renderProducts(items);

            // رویدادهای delegated برای افزودن به سبد
            grid.addEventListener('click', (e) => {
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
