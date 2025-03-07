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

document.addEventListener("DOMContentLoaded", function () {
    const paymentButton = document.querySelector(".payment-button");

    paymentButton.addEventListener("click", async function () {
        const name = document.getElementById("name").value.trim();
        const phone = document.getElementById("phone").value.trim();
        const email = document.getElementById("email").value.trim();
        const totalPrice = parseInt(document.getElementById("total-price").textContent.replace(" 원", "").replaceAll(",", ""), 10);

        if (!name || !phone || !email || totalPrice <= 0) {
            alert("모든 정보를 입력하고, 결제 금액을 확인해주세요.");
            return;
        }

        if (!window.PortOne) {
            alert("결제 시스템을 불러오는 중입니다. 잠시 후 다시 시도해주세요.");
            return;
        }

        function generatePaymentId() {
            return [...crypto.getRandomValues(new Uint32Array(2))]
                .map((word) => word.toString(16).padStart(8, "0"))
                .join("");
        }

        const paymentId = generatePaymentId();

        try {
            const payment = await PortOne.requestPayment({
                storeId: "store-e4038486-8d83-41a5-acf1-844a009e0d94",
                channelKey: "channel-key-ebe7daa6-4fe4-41bd-b17d-3495264399b5",
                paymentId,
                orderName: "상품 결제",
                totalAmount: totalPrice,
                currency: "KRW",
                payMethod: "CARD",
                customer: {
                    name,
                    email,
                    phone,
                },
                customData: {
                    userInfo: { name, phone, email },
                },
            });

            if (payment.code !== undefined) {
                console.error("결제 실패:", payment);
                alert(`결제 실패: ${payment.message}`);
                return;
            }

            const response = await fetch("/api/payments/complete", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    paymentId: payment.paymentId,
                }),
            });

            if (response.ok) {
                const result = await response.json();
                if (result.status === "PAID") {
                    alert("결제가 성공적으로 완료되었습니다.");
                    window.location.href = "/success"; // 성공 페이지로 이동
                } else {
                    alert("결제 처리 중 오류가 발생했습니다.");
                }
            } else {
                alert("서버 응답 오류: 결제 처리에 실패했습니다.");
            }
        } catch (error) {
            console.error("결제 중 오류 발생:", error);
            alert("결제 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    });
});
