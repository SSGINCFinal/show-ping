window.onload = function () {
    const messageElement = document.getElementById("login-message");

    if (messageElement) {
        const message = messageElement.dataset.message || ""; // ✅ undefined 방지
        if (message.trim() !== '') {
            alert(message);
        }
    }
};

async function login(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const memberId = document.getElementById("memberId").value;
    const password = document.getElementById("memberPassword").value;

    try {
        const response = await axios.post("/api/auth/login", {
            memberId: memberId,
            memberPassword: password
        });

        // ✅ 백엔드에서 토큰이 정상적으로 반환되었는지 확인
        console.log("로그인 응답:", response.data);

        if (!response.data.accessToken) {
            alert("Access Token을 받지 못했습니다.");
            return;
        }

        // ✅ Access Token을 sessionStorage에 저장
        sessionStorage.setItem("accessToken", response.data.accessToken);
        console.log("저장된 Access Token:", sessionStorage.getItem("accessToken"));

        alert("로그인 성공!");
        window.location.href = "/"; // ✅ 로그인 후 메인 페이지로 이동
    } catch (error) {
        console.error("로그인 요청 실패:", error.response ? error.response.data : error);
        alert("로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
    }
}

// ✅ API 요청 (Access Token 포함)
async function fetchProtectedData() {
    const accessToken = sessionStorage.getItem("accessToken");

    if (!accessToken) {
        alert("Access Token이 없습니다. 로그인하세요.");
        return;
    }

    const response = await fetch("/api/protected-resource", {
        method: "GET",
        headers: { "Authorization": `Bearer ${accessToken}` },
    });

    if (!response.ok) {
        alert("API 요청 실패 (토큰 만료 가능성 있음)");
        return;
    }

    const result = await response.json();
    console.log("API 응답:", result);
}

// ✅ 로그아웃 (Access Token 삭제 & 서버에 로그아웃 요청)
async function logout() {
    const memberId = document.getElementById("memberId").value;

    if (!memberId) {
        alert("로그인 후 로그아웃 가능합니다.");
        return;
    }

    await fetch("/api/auth/logout", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ memberId }),
    });

    sessionStorage.removeItem("accessToken"); // ✅ Access Token 삭제
    alert("로그아웃 완료!");
    window.location.href = "/login"; // ✅ 로그인 페이지로 이동
}