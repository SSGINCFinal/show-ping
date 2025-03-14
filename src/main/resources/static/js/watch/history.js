function loadWatchHistory(memberNo) {
    axios.get(`/watch/history/${memberNo}`)
        .then(response => {
            const historyItems = response.data['historyList'];
            console.log(historyItems);
            const tableBody = document.querySelector(".history-items tbody");

            tableBody.innerHTML = ""; // 기존 데이터 초기화

            historyItems.forEach(item => {
                const date = new Date(item.watchTime);
                const watchDate = date.toLocaleDateString();
                console.log(watchDate);
                const row = `
                    <tr>
                        <td class="product-order">
                            <img class="product-img" src="/img/product_img/${item.productImg}" alt="${item.productName}">
                        </td>
                        <td class="stream-title" data-stream-title="${item.streamTitle}">
                            ${item.streamTitle}
                        </td>
                        <td class="product-name" data-product-name="${item.productName}">
                            ${item.productName}
                        </td>
                        <td class="product-price" data-product-price="${item.productPrice}">
                            ${(item.productPrice).toLocaleString('ko-KR')}원
                        </td>
                        <td class="watch-time" data-stream-time="${item.watchTime}">
                            ${watchDate}
                        </td>
                        <td>
                            <button class="watch-button" data-stream-no="${item.streamNo}">시청</button>
                        </td>
                    </tr>
                `;

                tableBody.innerHTML += row;
            });

            // 동적으로 추가된 시청 버튼에 이벤트 리스너 추가
            document.querySelectorAll(".watch-button").forEach(button => {
                button.addEventListener("click", function() {
                    const streamNo = this.getAttribute("data-stream-no");
                    if (streamNo) {
                        window.location.href = `/watch/vod/${streamNo}`;
                    }
                });
            });
        })
        .catch(error => {
            console.error("시청 이력 데이터를 불러오는 중 오류 발생:", error);
        });
}