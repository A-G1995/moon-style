(function(){
    const sid = localStorage.getItem('sessionId');
    if (!sid) { alert('ابتدا وارد شوید'); location.href='/login.html'; return; }

    const $ = (id) => document.getElementById(id);
    const errBox = $('err');
    const fields = ['fullName','email','phoneNumber','nationalNumber','birthDate'];

    function setDisabled(dis){ for(const f of fields){ $(f).disabled = dis; } }
    function clearErrors(){
        errBox.style.display='none'; errBox.textContent='';
        document.querySelectorAll('[data-error]').forEach(e=> e.textContent='');
    }
    function showErrors(map){
        for(const [k,msgs] of Object.entries(map)){
            const box = document.querySelector(`[data-field="${k}"] [data-error]`);
            if (box) box.textContent = Array.isArray(msgs) ? msgs.join('، ') : String(msgs);
        }
    }
    async function extractFieldErrors(res){
        let data={};
        try{ data = await res.json(); }catch{ return {global:'خطای نامشخص'}; }
        const out = {};
        const list = [];
        if(Array.isArray(data.errors)) list.push(...data.errors);
        if(Array.isArray(data.fieldErrors)) list.push(...data.fieldErrors);
        if(Array.isArray(data.violations)) list.push(...data.violations);
        if(Array.isArray(data.allErrors)) list.push(...data.allErrors);
        if(Array.isArray(data) && data[0]?.defaultMessage) list.push(...data);
        for(const e of list){
            const field = e.field || e.propertyPath || e.objectName || 'global';
            const msg = e.defaultMessage || e.message || e.error || 'خطا';
            (out[field] ||= []).push(msg);
        }
        if(!Object.keys(out).length && (data.message || typeof data==='string')) out.global = [data.message || String(data)];
        return out;
    }

    // اعداد فارسی/عربی → انگلیسی
    function toEnglishDigits(str){
        return (str||'')
            .replace(/[۰-۹]/g, d => String(d.charCodeAt(0) - '۰'.charCodeAt(0)))
            .replace(/[٠-٩]/g, d => String(d.charCodeAt(0) - '٠'.charCodeAt(0)));
    }

    // بارگذاری پروفایل
    (async function load(){
        try{
            const r = await fetch('/account/me', { headers: { 'X-Session-Id': sid } });
            if(!r.ok){ throw new Error('عدم دسترسی یا خطا در دریافت پروفایل'); }
            const j = await r.json();
            $('fullName').value = j.fullName || '';
            $('email').value = j.email || '';
            $('phoneNumber').value = j.phoneNumber || '';
            $('nationalNumber').value = j.nationalNumber || '';
            $('birthDate').value = j.birthDate || '';
        }catch(e){
            errBox.style.display='block'; errBox.textContent = e.message || 'خطا در بارگذاری پروفایل';
        }
    })();

    // ویرایش → فعال کردن ورودی‌ها
    $('editBtn').addEventListener('click', ()=>{
        clearErrors();
        setDisabled(false);
        $('editBtn').style.display='none';
        $('saveBtn').style.display='';
        $('cancelBtn').style.display='';
    });

    // انصراف → ریفرش
    $('cancelBtn').addEventListener('click', ()=> location.reload());

    // ذخیره → PUT /account/me
    $('saveBtn').addEventListener('click', async ()=>{
        clearErrors();

        const payload = {
            fullName: $('fullName').value.trim(),
            email: $('email').value.trim(),
            phoneNumber: toEnglishDigits($('phoneNumber').value.trim()),
            nationalNumber: toEnglishDigits($('nationalNumber').value.trim()),
            birthDate: $('birthDate').value.trim() || null
        };

        try{
            const r = await fetch('/account/me', {
                method:'PUT',
                headers: { 'Content-Type':'application/json', 'X-Session-Id': sid },
                body: JSON.stringify(payload)
            });
            if(!r.ok){
                const map = await extractFieldErrors(r);
                if (map.global) { errBox.style.display='block'; errBox.textContent = map.global.join(' | '); }
                showErrors(map);
                return;
            }
            const j = await r.json();
            setDisabled(true);
            $('saveBtn').style.display='none';
            $('cancelBtn').style.display='none';
            $('editBtn').style.display='';
            alert('پروفایل با موفقیت بروزرسانی شد');

            // نام/ایمیل برای هدر
            if (j.fullName) localStorage.setItem('fullName', j.fullName);
            if (j.email) localStorage.setItem('email', j.email);
        }catch(e){
            errBox.style.display='block';
            errBox.textContent = 'خطا در ارتباط با سرور';
        }
    });
})();
