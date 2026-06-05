/**
 * Product Variant Selector
 * Handles option selection, variant matching, price/stock/image updates
 * Option A: Disable add-to-cart/buy-now buttons when variant not fully selected
 */
(() => {
    'use strict';

    const config = window.productVariantConfig;
    if (!config || !config.hasVariants) return;

    // State: track selected option values { optionId: valueId }
    const selectedOptions = {};
    const totalOptions = config.options.length;

    // DOM references
    const priceEl = document.querySelector('.text-brand-red.text-\\[32px\\]');
    const mainImageEl = document.querySelector('img[alt="Main Product Image"]');
    const addToCartVariantId = document.getElementById('add-to-cart-variant-id');
    const buyNowVariantId = document.getElementById('buy-now-variant-id');
    const variantStockInfo = document.getElementById('variant-stock-info');

    // Cart buttons to disable/enable
    const addToCartBtn = document.querySelector('#add-to-cart-quantity')?.closest('form')?.querySelector('button[type="submit"]');
    const buyNowBtn = document.querySelector('#buy-now-quantity')?.closest('form')?.querySelector('button[type="submit"]');

    /**
     * Find variant matching current selection
     */
    function findMatchingVariant() {
        if (Object.keys(selectedOptions).length !== totalOptions) return null;

        return config.variants.find(variant => {
            if (!variant.optionValues || variant.optionValues.length !== totalOptions) return false;
            return variant.optionValues.every(ov =>
                selectedOptions[ov.optionId] === ov.id
            );
        });
    }

    /**
     * Format price Vietnamese style
     */
    function formatPrice(price) {
        const num = Number(price);
        if (isNaN(num)) return '\u20ab0';
        return '\u20ab' + num.toLocaleString('vi-VN');
    }

    /**
     * Set cart buttons enabled/disabled state
     */
    function setCartButtonsState(enabled) {
        [addToCartBtn, buyNowBtn].forEach(btn => {
            if (!btn) return;
            if (enabled) {
                btn.disabled = false;
                btn.classList.remove('opacity-50', 'cursor-not-allowed');
            } else {
                btn.disabled = true;
                btn.classList.add('opacity-50', 'cursor-not-allowed');
            }
        });
    }

    /**
     * Update UI based on matched variant
     */
    function updateUI(variant) {
        if (variant) {
            // ✅ Variant found → enable buttons
            setCartButtonsState(true);

            // Update price
            if (priceEl) {
                priceEl.textContent = formatPrice(variant.price);
            }

            // Update stock info
            if (variantStockInfo) {
                const stock = variant.stockQuantity ?? 0;
                variantStockInfo.textContent = stock > 0
                    ? `${stock} s\u1ea3n ph\u1ea9m c\u00f3 s\u1eb5n`
                    : 'H\u1ebft h\u00e0ng';
                variantStockInfo.className = stock > 0
                    ? 'text-xs text-text-secondary ml-[122px]'
                    : 'text-xs text-brand-red font-medium ml-[122px]';

                // Disable buttons if variant is out of stock
                if (stock <= 0) {
                    setCartButtonsState(false);
                }
            }

            // Update main image if variant has thumbnail
            if (variant.thumbnailUrl && mainImageEl) {
                mainImageEl.src = variant.thumbnailUrl;
            }

            // Set variant ID in forms
            if (addToCartVariantId) addToCartVariantId.value = variant.id;
            if (buyNowVariantId) buyNowVariantId.value = variant.id;
        } else {
            // ❌ No match → disable buttons
            setCartButtonsState(false);

            if (variantStockInfo) {
                const allSelected = Object.keys(selectedOptions).length === totalOptions;
                if (allSelected) {
                    variantStockInfo.textContent = 'Kh\u00f4ng t\u00ecm th\u1ea5y bi\u1ebfn th\u1ec3 ph\u00f9 h\u1ee3p';
                    variantStockInfo.className = 'text-xs text-brand-red font-medium ml-[122px]';
                } else {
                    variantStockInfo.textContent = 'Vui l\u00f2ng ch\u1ecdn \u0111\u1ea7y \u0111\u1ee7 thu\u1ed9c t\u00ednh';
                    variantStockInfo.className = 'text-xs text-text-secondary ml-[122px]';
                }
            }

            // Clear variant IDs
            if (addToCartVariantId) addToCartVariantId.value = '';
            if (buyNowVariantId) buyNowVariantId.value = '';
        }
    }

    /**
     * Check if a specific option value is available given current selections
     */
    function isOptionValueAvailable(optionId, valueId) {
        const hypothetical = { ...selectedOptions, [optionId]: valueId };
        const partialKeys = Object.keys(hypothetical);
        return config.variants.some(variant => {
            if (!variant.optionValues) return false;
            return partialKeys.every(oId =>
                variant.optionValues.some(ov =>
                    ov.optionId === oId && ov.id === hypothetical[oId]
                )
            );
        });
    }

    /**
     * Handle option value click
     */
    function handleOptionClick(btn) {
        const optionId = btn.dataset.optionId;
        const valueId = btn.dataset.valueId;

        // Toggle selection
        if (selectedOptions[optionId] === valueId) {
            delete selectedOptions[optionId];
        } else {
            selectedOptions[optionId] = valueId;
        }

        refreshButtonStates();
        const variant = findMatchingVariant();
        updateUI(variant);
    }

    /**
     * Refresh visual state of all option buttons
     */
    function refreshButtonStates() {
        document.querySelectorAll('.option-value-btn').forEach(btn => {
            const optionId = btn.dataset.optionId;
            const valueId = btn.dataset.valueId;
            const isSelected = selectedOptions[optionId] === valueId;
            const isAvailable = isOptionValueAvailable(optionId, valueId);

            btn.classList.remove(
                'border-primary', 'bg-primary/10', 'text-primary', 'font-medium',
                'opacity-40', 'cursor-not-allowed', 'line-through'
            );

            if (isSelected) {
                btn.classList.add('border-primary', 'bg-primary/10', 'text-primary', 'font-medium');
            } else if (!isAvailable) {
                btn.classList.add('opacity-40', 'cursor-not-allowed', 'line-through');
                btn.disabled = true;
            } else {
                btn.disabled = false;
            }
        });
    }

    // Initialize: attach click handlers
    document.querySelectorAll('.option-value-btn').forEach(btn => {
        btn.addEventListener('click', () => handleOptionClick(btn));
    });

    // Initial state: disable cart buttons if variants exist but none selected
    refreshButtonStates();
    setCartButtonsState(false);
    updateUI(null);
})();
