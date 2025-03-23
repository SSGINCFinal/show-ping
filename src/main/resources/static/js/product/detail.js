// 상품 클릭 시 상품 상세정보 페이지로 이동
document.addEventListener("DOMContentLoaded", function () {
    const productNo = window.location.pathname.split('/').pop(); // URL에서 productNo 추출
    loadProductDetail(productNo);
    loadProductReview(productNo);
});

function loadProductDetail(productNo) {
    axios.get(`/api/products/detail/${productNo}`)
        .then(response => {
            const product = response.data;
            const productSale = product.productSale;  // productSale 값을 가져옵니다.
            const productDetail = document.getElementById('product-detail-page');

            const formattedPrice = product.productPrice.toLocaleString('ko-KR'); // 가격 콤마 포맷팅
            const formattedFinalPrice = product.discountedPrice.toLocaleString('ko-KR');


            // 상품 상세 정보를 동적으로 삽입
            productDetail.innerHTML = `
                <div class="product-detail">
                    <img src="${product.productImg}" alt="${product.productName}" />

                    <div class="product-info">
                        <h1>${product.productName}</h1>
                        
                    <div class="product-price" id="product-sale" style="font-size: 25px">
                        <p style="text-decoration: line-through">${formattedPrice}원</p>
                        <p style="color: red">${product.productSale} %</p>
                    </div>
                    
                    <div class="product-price-final" id="product-price-final">
                        <p>${formattedFinalPrice}원</p>
                    </div>

                        <div class="purchase-section">
                            <div class="quantity-control">
                                <button id="decrease-btn">-</button>
                                <input type="text" id="quantity-input" value="1" readonly>
                                <button id="increase-btn">+</button>
                            </div>
                            <div class="purchase-buttons">
                                <button id="add-to-cart-btn">장바구니</button>
                                <button>바로 결제</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 상품 상세 설명 이미지 추가 -->
                <div class="promotion-banner">
                    <img src="${product.productDescript}" alt="상품 상세 설명 이미지" />
                </div>
            `;

            // productSale이 0일 경우 product-price를 숨기기
            if (productSale === 0) {
                document.getElementById("product-sale").style.display = "none"; // product-price 숨기기
            } else {
                document.getElementById("product-sale").style.display = "block"; // product-price 보이기
            }

            setupEventListeners(productNo); // 수량 조절 및 장바구니 추가 기능 연결

        })
        .catch(error => {
            console.error("상품 상세 정보를 불러오는 중 오류 발생:", error);
        });
}


function loadProductReview(productNo) {
    axios.get(`/api/products/reviews/${productNo}`)
        .then(response => {
            const reviews = response.data;
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

function setupEventListeners(productNo) {
    const quantityInput = document.getElementById("quantity-input");
    const decreaseBtn = document.getElementById("decrease-btn");
    const increaseBtn = document.getElementById("increase-btn");
    const addToCartBtn = document.getElementById("add-to-cart-btn");

    let quantity = 1;  // 기본 수량

    // - 버튼 클릭 시 수량 감소 (최소 1)
    decreaseBtn.addEventListener("click", function () {
        if (quantity > 1) {
            quantity--;
            quantityInput.value = quantity;
        }
    });

    // + 버튼 클릭 시 수량 증가
    increaseBtn.addEventListener("click", function () {
        quantity++;
        quantityInput.value = quantity;
    });

    // 장바구니 버튼 클릭 시 상품 추가 요청
    addToCartBtn.addEventListener("click", async function () {
        try {
            // JWT 토큰 가져오기 (sessionStorage 사용)
            const token = sessionStorage.getItem("accessToken");

            if (!token) {
                // 로그인하지 않은 경우 로그인 페이지로 리디렉션
                if (confirm("로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?")) {
                    window.location.href = "/login";
                }
                return; // 로그인하지 않았다면 함수 종료
            }

            // JWT 토큰을 Authorization 헤더에 포함시켜 API 호출
            const response = await axios.get("/api/carts/info", {
                headers: {
                    Authorization: `Bearer ${token}`  // JWT 토큰을 Authorization 헤더에 포함
                }
            });

            console.log(response.data);

            const memberNo = response.data.memberNo;  // 로그인된 사용자 정보에서 memberNo 추출

            // 로그인 여부 확인
            if (!memberNo) {
                alert("사용자 정보가 없습니다.");
                return;
            }

            // 장바구니에 상품 추가 요청
            axios.post(`/api/carts/add?memberNo=${memberNo}`, {
                productNo: productNo,
                quantity: quantity
            })
                .then(response => {
                    quantityInput.value = 1;  // 수량 초기화
                    if (confirm("장바구니에 상품이 추가되었습니다. 장바구니로 이동하시겠습니까?")) {
                        window.location.href = "/cart";  // 장바구니 페이지로 이동
                    }
                })
                .catch(error => {
                    alert("장바구니 추가 실패: " + (error.response?.data || "알 수 없는 오류"));
                });
        } catch (error) {
            console.error("로그인 이후 장바구니를 사용할 수 있습니다.", error);
            if (confirm("로그인 이후 장바구니를 사용할 수 있습니다. 로그인 하시겠습니까?")) {
                window.location.href = "/login";  // 로그인 페이지로 이동
            }
        }
    });
}