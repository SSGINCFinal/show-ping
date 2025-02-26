document.addEventListener("DOMContentLoaded", function () {
    const categoryNo = window.location.pathname.split('/').pop(); // URL에서 categoryNo 추출
    loadProducts(categoryNo);
});

function loadProducts(categoryNo) {
    axios.get(`/api/products/${categoryNo}`)
        .then(response => {
            const products = response.data;
            const productGrid = document.getElementById('product-grid');
            productGrid.innerHTML = '';

            if (products.length === 0) {
                productGrid.innerHTML = '<p>등록된 상품이 없습니다.</p>';
            } else {
                products.forEach(product => {
                    const productDiv = document.createElement('div');
                    productDiv.classList.add('product-item');
                    const formattedPrice = product.productPrice.toLocaleString('ko-KR');

                    productDiv.innerHTML = `
                        <img src="/img/product_img/${product.productImg}" alt="${product.productName}" />
                        <p id="product-name">${product.productName}</p>
                        <p id="product-price">${formattedPrice}원</p>
                    `;

                    // 상품 클릭 시 상세 페이지로 이동
                    productDiv.addEventListener('click', () => {
                        window.location.href = `/product/detail/${product.productNo}`;
                    });

                    productGrid.appendChild(productDiv);
                });
            }
        })
        .catch(error => {
            console.error("상품 목록을 불러오는 중 오류 발생:", error);
        });
}

document.addEventListener("DOMContentLoaded", function () {
    const productNo = window.location.pathname.split('/').pop(); // URL에서 productNo 추출
    loadProductDetail(productNo);
});

function loadProductDetail(productNo) {
    axios.get(`/api/products/detail/${productNo}`)
        .then(response => {
            const product = response.data;
            const productDetail = document.getElementById('product-detail-page');

            const formattedPrice = product.productPrice.toLocaleString('ko-KR'); // 가격 콤마 포맷팅
            const formattedQuantity = product.productQuantity.toLocaleString('ko-KR'); // 재고 콤마 포맷팅

            // 상품 상세 정보를 동적으로 삽입
            productDetail.innerHTML = `
                <div class="product-detail">
                    <img src="/img/product_img/${product.productImg}" alt="${product.productName}" />

                    <div class="product-info">
                        <h1>${product.productName}</h1>

                        <div class="final-price">
                            <p>가격: ${formattedPrice}원</p>
                        </div>

                        <div class="purchase-section">
                            <div class="quantity-control">
                                <button>-</button>
                                <input type="text" value="1">
                                <button>+</button>
                            </div>
                            <div class="purchase-buttons">
                                <button>장바구니</button>
                                <button>바로 결제</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 상품 상세 설명 이미지 추가 -->
                <div class="promotion-banner">
                    <img src="/img/product_detail_img/${product.productDescript}" alt="상품 상세 설명 이미지" />
                </div>
                
            `;
        })
        .catch(error => {
            console.error("상품 상세 정보를 불러오는 중 오류 발생:", error);
        });
}

document.addEventListener("DOMContentLoaded", function () {
    const productNo = window.location.pathname.split('/').pop(); // URL에서 productNo 추출
    loadProductReview(productNo);
});

function loadProductReview(productNo) {
    axios.get(`/api/products/reviews/${productNo}`)
        .then(response => {
            const reviews = response.data;
            console.log(reviews);
            const productReviews = document.getElementById('product-review');
        // 리뷰가 비어있는지 확인
            if (reviews.length === 0) {
                productReviews.innerHTML = `
        <div class="product-reviews">
            <h2>상품 리뷰 ☆☆☆☆☆</h2>
            <p>아직 등록된 리뷰가 없습니다.</p>
        </div>
    `;
            } else {
                // ★ 평균 별점 계산
                const totalRating = reviews.reduce((sum, review) => sum + review.reviewRating, 0);
                const averageRating = (totalRating / reviews.length).toFixed(1); // 소수점 1자리까지

                // 리뷰 리스트 생성
                let reviewListHtml = '';

                reviews.forEach((review, index) => {
                    reviewListHtml += `
            <div class="review">
                <h3>${review.memberName}님 ${'★'.repeat(review.reviewRating)} (${review.reviewRating}/5)</h3>
                <p>${review.reviewComment}</p>
                <small>작성일: ${new Date(review.reviewCreateAt).toLocaleDateString()}</small>
                <div class="review-image">
<!--                    <img src="/img/${review.reviewUrl}.jpg" alt="리뷰 이미지 ${index + 1}" />-->
                </div>
            </div>
        `;
                });

                // 전체 리뷰 섹션 생성
                productReviews.innerHTML = `
        <div class="product-reviews">
            <h3>상품 리뷰 ${'★'.repeat(Math.round(averageRating))} (${averageRating}/5)</h3>
<!--            <div class="review-carousel">-->

<!--            </div>-->

            <!-- 동적으로 삽입된 리뷰 목록 -->
            ${reviewListHtml}
        </div>
    `;
            }
        })
        .catch(error => {
            console.error("상품 상세 정보를 불러오는 중 오류 발생:", error);
        });
}