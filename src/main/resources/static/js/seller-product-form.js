(function() {
    const _opts = [];

    window.addOption = function() {
        _opts.push({ name: '', values: [''] });
        renderOptions();
        generateVariants();
    };
    window.removeOption = function(oi) { _opts.splice(oi, 1); renderOptions(); generateVariants(); };
    window.updateOptionName = function(oi, val) { _opts[oi].name = val; generateVariants(); };
    window.addValue = function(oi) { _opts[oi].values.push(''); renderOptions(); generateVariants(); };
    window.removeValue = function(oi, vi) {
        if (_opts[oi].values.length <= 1) return;
        _opts[oi].values.splice(vi, 1); renderOptions(); generateVariants();
    };
    window.updateValue = function(oi, vi, val) { _opts[oi].values[vi] = val; generateVariants(); };

    function esc(s) {
        return String(s||'').replace(/&/g,'&amp;').replace(/"/g,'&quot;').replace(/'/g,'&#39;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    }

    function renderOptions() {
        const c = document.getElementById('options-container');
        c.innerHTML = '';
        _opts.forEach(function(opt, oi) {
            const div = document.createElement('div');
            div.className = 'bg-gray-50 rounded-xl p-3 space-y-2';
            let vh = '';
            opt.values.forEach(function(v, vi) {
                vh += '<div class="flex items-center gap-2">' +
                    '<input type="text" value="' + esc(v) + '" onchange="updateValue('+oi+','+vi+',this.value)" ' +
                    'class="flex-1 px-3 py-1.5 rounded-lg border border-gray-200 text-sm focus:border-brand-blue outline-none" placeholder="Giá trị">' +
                    (opt.values.length > 1 ? '<button type="button" onclick="removeValue('+oi+','+vi+')" class="text-gray-400 hover:text-red-500"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg></button>' : '') +
                    '</div>';
            });
            div.innerHTML = '<div class="flex items-center gap-2">' +
                '<input type="text" value="' + esc(opt.name) + '" onchange="updateOptionName('+oi+',this.value)" ' +
                'class="flex-1 px-3 py-1.5 rounded-lg border border-gray-200 text-sm font-medium focus:border-brand-blue outline-none" placeholder="Tên tùy chọn">' +
                '<button type="button" onclick="addValue('+oi+')" class="text-xs text-brand-blue hover:underline">+ Giá trị</button>' +
                '<button type="button" onclick="removeOption('+oi+')" class="text-gray-400 hover:text-red-500"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg></button>' +
                '</div><div class="pl-2 space-y-1.5">' + vh + '</div>';
            c.appendChild(div);
        });
    }

    function cartesian(arrs) {
        if (!arrs.length) return [];
        return arrs.reduce(function(acc, cur) {
            let res = [];
            acc.forEach(function(a) { cur.forEach(function(c) { res.push(a.concat([c])); }); });
            return res;
        }, [[]]);
    }

    function getGlobalIdx(opt, val) {
        let idx = 0;
        for (let i = 0; i < _opts.length; i++) {
            if (_opts[i] === opt) return idx + _opts[i].values.indexOf(val);
            idx += _opts[i].values.length;
        }
        return idx;
    }

    function generateVariants() {
        const sec = document.getElementById('variant-section');
        const tb = document.getElementById('variant-table-body');
        tb.innerHTML = '';
        const valid = _opts.filter(function(o) { return o.name.trim() && o.values.some(function(v) { return v.trim(); }); });
        if (!valid.length) { sec.classList.add('hidden'); return; }
        sec.classList.remove('hidden');
        const va = valid.map(function(o) {
            return o.values.filter(function(v) { return v.trim(); }).map(function(v) {
                return { label: v, gi: getGlobalIdx(o, v) };
            });
        });
        const combos = cartesian(va);
        combos.forEach(function(combo, ci) {
            const label = combo.map(function(c) { return c.label; }).join(' / ');
            const tr = document.createElement('tr');
            tr.className = 'border-t border-gray-100';
            let h = '<td class="px-3 py-2 text-gray-600">' + esc(label) + '</td>' +
                '<td class="px-3 py-2"><input type="text" name="variants['+ci+'].sku" required class="w-full px-2 py-1.5 rounded-lg border border-gray-200 text-sm focus:border-brand-blue outline-none" placeholder="SKU"></td>' +
                '<td class="px-3 py-2"><input type="number" step="0.01" name="variants['+ci+'].price" required min="0" class="w-full px-2 py-1.5 rounded-lg border border-gray-200 text-sm focus:border-brand-blue outline-none" placeholder="0"></td>' +
                '<td class="px-3 py-2"><input type="number" name="variants['+ci+'].stockQuantity" min="0" value="0" class="w-full px-2 py-1.5 rounded-lg border border-gray-200 text-sm focus:border-brand-blue outline-none"></td>';
            combo.forEach(function(c, ii) {
                h += '<input type="hidden" name="variants['+ci+'].optionValueIndexes['+ii+']" value="'+c.gi+'">';
            });
            tr.innerHTML = h;
            tb.appendChild(tr);
        });
    }

    document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('form');
        if (form) {
            form.addEventListener('submit', function() {
                document.querySelectorAll('input[name^="options["]').forEach(function(el) { el.remove(); });
                _opts.forEach(function(opt, oi) {
                    const ni = document.createElement('input'); ni.type='hidden'; ni.name='options['+oi+'].name'; ni.value=opt.name; form.appendChild(ni);
                    opt.values.forEach(function(v, vi) {
                        const vi2 = document.createElement('input'); vi2.type='hidden'; vi2.name='options['+oi+'].values['+vi+'].value'; vi2.value=v; form.appendChild(vi2);
                    });
                });
            });
        }
    });
})();
