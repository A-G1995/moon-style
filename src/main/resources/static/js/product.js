// /js/product.js
(async function(){
    const params = new URLSearchParams(location.search);
    const id = Number(params.get('id'));
    if (!id) { document.body.innerHTML = 'شناسه محصول نامعتبر است'; return; }

    // واکشی محصول
    const res = await fetch('/products/' + id);
    if (!res.ok) { document.body.innerHTML = 'محصول یافت نشد'; return; }
    const p = await res.json();

    // مقداردهی به DOM
    document.querySelector('.product-name').textContent = p.title || '';
    document.querySelector('#priceBox').textContent = `${(p.price ?? '').toString()} تومان`;
    document.querySelector('.price').textContent = `${(p.price ?? '').toString()} تومان`;
    document.querySelector('.color').textContent = p.color || '-';
    document.querySelector('.size').textContent = p.size || '-';
    document.querySelector('.category').textContent = p.category || '-';
    document.querySelector('.stock').textContent = p.stockQty ?? '-';
    const img = document.querySelector('.product-image');
    if (img) img.src = p.imageUrl || '/images/placeholder.jpg.jpg';

    // افزودن به سبد
    document.getElementById('addBtn')?.addEventListener('click', async () => {
        const sid = localStorage.getItem('sessionId');
        if (!sid) { alert('ابتدا وارد شوید'); location.href='/login.html'; return; }
        const r = await fetch('/cart/items', {
            method:'POST',
            headers:{'Content-Type':'application/json','X-Session-Id':sid},
            body: JSON.stringify({ productId: id, quantity: 1 })
        });
        if (!r.ok) {
            const msg = (await r.json().catch(()=>({message:'خطا'}))).message || 'خطا در افزودن';
            alert(msg); return;
        }
        alert('به سبد افزوده شد');
    });
})();