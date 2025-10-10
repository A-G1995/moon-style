// /js/admin.js
(function(){
    if (window.__adminBooted) return; window.__adminBooted = true;

    const sid = localStorage.getItem('sessionId');
    const isAdmin = localStorage.getItem('isAdmin') === 'true';
    const errEl = document.getElementById('adminError');

    const showErr = (m)=>{ if(errEl){ errEl.textContent=m; errEl.style.display='block'; } };

    if (!sid) { alert('برای دسترسی به پنل ادمین ابتدا وارد شوید'); location.href = '/login.html'; return; }
    if (!isAdmin) { alert('دسترسی شما مجاز نیست'); location.href = '/all-products.html'; return; }

    async function loadPie(){
        try{
            const r = await fetch('/admin/stats/sales-by-category', {
                headers: { 'X-Session-Id': sid, 'Accept':'application/json' }
            });
            if (r.status === 401 || r.status === 403){
                showErr('دسترسی شما مجاز نیست'); return;
            }
            if (!r.ok){
                showErr('بارگذاری گزارش ناموفق بود'); return;
            }
            const data = await r.json(); // { labels:[], values:[] }
            if (!Array.isArray(data.labels) || data.labels.length === 0){
                showErr('داده‌ای برای نمایش وجود ندارد'); return;
            }
            renderPie(data.labels, data.values);
        }catch(e){
            console.error(e);
            showErr('خطا در اتصال به سرور');
        }
    }

    function renderPie(labels, values){
        const ctx = document.getElementById('salesPie').getContext('2d');

        const palette = [
            '#4e79a7','#f28e2b','#e15759','#76b7b2','#59a14f',
            '#edc949','#af7aa1','#ff9da7','#9c755f','#bab0ab'
        ];
        const colors = labels.map((_,i)=> palette[i % palette.length]);

        new Chart(ctx, {
            type: 'pie',
            data: {
                labels,
                datasets: [{
                    data: values,
                    backgroundColor: colors,
                    borderWidth: 1
                }]
            },
            options: {
                plugins: {
                    legend: { position: 'right', labels: { font: { family: 'Tahoma' } } },
                    tooltip: {
                        callbacks: {
                            label: (ctx) => {
                                const val = ctx.parsed;
                                const total = values.reduce((a,b)=>a+b,0) || 1;
                                const pct = Math.round((val/total)*100);
                                return `${ctx.label}: ${val.toLocaleString('fa-IR')} عدد (${pct}٪)`;
                            }
                        }
                    }
                }
            }
        });
    }

    loadPie();
})();
