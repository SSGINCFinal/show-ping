////////////////////////////////////////////////
// axiosInstance.js
// CSRF 토큰 저장 변수
let csrfToken = "";

// HTTPOnly 쿠키는 자바스크립트에서 접근할 수 없지만,
// 브라우저는 자동으로 HTTPOnly 쿠키를 모든 요청에 포함시킴.
// 이 쿠키는 withCredentials 옵션과는 관계없이, <a> 태그나 서버로의 기본적인 HTTP 요청에서 자동으로 전송됨.

const api = axios.create({
    baseURL: "http://localhost:8888/auth",
    withCredentials: true, // HTTPOnly 쿠키 포함 (자동으로 전송됨)
    // GET /some-endpoint HTTP/1.1
    // Host: example.com
    // Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhdXRob3IiLCJleHBpcmVkX3VzZXIiOiJ1c2VyX2F1dGgiLCJpYXQiOjE2Mjg4NDEwMzF9.GZsM...
    headers: {
        "Content-Type": "application/json"
    }
});

////////////////////////////////////////////////
// login.js
async function login() {
    const username = document.getElementById("memberId").value;
    const password = document.getElementById("memberPassword").value;

    try {
        const response = await api.post("login3", {memberId, memberPassword});
        console.log("📢 서버 응답 message:", response.data.message);
        console.log("📢 서버 응답 token:", response.data.token);

        // ✅ API 응답에 'error' 키가 포함된 경우 → 로그인 실패 처리
        if (response.data.error) {
            alert(response.data.error);
            return;
        }

        // ✅ API 응답 메시지가 "로그인 성공!"이면 로그인 성공 처리
        if (response.data.message === "성공") {
            alert("로그인 성공!");
            // CSRF 토큰 가져오는 부분이 필요하면 활성화
            // await fetchCsrfToken();
            //window.location.href = "/auth/user-info3"; // 로그인 후 사용자 정보 페이지로 이동
        } else {
            alert("로그인 실패! 올바른 정보를 입력했는지 확인하세요.");
        }
    } catch (error) {
        alert("로그인 요청 중 오류 발생!");
        console.error("🚨 로그인 오류:", error);

        // 서버에서 403 응답을 받은 경우 (예: CSRF 문제)
        if (error.response && error.response.status === 403) {
            alert("403 오류: CSRF 문제가 발생했을 가능성이 있습니다. 새로고침 후 다시 시도하세요.");
        }
    }
}

////////////////////////////////////////////////
// logout.js
async function logout() {
    try {
        await api.get("logout3");
        alert("로그아웃 성공!");
        window.location.href = "/";
    } catch (error) {
        alert("로그아웃 실패!");
        console.error("로그아웃 오류:", error);
    }
}

////////////////////////////////////////////////
// userInfo.js
async function getUserInfo() {
    try {
        const response = await api.get("user-info3");

        if (response.status === 200) {
            console.log(`사용자 정보: ${response.data.username}` + "        " + response.data.role);
            document.getElementById("user-info").innerText = `사용자 정보: ${response.data.username}` + " " + `${response.data.role}`;
        }
    } catch (error) {
        alert("인증 실패! 다시 로그인하세요.");
        console.error("사용자 정보 가져오기 오류:", error);
    }
}