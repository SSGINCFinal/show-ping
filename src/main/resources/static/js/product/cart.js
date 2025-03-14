let memberNo = null;
let member = null

document.addEventListener("DOMContentLoaded", function () {
    axios.get("/api/carts/info")
        .then(response => {
            member = response.data;
            memberNo = member.memberNo;
            loadCartItems(memberNo);
        })
        .catch(error => {
            console.error("사용자 정보를 불러오는 중 오류 발생:", error);
            alert("로그인이 필요합니다.");
            window.location.href = "/login"; // 로그인 페이지로 리디렉션
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
                        <td>
                            <input type="checkbox" class="product-checkbox"
                                   data-name="${item.productName}" 
                                   data-price="${item.productPrice}" 
                                   data-quantity="${item.cartProductQuantity}">
                        </td>
                        <td class="product-order">
                            <img class="product-img" src="${item.productImg}" alt="${item.productName}">
                            ${item.productName}
                        </td>
                        <td>
                            <input type="number" class="quantity-input" 
                                   data-product-no="${item.productNo}" 
                                   data-unit-price="${item.productPrice}" 
                                   value="${item.cartProductQuantity}" 
                                   min="1" style="width: 40px;">
                        </td>
                        <td class="product-price" style="width: 200px;" data-price="${item.productPrice * item.cartProductQuantity}">
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

    let updateTimeout = null; // 서버 업데이트 딜레이 타이머

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
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", function () {
            checkboxes.forEach((checkbox) => {
                checkbox.checked = selectAllCheckbox.checked;
            });
            updateTotalPrice();
        });
    }

    // 개별 체크박스 변경 시 총 금액 업데이트
    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener("change", function () {
            updateTotalPrice();
            if (selectAllCheckbox) {
                selectAllCheckbox.checked = [...checkboxes].every(cb => cb.checked);
            }
        });
    });

    // 수량 변경 시 서버에 1초 딜레이 후 업데이트 요청 & 가격 업데이트
    document.querySelectorAll(".quantity-input").forEach(input => {
        input.addEventListener("input", function () {
            if (this.value < 1) this.value = 1; // 최소값 유지
            if (this.value > 50) this.value = 50; //최댓값 유지

            const row = this.closest("tr");
            const productNo = this.getAttribute("data-product-no");
            const unitPrice = parseInt(this.getAttribute("data-unit-price"));
            const quantity = parseInt(this.value);
            const totalItemPrice = unitPrice * quantity;

            // 개별 상품 가격 업데이트
            row.querySelector(".product-price").setAttribute("data-price", totalItemPrice);
            row.querySelector(".product-price").textContent = formatPrice(totalItemPrice);

            updateTotalPrice(); // 총 상품 금액 업데이트

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
                    location.reload()
                })
                .catch(error => {
                    alert("상품 삭제 실패: " + error.response.data);
                });
        });
    });
}

document.getElementById("checkout-btn").addEventListener("click", function (event) {
    event.preventDefault();

    const selectedItems = [];
    const checkboxes = document.querySelectorAll(".product-checkbox:checked");

    checkboxes.forEach(checkbox => {
        const row = checkbox.closest("tr");
        const productNo = row.querySelector(".quantity-input").getAttribute("data-product-no"); // ✅ productNo 가져오기
        const productName = row.querySelector(".product-order").textContent.trim();
        const productPrice = parseInt(row.querySelector(".product-price").getAttribute("data-price"));
        const quantity = parseInt(row.querySelector(".quantity-input").value);

        selectedItems.push({
            productNo: parseInt(productNo),
            name: productName,
            quantity: quantity,
            totalPrice: productPrice,
        });
    });

    if (selectedItems.length === 0) {
        alert("선택된 상품이 없습니다.");
        return;
    }

    console.log("선택된 상품:", selectedItems); // ✅ 콘솔에서 productNo 포함 여부 확인
    sessionStorage.setItem("selectedItems", JSON.stringify(selectedItems));

    window.location.href = "/payment";
});
