document.addEventListener("DOMContentLoaded", function () {
    loadLive();
    loadVods();
});

function loadLive() {
    axios.get('/stream/live')
        .then(response => {
            console.log(response.data);
            const live = response.data['live'];
            const liveGrid = document.getElementById('live-grid');
            liveGrid.innerHTML = '';

            if (!live) {
                liveGrid.innerHTML = '<p>등록된 상품이 없습니다.</p>';
            } else {
                const liveDiv = document.createElement('div');
                liveDiv.classList.add('item');
                const productPrice = live.productPrice;
                const discountRate = live.productSale;
                const streamStartTime = live.streamStartTime;

                const discountedPrice = productPrice * ((100 - discountRate) / 100);
                const formattedPrice = discountedPrice.toLocaleString('ko-KR');

                const date = new Date(streamStartTime);

                // 년, 월, 일을 추출하여 포맷
                const formattedDate = `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;

                liveDiv.innerHTML = `
                    <img src="/img/product_img/${live.productImg}" alt="${live.productName}" />
                    <p id="ate">${formattedDate}</p>
                    <p id="title">${live.streamTitle}</p>
                    <p id="price">${formattedPrice}원</p>
                `;

                // 상품 클릭 시 상세 페이지로 이동
                liveDiv.addEventListener('click', () => {
                    window.location.href = `/watch/live/${live.streamNo}`;
                });
                liveGrid.appendChild(liveDiv);
            }
        })
        .catch(error => {
            console.error("상품 목록을 불러오는 중 오류 발생:", error);
        });
}

function loadVods() {
    axios.get('/stream/vod/list')
        .then(response => {
            console.log(response.data);
            const vodList = response.data['vodList'];
            const vodGrid = document.getElementById('vod-grid');
            vodGrid.innerHTML = '';

            if (vodList.length === 0) {
                vodGrid.innerHTML = '<p>등록된 상품이 없습니다.</p>';
            } else {
                vodList.forEach(vod => {
                    const vodDiv = document.createElement('div');
                    vodDiv.classList.add('item');
                    const productPrice = vod.productPrice;
                    const discountRate = vod.productSale;
                    const streamStartTime = vod.streamStartTime;

                    const discountedPrice = productPrice * ((100 - discountRate) / 100);
                    const formattedPrice = discountedPrice.toLocaleString('ko-KR');

                    const date = new Date(streamStartTime);

                    // 년, 월, 일을 추출하여 포맷
                    const formattedDate = `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;

                    vodDiv.innerHTML = `
                        <img src="/img/product_img/${vod.productImg}" alt="${vod.productName}" />
                        <p id="date">${formattedDate}</p>
                        <p id="title">${vod.streamTitle}</p>
                        <p id="price">${formattedPrice}원</p>
                    `;

                    // 상품 클릭 시 상세 페이지로 이동
                    vodDiv.addEventListener('click', () => {
                        window.location.href = `/watch/vod/${vod.streamNo}`;
                    });

                    vodGrid.appendChild(vodDiv);
                });
            }
        })
        .catch(error => {
            console.error("상품 목록을 불러오는 중 오류 발생:", error);
        });
}