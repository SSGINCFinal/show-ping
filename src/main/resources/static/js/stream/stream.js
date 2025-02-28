document.addEventListener("DOMContentLoaded", function () {
    loadLive();                 // 단일 라이브 방송 불러오기
    loadVods(0);        // 최초 1페이지의 VOD 목록 불러오기
});

function loadLive() {
    axios.get('/stream/live')
        .then(response => {
            console.log(response.data);
            const live = response.data['live'];
            const liveGrid = document.getElementById('live-grid');
            liveGrid.innerHTML = '';

            if (!live) {
                liveGrid.innerHTML = '<p>진행중인 라이브가 없습니다.</p>';
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
                    <p id="date">${formattedDate}</p>
                    <p id="title">${live.streamTitle}</p>
                    <p id="price">${formattedPrice}원</p>
                `;

                // 라이브 클릭 시 시청 및 상세 페이지로 이동
                liveDiv.addEventListener('click', () => {
                    window.location.href = `/watch/live/${live.streamNo}`;
                });
                liveGrid.appendChild(liveDiv);
            }
        })
        .catch(error => {
            console.error("라이브 목록을 불러오는 중 오류 발생:", error);
        });
}

function loadVods(pageNo) {
    // 현재 페이지의 VOD 목록 불러오기
    axios.get('/stream/vod/list/page', {
        params: {
            pageNo: pageNo
        }
    })
        .then(response => {
            const vodContent = pageInfo['content'];
            const vodGrid = document.getElementById('vod-grid');
            vodGrid.innerHTML = '';

            // VOD 정보 추가
            if (vodContent.length === 0) {
                vodGrid.innerHTML = '<p>등록된 VOD가 없습니다.</p>';
            } else {
                vodContent.forEach(vod => {
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

                    // VOD 클릭 시 상세 및 시청 페이지로 이동
                    vodDiv.addEventListener('click', () => {
                        window.location.href = `/watch/vod/${vod.streamNo}`;
                    });

                    vodGrid.appendChild(vodDiv);
                });

                // 페이지 버튼 영역 생성
                const pageInfo = response.data['pageInfo'];
                const pageContainer = document.getElementById('page-container');
                pageContainer.innerHTML = '';
                const totalPages = pageInfo['totalPages'];
                const pageNumber = pageInfo['number'];

                // 페이지 버튼 생성 (최대 3개까지 표현)
                var count = 0;
                for (var pages = Math.max(pageNumber - 1, 0); count < 3 && pages < totalPages; pages++) {
                    const current = pages;
                    const pageDiv = document.createElement('div');

                    // 현재 페이지 번호 버튼 추가
                    if (pageNumber === current) {
                        pageDiv.classList.add('page-item');
                        pageDiv.innerHTML = `<button class="current">${pages + 1}</button>`
                    }
                    // 이전 다음 페이지 번호 버튼 추가
                    else {
                        pageDiv.classList.add('page-item');
                        pageDiv.innerHTML = `<button class="prev-next">${pages + 1}</button>`
                    }

                    // 페이지 번호 클릭시 해당 페이지의 VOD 목록을 가져옴
                    pageDiv.addEventListener('click', () => {
                        loadVods(current);
                    });

                    pageContainer.appendChild(pageDiv);
                    count++;
                }
            }
        })
        .catch(error => {
            console.error("VOD 목록을 불러오는 중 오류 발생:", error);
        });
}