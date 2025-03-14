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

        if (selectedItems.length === 0) {
            orderItemsContainer.innerHTML = "<p>선택된 상품이 없습니다.</p>";
            return;
        }

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

        document.getElementById("total-price").textContent = `${totalPrice.toLocaleString()} 원`;

    } catch (error) {
        console.error("사용자 정보를 불러오는 중 오류 발생:", error);
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
    }
});

// 결제 완료 버튼 클릭 시 주문 저장
document.addEventListener("DOMContentLoaded", async function () {
    const paymentButton = document.querySelector(".payment-button");

    paymentButton.addEventListener("click", async function () {
        const name = document.getElementById("name").value.trim();
        const phone = document.getElementById("phone").value.trim();
        const email = document.getElementById("email").value.trim();
        const address = document.getElementById("address").value.trim();
        const totalPrice = parseInt(document.getElementById("total-price").textContent.replace(" 원", "").replaceAll(",", ""), 10);
        const selectedItems = JSON.parse(sessionStorage.getItem("selectedItems")) || [];

        if (!name || !phone || !email || totalPrice <= 0) {
            alert("모든 정보를 입력하고, 결제 금액을 확인해주세요.");
            return;
        }

        if (selectedItems.length === 0) {
            alert("결제할 상품이 없습니다.");
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
            // 회원 정보 가져오기 (수정된 부분)
            const userResponse = await axios.get("/api/carts/info");
            const memberNo = userResponse.data.memberNo; // memberNo를 별도로 가져옴

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

            // 결제 성공 후 주문 정보 서버에 저장
            const orderResponse = await fetch("/api/orders/create", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    memberNo: memberNo,
                    totalPrice: totalPrice,
                    orderItems: selectedItems.map(item => ({
                        productNo: item.productNo,
                        quantity: item.quantity,
                        totalPrice: item.totalPrice
                    }))
                }),
            });

            if (orderResponse.ok) {
                alert("결제가 성공적으로 완료되었습니다!");
                window.location.href = "/success";
            } else {
                alert("주문 저장 중 오류가 발생했습니다.");
            }

        } catch (error) {
            console.error("결제 중 오류 발생:", error);
            alert("결제 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    });
});