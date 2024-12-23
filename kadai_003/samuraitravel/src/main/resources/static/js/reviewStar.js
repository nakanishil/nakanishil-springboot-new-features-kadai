console.log("reviewStar.js is loaded!");

function renderPartialStars(target, rating) {
    console.log("Render Stars for:", target, "Rating:", rating); // デバッグログ

    const starContainer = typeof target === 'string' 
        ? document.querySelector(target)
        : target;

    if (!starContainer) {
        console.error("Star container not found for target:", target);
        return;
    }

    // 子要素をすべて削除してクリア（重複防止）
    while (starContainer.firstChild) {
        starContainer.removeChild(starContainer.firstChild);
    }

    // 塗りつぶし率を計算
    const percentage = (rating / 5) * 100;

    // 背景用の星
    const starsBackground = document.createElement('div');
    starsBackground.className = 'stars-background';
    starsBackground.textContent = '★★★★★'; // 背景の星

    // 塗りつぶし用の星
    const starsFilled = document.createElement('div');
    starsFilled.className = 'stars-filled';
    starsFilled.style.width = `${percentage}%`;
    starsFilled.textContent = '★★★★★'; // 塗りつぶし部分

    // 星の背景と塗りつぶしを追加
    starContainer.appendChild(starsBackground);
    starContainer.appendChild(starsFilled);

    console.log("Rendered stars for", target, "Percentage filled:", percentage);
}


// 星評価のインタラクション設定（投稿ページ専用）
function setupStarRatingInteraction(containerSelector) {
    console.log("Initializing star rating interaction...");

    const starsContainer = document.querySelector(containerSelector);
    if (!starsContainer) {
        console.warn(`Stars container not found for selector: ${containerSelector}`);
        return;
    }

    console.log("Found stars container:", starsContainer);

    const stars = starsContainer.querySelectorAll('.star');
    if (stars.length === 0) {
        console.warn("No stars found in the container.");
        return;
    }

    console.log(`Found ${stars.length} stars in the container.`);

    const evaluationInput = starsContainer.querySelector('input[name="evaluation"]');
    if (!evaluationInput) {
        console.warn("Evaluation input not found in the container.");
        return;
    }

    console.log("Found evaluation input:", evaluationInput);

    // 初期化（既存の評価値をハイライト）
    if (evaluationInput.value) {
        const initialRating = parseInt(evaluationInput.value, 10);
        stars.forEach((star, index) => {
            if (index < initialRating) {
                star.classList.add('selected');
            }
        });
        console.log(`Initialized with rating: ${initialRating}`);
    }

    // 星をクリックした時の処理
    stars.forEach(star => {
        const value = parseInt(star.getAttribute('data-value'), 10);
        star.addEventListener('click', () => {
            console.log(`Star clicked with value: ${value}`);
            evaluationInput.value = value;

            // 全ての星をリセット
            stars.forEach(s => s.classList.remove('selected'));

            // 選択した星までをハイライト
            for (let i = 0; i < value; i++) {
                stars[i].classList.add('selected');
            }
            console.log(`Stars highlighted up to value: ${value}`);
        });
    });
}


// 平均評価星を描画（民宿詳細ページ）
function initOverallRating() {
    const overallRatingElement = document.getElementById('overall-rating-value');
    if (overallRatingElement) {
        const overallRating = parseFloat(overallRatingElement.textContent || '0');
        renderPartialStars('#overall-stars', overallRating);
    }
}

// 各レビューの星を描画
function initReviewRatings() {
    document.querySelectorAll('.review-rating').forEach(review => {
        const rating = parseFloat(review.dataset.rating || '0');
        renderPartialStars(review, rating);
    });
}


// 削除モーダルのセットアップ
document.addEventListener('DOMContentLoaded', function () {
    const deleteModal = document.getElementById('deleteConfirmModal');
    const deleteForm = document.getElementById('deleteForm');

    if (deleteModal && deleteForm) {
        deleteModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget; 
            const reviewId = button.getAttribute('data-review-id');
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

            if (reviewId) {
                deleteForm.action = `/reviews/${reviewId}/delete`;
                console.log("Delete form action set to:", deleteForm.action);

                // CSRFトークンを動的に追加
                let csrfInput = deleteForm.querySelector('input[name="_csrf"]');
                if (!csrfInput) {
                    csrfInput = document.createElement('input');
                    csrfInput.type = 'hidden';
                    csrfInput.name = '_csrf';
                    deleteForm.appendChild(csrfInput);
                }
                csrfInput.value = csrfToken;
                console.log("CSRF token set:", csrfToken);
            }
        });
    }
});



// DOMContentLoadedで初期化
document.addEventListener('DOMContentLoaded', () => {
    console.log("Initializing review ratings...");

    // 総合評価の星を描画
    const overallRatingElement = document.getElementById('overall-rating-value');
    if (overallRatingElement) {
        const overallRating = parseFloat(overallRatingElement.textContent || '0');
        renderPartialStars('#overall-stars', overallRating); // 総合評価を描画
    }

    // 各レビューの星を描画
    document.querySelectorAll('.review-rating').forEach((review, index) => {
        const rating = parseFloat(review.dataset.rating || '0');
        console.log(`Review #${index + 1}: Rating = ${rating}`);
        renderPartialStars(review, rating);
    });

    // 投稿ページ用の星評価の初期化
    const postStarsContainer = document.querySelector('.post-stars-container');
    if (postStarsContainer) {
        console.log("Initializing star rating interaction for post page...");
        setupStarRatingInteraction('.post-stars-container');
    } else {
        console.log("post-stars-container not found. Skipping post page-specific logic.");
    }
});

function editReview(reviewId) {
    fetch(`/reviews/${reviewId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            // 編集データをここに記載
            review: "更新されたレビュー内容",
            evaluation: 5 // 評価の例
        }),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log("Review updated:", data);
    })
    .catch(error => {
        console.error("Error updating review:", error);
    });
}




