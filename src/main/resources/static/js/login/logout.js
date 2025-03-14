document.addEventListener("DOMContentLoaded", function () {
    const accessToken = sessionStorage.getItem("accessToken"); // ✅ Access Token 가져오기
    const userMenu = document.getElementById("user-menu");
    const loginButton = document.getElementById("login-button");
    const logoutButton = document.querySelector(".logout-button");

    if (accessToken) {
        // ✅ 로그인 상태: 사용자 정보 요청하여 표시
        fetch("/api/auth/user-info", {
            method: "GET",
            headers: { Authorization: `Bearer ${accessToken}` }
        })
            .then(response => response.json())
            .then(data => {
                if (data.username) {
                    document.getElementById("user-greeting").innerText = `환영합니다, ${data.username}님`;
                    if (userMenu) userMenu.style.display = "block";
                    if (loginButton) loginButton.style.display = "none";
                }
            })
            .catch(error => {
                console.log("❌ 로그인 정보 불러오기 실패:", error);
                sessionStorage.removeItem("accessToken"); // 로그인 실패 시 토큰 삭제
                if (userMenu) userMenu.style.display = "none";
                if (loginButton) loginButton.style.display = "block";
            });
    } else {
        // ✅ 비로그인 상태
        if (userMenu) userMenu.style.display = "none";
        if (loginButton) loginButton.style.display = "block";
    }

    if (logoutButton) {
        console.log("로그아웃 버튼이 제대로 선택됨")
        logoutButton.addEventListener("click", function (event) {
            event.preventDefault(); // 기본 링크 동작 방지

            console.log("🚀 로그아웃 버튼 클릭됨 API 요청 실행");

            fetch("/api/auth/logout", {  // ✅ 올바른 URL
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${sessionStorage.getItem("accessToken")}`,
                    "Content-Type": "application/json"
                },
                credentials: "include" // 쿠키를 포함하여 요청
            })
                .then(response => {
                    console.log("✅ 로그아웃 요청 완료, 응답 상태 코드:", response.status);
                    if (response.ok) {
                        sessionStorage.removeItem("accessToken"); // ✅ Access Token 삭제
                        sessionStorage.removeItem("refreshToken"); // ✅ Refresh Token 삭제
                        console.log("✅ 세션 토큰 삭제 완료");
                        window.location.href = "/login"; // ✅ 로그인 페이지로 이동
                    } else {
                        console.error("🚨 로그아웃 실패: ", response.statusText);
                    }
                })
                .catch(error => console.error("🚨 로그아웃 오류:", error));
        });
    }else {
        console.log("로그아웃 버튼을 찾을 수 없음")
    }
});