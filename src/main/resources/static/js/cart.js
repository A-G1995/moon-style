(function(){
    const sid = localStorage.getItem('sessionId');
    if (!sid) { alert('ابتدا وارد شوید'); location.href='/login.html'; return; }

    const $ = (id) => document.getElementById(id);
    const err = $('err');
    const cartBox = $('cartBox');
    const recsCard = $('recsCard');
    const recs = $('recs');

    function showErr(msg){ err.style.display='block'; err.textContent=msg; }
    function clearErr(){ err.style.display='none'; err.textContent=''; }

    async function loadCart(){
        clearErr();
        cartBox.innerHTML = '<div class="empty">در حال بارگذاری…</div>';
        try{
            const r = await fetch('/cart', { headers: { 'X-Session-Id': sid } });
            if(!r.ok){ showErr('خطا در دریافت سبد'); cartBox.innerHTML=''; return; }
            const j = await r.json();
            renderCart(j);
        }catch(e){ showErr('خطا در ارتباط با سرور'); }
    }

    function renderCart(data){
        const items = Array.isArray(data.items) ? data.items : [];
        if (items.length === 0) {
            cartBox.innerHTML = '<div class="empty">سبد شما خالی است</div>';
            return;
        }
        const rows = items.map(it => `
      <tr>
        <td>${it.title}</td>
        <td>${it.price}</td>
        <td>
          <input type="number" min="1" value="${it.quantity}" data-id="${it.productId}" class="qty" style="width:70px;text-align:center"/>
        </td>
        <td>${it.subtotal}</td>
        <td><button class="btn ghost rm" data-id="${it.productId}">حذف</button></td>
      </tr>
    `).join('');

        cartBox.innerHTML = `
      <table>
        <thead>
          <tr>
            <th>محصول</th><th>قیمت واحد</th><th>تعداد</th><th>جمع جزء</th><th>عملیات</th>
          </tr>
        </thead>
        <tbody>${rows}</tbody>
        <tfoot>
          <tr><th colspan="3" style="text-align:left;padding:10px 16px">جمع کل</th><th>${data.total}</th><th></th></tr>
        </tfoot>
      </table>
    `;
    }

    // تغییر تعداد (set quantity)
    cartBox.addEventListener('change', async (e)=>{
        if (!e.target.classList.contains('qty')) return;
        const productId = Number(e.target.dataset.id);
        const quantity = Number(e.target.value);
        if (quantity <= 0){ e.target.value = 1; return; }
        try{
            const r = await fetch('/cart/items', {
                method:'POST',
                headers:{'Content-Type':'application/json','X-Session-Id':sid},
                body: JSON.stringify({ productId, quantity })
            });
            if(!r.ok){
                const j = await r.json().catch(()=>({}));
                alert(j.message || 'خطا در بروزرسانی تعداد');
                return;
            }
            await loadCart();
            // آپدیت شمارش سبد در هدر (اگر listener گذاشتی)
            window.dispatchEvent(new CustomEvent('cart-updated'));
        }catch{ alert('خطا در ارتباط با سرور'); }
    });

    // حذف یک مورد
    cartBox.addEventListener('click', async (e)=>{
        const btn = e.target.closest('.rm');
        if(!btn) return;
        const productId = Number(btn.dataset.id);
        try{
            const r = await fetch('/cart/items/'+productId, {
                method:'DELETE',
                headers:{'X-Session-Id':sid}
            });
            if(!r.ok){ alert('حذف نشد'); return; }
            await loadCart();
            window.dispatchEvent(new CustomEvent('cart-updated'));
        }catch{ alert('خطا در ارتباط با سرور'); }
    });

    // حذف همه
    $('clearBtn').addEventListener('click', async ()=>{
        if(!confirm('همه‌ی اقلام سبد حذف شوند؟')) return;
        try{
            const r = await fetch('/cart', { method:'DELETE', headers:{'X-Session-Id':sid} });
            if(!r.ok){ alert('حذف نشد'); return; }
            await loadCart();
            window.dispatchEvent(new CustomEvent('cart-updated'));
        }catch{ alert('خطا در ارتباط با سرور'); }
    });

    // تسویه و پرداخت (checkout)
    $('checkoutBtn').addEventListener('click', async ()=>{
        clearErr();
        try{
            const r = await fetch('/orders/checkout', { method:'POST', headers:{'X-Session-Id':sid} });
            if(!r.ok){
                const j = await r.json().catch(()=>({}));
                showErr(j.message || 'خطا در ثبت سفارش');
                return;
            }
            const order = await r.json(); // { id,total,items,createdAt }
            renderAfterCheckout(order);
            window.dispatchEvent(new CustomEvent('cart-updated'));
        }catch(e){ showErr('خطا در ارتباط با سرور'); }
    });

    async function renderAfterCheckout(order){
        cartBox.innerHTML = `
      <div class="empty">
        سفارش شما با شماره <b>${order.id}</b> ثبت شد. جمع کل: <b>${order.total}</b>
      </div>
    `;
        // SNNA: پیشنهادها بر اساس مبلغ خرید
        try{
            const r = await fetch(`/recommendations/spend?amount=${encodeURIComponent(order.total)}&percent=20&limit=8`);
            if(!r.ok){ recsCard.style.display = 'none'; return; }
            const list = await r.json();
            if(!Array.isArray(list) || list.length===0){ recsCard.style.display='none'; return; }
            recs.innerHTML = list.map(p => `
        <a class="rec" href="/product.html?id=${p.id}">
          <img src="${p.imageUrl || '/images/placeholder.jpg'}" alt="${p.title||''}"/>
          <div class="t">${p.title||''}</div>
          <div class="p">${p.price} تومان</div>
        </a>
      `).join('');
            recsCard.style.display = '';
        }catch{ recsCard.style.display = 'none'; }
    }

    // init
    loadCart();
})();
