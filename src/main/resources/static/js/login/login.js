window.onload = function () {
    const messageElement = document.getElementById("login-message");
    if (messageElement) {
        const message = messageElement.dataset.message || ""; // ✅ undefined 방지
        if (message.trim() !== '') {
            alert(message);
        }
    }
};

async function login(event) {  // ✅ event 파라미터 추가
    event.preventDefault();

    const memberId = document.getElementById("memberId").value;
    const password = document.getElementById("memberPassword").value;

    try {
        const response = await axios.post("/api/admin/login", {
            adminId: memberId,
            password: password
        }, {
            headers: { "Content-Type": "application/json" }
        });

        console.log("로그인 응답:", response.data);

        // ✅ 2FA가 필요한 경우
        if (response.data.status === "2FA_REQUIRED") {
            console.log("✅ 2FA 인증이 필요합니다! TOTP 입력창을 표시합니다.");
            sessionStorage.setItem("accessToken", response.data.accessToken);
            sessionStorage.setItem("memberId", memberId); // ✅ 사용자 ID 저장 (TOTP 검증에 필요)
            document.getElementById("login-form").style.display = "none";
            document.getElementById("totp-form").style.display = "block";
        } else if (response.data.status === "LOGIN_SUCCESS") {
            console.log("✅ 일반 사용자 로그인 성공!");

            // ✅ Access Token 저장
            if (response.data.accessToken) {
                sessionStorage.setItem("accessToken", response.data.accessToken);
                console.log("✅ Access Token 저장 완료:", sessionStorage.getItem("accessToken"));
            }

            setTimeout(() => {
                window.location.href = "/";
            }, 500);
        } else{
            alert("로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
        }
    } catch (error) {
        console.error("로그인 요청 실패:", error.response ? error.response.data : error);
        alert("로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
    }
}

async function verifyTOTP(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const memberId = sessionStorage.getItem("memberId"); // ✅ 저장된 사용자 ID 가져오기
    const totpCode = document.getElementById("totpCode").value;
    const accessToken = sessionStorage.getItem("accessToken"); // ✅ 기존 Access Token 유지
    const refreshToken = sessionStorage.getItem("refreshToken"); // ✅ 기존 Refresh Token 유지

    if (!memberId) {
        alert("로그인 정보가 없습니다. 다시 로그인해주세요.");
        window.location.href = "/login";
        return;
    }

    try {
        const response = await axios.post("/api/admin/verify-totp", {
            adminId: memberId,
            totpCode: totpCode,
            accessToken: accessToken, // ✅ 기존 Access Token 전달
            refreshToken: refreshToken
        });

        console.log("🚀 TOTP 응답 데이터:", response.data);

        if (response.data.status === "LOGIN_SUCCESS") {
            console.log("✅ 2FA 인증 성공! 최종 로그인 완료");

            // ✅ 기존 Access Token을 다시 저장 (덮어쓰기 방지)
            if (response.data.accessToken) {
                sessionStorage.setItem("accessToken", response.data.accessToken);
                console.log("✅ 최종 Access Token 저장 완료:", sessionStorage.getItem("accessToken"));
            }

            setTimeout(() => {
                window.location.href = "/";
            }, 500);
        } else {
            alert("OTP 인증 실패! 다시 시도하세요.");
        }
    } catch (error) {
        console.error("🚨 TOTP 인증 실패:", error.response ? error.response.data : error);
        alert("OTP 인증 실패! 다시 시도하세요.");
    }
}
