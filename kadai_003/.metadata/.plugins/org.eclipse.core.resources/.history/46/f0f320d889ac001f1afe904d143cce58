document.addEventListener('DOMContentLoaded', () => {
    const submitButton = document.getElementById('favorite-submit');
    const csrfMeta = document.querySelector('meta[name="_csrf"]');
    const csrfToken = csrfMeta ? csrfMeta.getAttribute('content') : null;

    if (!csrfToken) {
        console.error("CSRFトークンが見つかりません");
        return;
    }

    submitButton.addEventListener('click', () => {
        const houseId = document.getElementById('house-id').value; // 宿ID
        const comment = document.getElementById('favorite-comment').value.trim(); // コメント

        console.log("送信データ:", { houseId, comment });

        fetch('/favorites', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify({
                comment: comment,
                house: { id: houseId }
            })
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(`HTTP error! status: ${response.status}, message: ${text}`);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log("レスポンスデータ:", data);
            alert("お気に入りに追加しました！");

            // モーダルを閉じる
            const favoriteModal = document.getElementById('favoriteModal');

            // Bootstrap 5
            if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
                const modalInstance = bootstrap.Modal.getInstance(favoriteModal);
                if (modalInstance) {
                    modalInstance.hide();
                }
            } else {
                // Bootstrap 4
                $(favoriteModal).modal('hide');
            }

            // 必要ならコメント欄をリセット
            document.getElementById('favorite-comment').value = '';
        })
        .catch(error => {
            console.error("エラー:", error.message);
            alert("エラーが発生しました: " + error.message);
        });
    });
});
