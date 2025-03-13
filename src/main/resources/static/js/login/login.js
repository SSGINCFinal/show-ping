window.onload = function () {
    const messageElement = document.getElementById("login-message");
    if (messageElement) {
        const message = messageElement.dataset.message || ""; // ✅ undefined 방지
        if (message.trim() !== '') {
            alert(message);
        }
    }
};

async function login() {
    event.preventDefault();

    const memberId = document.getElementById("memberId").value;
    const password = document.getElementById("memberPassword").value;

    try {
        const response = await axios.post("/api/admin/login", { // ✅ 여기 변경
            adminId: memberId,
            password: password
        }, {
            headers: { "Content-Type": "application/json"}
        });

        console.log("로그인 응답:", response.data);

        if (response.data.status === "2FA_REQUIRED") {
            console.log("✅ 2FA 인증이 필요합니다! TOTP 입력창을 표시합니다.");
            document.getElementById("login-form").style.display = "none";
            document.getElementById("totp-form").style.display = "block";
        } else {
            alert("로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
        }
    } catch (error) {
        console.error("로그인 요청 실패:", error.response ? error.response.data : error);
        alert("로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
    }
}

async function verifyTOTP() {
    const memberId = document.getElementById("memberId").value;
    const totpCode = document.getElementById("totpCode").value;

    try {
        const response = await axios.post("/api/admin/verify-totp", {
            adminId: memberId,
            totpCode: totpCode
        });

        if (response.data.status === "LOGIN_SUCCESS") {
            alert("로그인 성공!");
            sessionStorage.setItem("accessToken", response.data.accessToken); // ✅ Access Token 저장
            window.location.href = "/"; // ✅ 로그인 후 메인 페이지로 이동
        } else {
            alert("OTP 인증 실패! 다시 시도하세요.");
        }
    } catch (error) {
        console.error("TOTP 인증 실패:", error.response ? error.response.data : error);
        alert("OTP 인증 실패! 다시 시도하세요.");
    }
}
