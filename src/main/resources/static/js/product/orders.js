document.addEventListener("DOMContentLoaded", async function () {
    try {
        // 사용자 정보 가져오기
        const userResponse = await axios.get("/api/carts/info");
        const memberNo = userResponse.data.memberNo;

        if (!memberNo) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
            return;
        }

        // 회원의 전체 주문 목록 가져오기
        const orderResponse = await axios.get(`/api/orders/member/${memberNo}`);
        const orders = orderResponse.data;

        if (!orders || orders.length === 0) {
            document.getElementById("order-list").innerHTML = "<p>최근 주문 내역이 없습니다.</p>";
            return;
        }

        const orderContainer = document.getElementById("order-list");
        orderContainer.innerHTML = ""; // 기존 내용 초기화

        // 각 주문을 리스트에 추가
        orders.forEach(order => {
            // 주문 정보 추가
            const orderDiv = document.createElement("div");
            orderDiv.classList.add("order-box");
            orderDiv.innerHTML = `
                <h3>주문 번호: ${order.ordersNo}</h3>
                <p><strong>주문 상태:</strong> ${order.ordersStatus}</p>
                <p><strong>총 결제 금액:</strong> ${order.ordersTotalPrice.toLocaleString()} 원</p>
                <p><strong>주문 일시:</strong> ${new Date(order.ordersDate).toLocaleString()}</p>
                <button class="toggle-details" data-order-no="${order.ordersNo}">상세 보기</button>
                <div class="order-details" id="order-details-${order.ordersNo}" style="display: none;">
                    <ul id="order-items-${order.ordersNo}"></ul>
                </div>
            `;
            orderContainer.appendChild(orderDiv);
        });

        // "상세 보기" 버튼 이벤트 추가
        document.querySelectorAll(".toggle-details").forEach(button => {
            button.addEventListener("click", async function () {
                const orderNo = this.getAttribute("data-order-no");
                const detailsDiv = document.getElementById(`order-details-${orderNo}`);

                // 상세 내역이 이미 보이고 있으면 숨김
                if (detailsDiv.style.display === "block") {
                    detailsDiv.style.display = "none";
                    this.textContent = "상세 보기";
                    return;
                }

                // 상세 내역이 숨겨져 있으면 가져와서 보여줌
                try {
                    const orderDetailResponse = await axios.get(`/api/orders/${orderNo}/details`);
                    const orderDetails = orderDetailResponse.data;

                    console.log(orderDetails)

                    const orderItemsList = document.getElementById(`order-items-${orderNo}`);
                    orderItemsList.innerHTML = ""; // 기존 리스트 초기화

                    orderDetails.forEach(item => {
                        const li = document.createElement("li");
                        li.innerHTML = `상품 이름: ${item.productName} | 수량: ${item.orderDetailQuantity} | 가격: <strong>${item.orderDetailTotalPrice.toLocaleString()} 원</strong>`;
                        orderItemsList.appendChild(li);
                    });

                    detailsDiv.style.display = "block"; // 상세 내역 표시
                    this.textContent = "상세 보기 닫기"; // 버튼 텍스트 변경
                } catch (error) {
                    console.error(`주문 ${orderNo}의 상세 정보를 불러오는 중 오류 발생:`, error);
                }
            });
        });

    } catch (error) {
        console.error("주문 정보 불러오기 실패:", error);
        document.getElementById("order-list").innerHTML = "<p>주문 정보를 불러오는 중 오류가 발생했습니다.</p>";
    }
});
