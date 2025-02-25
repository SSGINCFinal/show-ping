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
                        <div class="rating">
                            ⭐⭐⭐⭐☆
                            <span>5개의 상품평</span>
                        </div>
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
                
                        <!-- 상품 리뷰 -->
        <div class="product-reviews">
            <h2>상품 리뷰 ⭐⭐⭐⭐☆</h2>
            <div class="review-carousel">
                <img src="/img/review1.jpg" alt="리뷰 이미지 1">
                <img src="/img/review2.jpg" alt="리뷰 이미지 2">
                <img src="/img/review3.jpg" alt="리뷰 이미지 3">
                <img src="/img/review4.jpg" alt="리뷰 이미지 4">
            </div>

            <!-- 리뷰 상세 -->
            <div class="review">
                <h3>이수근 ⭐⭐⭐⭐☆</h3>
                <p>사용해본 결과 가볍고 성능이 매우 뛰어납니다. 디자인도 정말 예쁘고 만족합니다!</p>
            </div>
        </div>
            `;
        })
        .catch(error => {
            console.error("상품 상세 정보를 불러오는 중 오류 발생:", error);
        });
}
