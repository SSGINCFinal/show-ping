document.addEventListener("DOMContentLoaded", async function () {
    try {
        // API에서 사용자 정보 가져오기
        const response = await axios.get("/api/carts/info");
        console.log("API 응답 데이터:", response.data);

        if (response.data) {
            document.getElementById("name").value = response.data.memberName || "";
            document.getElementById("phone").value = response.data.memberPhone || "";
            document.getElementById("email").value = response.data.memberEmail || "";
            document.getElementById("address").value = response.data.memberAddress || "";
        }

        // sessionStorage에서 선택된 상품 정보 가져오기
        const selectedItems = JSON.parse(sessionStorage.getItem("selectedItems")) || [];
        const orderItemsContainer = document.getElementById("order-items");
        let totalPrice = 0;

        // 선택된 상품이 없을 경우 메시지 표시
        if (selectedItems.length === 0) {
            orderItemsContainer.innerHTML = "<p>선택된 상품이 없습니다.</p>";
            return;
        }

        // 선택된 상품 목록을 동적으로 추가
        selectedItems.forEach(item => {
            const itemElement = document.createElement("div");
            itemElement.classList.add("order-item");
            itemElement.innerHTML = `
                        <span>${item.name} x ${item.quantity}</span> 
                        <strong>${item.totalPrice.toLocaleString()} 원</strong>
                    `;
            orderItemsContainer.appendChild(itemElement);
            totalPrice += item.totalPrice;
        });

        // 총 금액 업데이트
        document.getElementById("total-price").textContent = `${totalPrice.toLocaleString()} 원`;

    } catch (error) {
        console.error("사용자 정보를 불러오는 중 오류 발생:", error);
        alert("로그인이 필요합니다.");
        window.location.href = "/login"; // 로그인 페이지로 리디렉션
    }
});