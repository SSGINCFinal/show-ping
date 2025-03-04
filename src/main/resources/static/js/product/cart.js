let memberNo = null; // 전역 변수로 선언

document.addEventListener("DOMContentLoaded", function () {
    axios.get('/api/carts/info')
        .then(response => {
            memberNo = response.data.memberNo;  // 전역 변수에 저장
            loadCartItems(memberNo);
        })
        .catch(error => {
            console.error("로그인한 사용자 정보를 불러오는 중 오류 발생:", error);
            alert("로그인이 필요합니다.");
        });
});

// 장바구니 데이터 불러오기 및 테이블 생성
function loadCartItems(memberNo) {
    axios.get(`/api/carts/${memberNo}`)
        .then(response => {
            const cartItems = response.data;
            const tableBody = document.querySelector(".cart-items tbody");

            tableBody.innerHTML = ""; // 기존 데이터 초기화

            cartItems.forEach(item => {
                const formattedPrice = item.productPrice.toLocaleString('ko-KR') + "원"; // 가격 포맷
                const row = `
                    <tr>
                        <td><input type="checkbox" class="product-checkbox"></td>
                        <td class="product-order">
                            <img class="product-img" src="/img/product_img/${item.productImg}" alt="${item.productName}">
                            ${item.productName}
                        </td>
                        <td>
                            <input type="number" class="quantity-input"
                                   data-product-no="${item.productNo}" 
                                   data-unit-price="${item.productPrice}" 
                                   value="${item.cartProductQuantity}" 
                                   min="1" style="width: 40px;">
                        </td>
                        <td class="product-price" data-price="${item.productPrice * item.cartProductQuantity}">
                            ${(item.productPrice * item.cartProductQuantity).toLocaleString('ko-KR')}원
                        </td>
                        <td class="remove-btn" data-product-no="${item.productNo}">🗑</td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            });

            setupEventListeners(); // 체크박스 및 수량 변경 이벤트 설정
        })
        .catch(error => {
            console.error("장바구니 데이터를 불러오는 중 오류 발생:", error);
        });
}

// 체크박스 및 수량 변경 이벤트 설정
function setupEventListeners() {
    const checkboxes = document.querySelectorAll(".product-checkbox");
    const selectAllCheckbox = document.querySelector(".product-checkbox-all");
    const totalPriceElement = document.querySelector(".cart-summary strong");
    const buyButton = document.querySelector(".checkout-btn");

    let updateTimeout = null; // 서버 업데이트 딜레이 타이머

    // 가격 포맷 변환 함수
    function formatPrice(price) {
        return price.toLocaleString('ko-KR') + "원";
    }

    // 총 상품 금액 업데이트
    function updateTotalPrice() {
        let totalPrice = 0;
        checkboxes.forEach((checkbox) => {
            if (checkbox.checked) {
                const row = checkbox.closest("tr");
                const priceText = row.querySelector(".product-price").getAttribute("data-price");
                totalPrice += parseInt(priceText);
            }
        });
        totalPriceElement.textContent = formatPrice(totalPrice);
    }

    // 전체 선택 체크박스 클릭 시 모든 체크박스 선택/해제
    selectAllCheckbox.addEventListener("change", function () {
        checkboxes.forEach((checkbox) => {
            checkbox.checked = selectAllCheckbox.checked;
        });
        updateTotalPrice();
    });

    // 개별 체크박스 변경 시 총 금액 업데이트
    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener("change", function () {
            updateTotalPrice();
            selectAllCheckbox.checked = [...checkboxes].every(cb => cb.checked);
        });
    });

    // 수량 변경 시 서버에 1초 딜레이 후 업데이트 요청 & 가격 업데이트
    document.querySelectorAll(".quantity-input").forEach(input => {
        input.addEventListener("input", function () {
            if (this.value < 1) this.value = 1; // 최소값 유지

            const row = this.closest("tr");
            const productNo = this.getAttribute("data-product-no");
            const unitPrice = parseInt(this.getAttribute("data-unit-price"));
            const quantity = parseInt(this.value);
            const totalItemPrice = unitPrice * quantity;

            // 개별 상품 가격 업데이트
            row.querySelector(".product-price").setAttribute("data-price", totalItemPrice);
            row.querySelector(".product-price").textContent = formatPrice(totalItemPrice);

            updateTotalPrice(); // 총 상품 금액 업데이트

            // 기존 요청이 있으면 취소하고 새로운 1초 딜레이 시작
            clearTimeout(updateTimeout);
            updateTimeout = setTimeout(() => {
                axios.put(`/api/carts/update?memberNo=${memberNo}`, {
                    productNo: parseInt(productNo),
                    quantity: this.value
                })
                    .then(response => {
                        console.log("장바구니 수량이 서버에서 업데이트됨:", response.data);
                    })
                    .catch(error => {
                        console.error("장바구니 수량 업데이트 실패:", error.response.data);
                    });
            }, 1000); // 1초 딜레이 후 요청 실행
        });
    });

    // 상품 삭제 기능
    document.querySelectorAll(".remove-btn").forEach(button => {
        button.addEventListener("click", function () {
            const productNo = this.getAttribute("data-product-no");
            axios.delete(`/api/carts/remove?memberNo=${memberNo}&productNo=${productNo}`)
                .then(response => {
                    // 상품 삭제 후 새로고침
                    location.reload()
                })
                .catch(error => {
                    alert("상품 삭제 실패: " + error.response.data);
                });
        });
    });
}