(function() {
    const cfg = window.reviewConfig;
    if (!cfg) return;

    const section = document.getElementById('product-review-section');
    if (!section) return;

    const productId = cfg.productId;
    const authenticated = cfg.authenticated;
    const i18n = cfg.i18n;

    const list = document.getElementById('review-list');
    const empty = document.getElementById('review-empty');
    const summary = document.getElementById('review-summary');
    const average = document.getElementById('review-average');
    const averageStars = document.getElementById('review-average-stars');
    const loadMore = document.getElementById('review-load-more');
    const formWrap = document.getElementById('review-form-wrap');
    const notEligibleMessage = document.getElementById('review-not-eligible-message');
    const form = document.getElementById('review-form');
    const orderSelect = document.getElementById('review-order-id');
    const ratingSelect = document.getElementById('review-rating');
    const commentInput = document.getElementById('review-comment');
    const formMessage = document.getElementById('review-form-message');
    const reviewAverage = document.getElementById('review-average');
    const reviewAverageStars = document.getElementById('review-average-stars');
    const topRating = document.getElementById('product-top-rating');
    const topNoRating = document.getElementById('product-top-no-rating');
    const topAverage = document.getElementById('product-top-average');
    const topStars = document.getElementById('product-top-stars');
    const topCount = document.getElementById('product-top-count');

    let currentPage = 0;
    let reviewCount = Number(section.dataset.reviewCount || 0);
    let averageRating = Number(section.dataset.averageRating || 0);
    const pageSize = 5;

    const stars = (rating) => '★★★★★'.slice(0, Math.round(rating)) + '☆☆☆☆☆'.slice(Math.round(rating));

    const highlightStars = (rating) => {
        const buttons = formWrap.querySelectorAll('.core-star-container button[data-star]');
        buttons.forEach(btn => {
            const starVal = Number(btn.dataset.star);
            if (starVal <= rating) {
                btn.classList.add('text-amber-400');
                btn.classList.remove('text-gray-300');
            } else {
                btn.classList.remove('text-amber-400');
                btn.classList.add('text-gray-300');
            }
        });
    };

    const bindStarEvents = () => {
        const buttons = formWrap.querySelectorAll('.core-star-container button[data-star]');
        buttons.forEach(btn => {
            btn.addEventListener('click', () => {
                const val = Number(btn.dataset.star);
                ratingSelect.value = val;
                highlightStars(val);
            });
            btn.addEventListener('mouseenter', () => highlightStars(Number(btn.dataset.star)));
            btn.addEventListener('mouseleave', () => highlightStars(Number(ratingSelect.value)));
        });
    };

    const renderRatingSummary = () => {
        if (reviewCount > 0) {
            average.innerHTML = `<span data-rating-value data-rating="${averageRating}"></span>/5`;
            average.hidden = false;
            document.getElementById('review-average-no-rating')?.setAttribute('hidden', 'hidden');
            averageStars.hidden = false;
            averageStars.dataset.rating = averageRating;
            summary.textContent = `· ${reviewCount} ${i18n.reviewCount}`;
        } else {
            average.hidden = true;
            var noRating = document.getElementById('review-average-no-rating');
            if (noRating) {
                noRating.removeAttribute('hidden');
                noRating.textContent = i18n.noRating;
            }
            averageStars.hidden = true;
            summary.textContent = '';
        }
        if (typeof applyRatingDisplay === 'function') applyRatingDisplay(section);
    };

    const escapeHtml = (value) => {
        const div = document.createElement('div');
        div.textContent = value ?? '';
        return div.innerHTML;
    };

    const formatDate = (value) => {
        if (!value) return '';
        return new Intl.DateTimeFormat('vi-VN', {
            dateStyle: 'medium',
            timeStyle: 'short'
        }).format(new Date(value));
    };

    const renderReview = (review) => {
        const item = document.createElement('article');
        item.className = 'rounded-lg border border-border bg-white p-4';
        item.innerHTML = `
            <div class="flex items-start justify-between gap-3">
                <div>
                    <div class="font-semibold text-text-primary">${escapeHtml(review.userName || i18n.user)}</div>
                    <div class="text-brand-red text-sm mt-1">${stars(Number(review.rating || 0))}</div>
                </div>
                <time class="text-xs text-text-secondary">${formatDate(review.createdAt)}</time>
            </div>
            <p class="text-sm text-text-primary leading-relaxed mt-3 whitespace-pre-line">${escapeHtml(review.comment || i18n.noComment)}</p>
        `;
        list.appendChild(item);
    };

    const loadReviews = async (page = 0) => {
        try {
            const res = await fetch(`/api/products/${productId}/reviews?page=${page}&size=${pageSize}`, {
                headers: { 'Accept': 'application/json' }
            });
            if (!res.ok) throw new Error(i18n.loadError);

            const data = await res.json();
            const reviews = data.content || [];

            if (page === 0) {
                list.innerHTML = '';
                empty.classList.toggle('hidden', reviews.length > 0);
            }

            reviews.forEach(renderReview);
            currentPage = data.number || page;
            loadMore.classList.toggle('hidden', data.last === true || reviews.length === 0);
        } catch (error) {
            summary.textContent = error.message;
            loadMore.classList.add('hidden');
        }
    };

    const loadEligibleOrders = async () => {
        formWrap.classList.add('hidden');
        notEligibleMessage.classList.add('hidden');
        if (!authenticated) return;

        try {
            const res = await fetch(`/api/products/${productId}/reviews/eligible-orders`, {
                headers: { 'Accept': 'application/json' }
            });
            if (!res.ok) {
                notEligibleMessage.classList.remove('hidden');
                return;
            }

            const orderIds = await res.json();
            orderSelect.innerHTML = '';

            if (!orderIds.length) {
                notEligibleMessage.classList.remove('hidden');
                return;
            }

            orderIds.forEach((id) => {
                const option = document.createElement('option');
                option.value = id;
                option.textContent = `${i18n.order} ${id}`;
                orderSelect.appendChild(option);
            });

            formWrap.classList.remove('hidden');
            bindStarEvents();
            highlightStars(Number(ratingSelect.value));
        } catch (_) {
            notEligibleMessage.classList.remove('hidden');
        }
    };

    topRating?.addEventListener('click', (event) => {
        event.preventDefault();
        section.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });

    loadMore.addEventListener('click', () => loadReviews(currentPage + 1));

    form?.addEventListener('submit', async (event) => {
        event.preventDefault();
        formMessage.textContent = i18n.sending;
        formMessage.className = 'text-sm text-text-secondary';

        try {
            const res = await fetch(`/api/products/${productId}/reviews`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({
                    orderId: orderSelect.value,
                    rating: Number(ratingSelect.value),
                    comment: commentInput.value.trim()
                })
            });

            if (!res.ok) {
                let message = i18n.submitError;
                try {
                    const error = await res.json();
                    message = error.message || error.error || message;
                } catch (_) {}
                throw new Error(message);
            }

            const submittedRating = Number(ratingSelect.value);
            averageRating = reviewCount > 0
                ? ((averageRating * reviewCount) + submittedRating) / (reviewCount + 1)
                : submittedRating;
            reviewCount += 1;
            renderRatingSummary();

            var topStarsEl = document.getElementById('product-top-stars');
            if (topStarsEl) topStarsEl.dataset.rating = averageRating;

            var topValue = document.getElementById('product-top-average');
            if (topValue) topValue.dataset.rating = averageRating;

            var topCountEl = document.getElementById('product-top-count');
            if (topCountEl) topCountEl.textContent = `(${reviewCount} ${i18n.reviewCount})`;

            if (typeof applyRatingDisplay === 'function') applyRatingDisplay(document);

            formMessage.textContent = i18n.submitted;
            formMessage.className = 'text-sm text-green-600';
            commentInput.value = '';
            await loadReviews(0);
            await loadEligibleOrders();
        } catch (error) {
            formMessage.textContent = error.message;
            formMessage.className = 'text-sm text-red-600';
        }
    });

    renderRatingSummary();
    loadReviews(0);
    loadEligibleOrders();
})();
