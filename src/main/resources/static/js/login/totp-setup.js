window.onload = async function() {
    const adminId = "ShowPing_Admin"; // ✅ 관리자 ID (로그인 후 가져올 수도 있음)

    try {
        const response = await axios.get(`/api/admin/totp-setup/${adminId}`);
        if (response.data.status === "SUCCESS") {
            document.getElementById("secretKey").textContent = response.data.secretKey;
        } else {
            alert("설정 키를 가져오는 데 실패했습니다: " + response.data.message);
        }
    } catch (error) {
        console.error("TOTP 설정 요청 실패:", error);
        alert("TOTP 설정 중 오류가 발생했습니다.");
    }
};