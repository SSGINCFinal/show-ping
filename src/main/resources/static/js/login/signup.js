document.addEventListener("DOMContentLoaded", function () {
    // 초기화
    document.getElementById("password").value = "";
    document.getElementById("confirm-password").value = "";
    document.getElementById("email").value = "";
});

// 비밀번호 토글 기능
document.querySelectorAll('.toggle-password').forEach(button => {
    button.addEventListener('click', function () {
        const input = this.previousElementSibling;
        if (input.type === "password") {
            input.type = "text";
            this.textContent = "🔒";
        } else {
            input.type = "password";
            this.textContent = "👁";
        }
    });
});

// 이메일 인증 코드 확인 버튼 이벤트
document.querySelector('.verify-code-btn').addEventListener('click', function () {
    const email = document.getElementById('email').value.trim();
    const emailCode = document.getElementById('email-code').value.trim();  // emailCode가 제대로 가져와지는지 확인

    console.log("✅ 인증 코드 확인 버튼 클릭됨!");
    console.log("보낼 데이터:", { email: email, emailCode: emailCode });

    axios.post('/signup/verify-code', {
        email: email,
        emailCode: emailCode
    })
        .then(response => {
            console.log("서버 응답:", response.data);
            if (response.data === true) {
                alert("✅ 이메일 인증 완료!");
            } else {
                alert("❌ 이메일 인증 코드가 일치하지 않습니다.");
            }
        })
        .catch(error => {
            console.error("❌ 오류 발생:", error);
            alert("❌ 이메일 인증 중 문제가 발생했습니다.");
        });
});

// 회원가입 버튼 이벤트
document.querySelector('.signup-btn').addEventListener('click', function (event) {
    event.preventDefault();  // 폼 제출을 막고 자바스크립트로 처리

    // 입력값 가져오기
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const emailCode = document.getElementById('email-code').value.trim();
    const memberId = document.getElementById('memberId').value.trim();
    const password = document.getElementById('password').value.trim();
    const confirmPassword = document.getElementById('confirm-password').value.trim();
    const address = document.getElementById('address').value.trim();
    const phone = document.getElementById('phone').value.trim();

    // 기본 유효성 검사
    if (!memberId) {
        alert("아이디를 입력해주세요.");
        return;
    }
    if (!validateMemberId(memberId)) {
        alert("아이디는 영문 또는 숫자로 6~20자여야 합니다.");
        return;
    }
    if (!password) {
        alert("비밀번호를 입력해주세요.");
        return;
    }
    if (!confirmPassword) {
        alert("비밀번호를 확인해주세요.");
        return;
    }
    if (!validatePassword(password) || !validatePassword(confirmPassword)) {
        alert("비밀번호는 문자, 숫자, 특수문자를 포함해 8~20자여야 합니다.");
        return;
    }
    if (!checkPasswordMatch()) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }
    if (!name) {
        alert("이름을 입력해주세요.");
        return;
    }
    if (!phone) {
        alert("핸드폰 번호를 입력해주세요.");
        return;
    }
    if (!validatePhone(phone)) {
        alert("올바른 핸드폰 번호를 입력해주세요.(010-1234-5678)");
        return;
    }
    if (!email) {
        alert("이메일을 입력해주세요.");
        return;
    }
    if (!validateEmail(email)) {
        alert("올바른 이메일 주소를 입력하세요.");
        return;
    }
    if (!emailCode) {
        alert("이메일 인증 코드를 입력해주세요.");
        return;
    }
    if (!address) {
        alert("주소를 입력해주세요.");
        return;
    }

    // 아이디 중복 체크
    fetch(`/check-duplicate?id=${memberId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('❌ 아이디가 중복되었습니다.');
            }
            return response.text();
        })
        .then(() => {
            console.log("✅ 아이디 중복 확인 완료 (사용 가능)");

            // 이메일 중복 체크
            return fetch(`/check-email-duplicate?email=${email}`);
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('❌ 중복된 이메일입니다.');
            }
            return response.text();
        })
        .then(() => {
            console.log("✅ 이메일 중복 확인 완료 (사용 가능)");

            // 이메일 인증 코드 확인 (axios 사용)
            return axios.post('/signup/verify-code', { email: email, emailCode: emailCode });
        })
        .then(response => {
            if (!response.data) {
                alert("❌ 이메일 인증 코드가 일치하지 않습니다.");
                // 필요한 경우 해당 입력 필드만 초기화할 수도 있음
                window.location.reload();  // 현재 페이지 새로고침 (404 방지)
                return;
            }
            console.log("✅ 이메일 인증 완료");

            // 모든 유효성 검사 통과 후 회원가입 진행 (axios 사용)
            return axios.post('/register', {
                memberId: memberId,
                memberPassword: password,
                memberName: name,
                memberPhone: phone,
                memberEmail: email,
                memberAddress: address,
            });
        })
        .then(response => {
            if (response) {
                alert("✅ 회원가입이 완료되었습니다!");
                window.location.href = "/login";
            }
        })
        .catch(error => {
            console.error("오류 발생:", error);
            alert(error.message);  // 중복된 아이디/이메일 또는 기타 오류 메시지 표시
            window.location.reload();  // 현재 페이지 새로고침 (404 방지)
        });
});

// 이메일 인증 코드 전송 버튼 이벤트
document.querySelector('.verify-btn').addEventListener('click', function () {
    const email = document.getElementById('email').value.trim();
    // 불필요한 emailCode 변수 제거

    if (!email) {
        alert("이메일을 입력해주세요.");
        return;
    }
    if (!validateEmail(email)) {
        alert("올바른 이메일 주소를 입력하세요.");
        return;
    }

    fetch('/signup/send-code', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email })
    })
        .then(response => response.text())
        .then(data => {
            alert("인증 코드가 이메일로 전송되었습니다.");
            console.log("서버 응답:", data);
            document.getElementById('email-verify-section').classList.remove('hidden');
        })
        .catch(error => {
            console.error("오류 발생:", error);
            alert("이메일 전송 실패");
        });
});

// 유효성 검사 함수들
function validateMemberId(memberId) {
    const memberIdRegex = /^[A-Za-z0-9]{6,20}$/;
    return memberIdRegex.test(memberId);
}

function validatePassword(password) {
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>]).{8,20}$/;
    return passwordRegex.test(password);
}

function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function validatePhone(phone) {
    const phoneRegex = /^01[0-9]-\d{3,4}-\d{4}$/;
    return phoneRegex.test(phone);
}

function checkPasswordMatch() {
    const password = document.getElementById('password').value.trim();
    const confirmPassword = document.getElementById('confirm-password').value.trim();
    const messageDiv = document.getElementById('confirm-password-message');

    if (password === '' || confirmPassword === '') {
        messageDiv.style.display = 'none';
        return false;
    }

    if (password !== confirmPassword) {
        messageDiv.textContent = "비밀번호가 같지 않습니다.";
        messageDiv.style.color = 'red';
        messageDiv.style.display = 'block';
        return false;
    } else {
        messageDiv.textContent = "비밀번호가 같습니다.";
        messageDiv.style.color = 'green';
        messageDiv.style.display = 'block';
        return true;
    }
}

function checkDuplicate() {
    const memberId = document.getElementById("memberId").value.trim();
    const memberIdRegex = /^[A-Za-z0-9]{6,20}$/;
    if (!memberIdRegex.test(memberId)) {
        alert("❌ 아이디는 영문 또는 숫자로 6~20자여야 합니다.");
        return;
    }

    fetch(`/check-duplicate?id=${memberId}`)
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('❌ 중복된 아이디입니다.');
            }
        })
        .then(message => {
            alert("✅ 사용 가능한 아이디입니다.");
        })
        .catch(error => {
            alert(error.message);
        });
}

// 아이디 입력 시 유효성 검사
document.getElementById('memberId').addEventListener('input', function () {
    const memberId = this.value.trim();
    const messageDiv = document.getElementById('memberId-message');

    if (memberId === '') {
        messageDiv.style.display = 'none';
        return;
    }

    if (!validateMemberId(memberId)) {
        messageDiv.textContent = "아이디는 영문 또는 숫자로 6~20자여야 합니다.";
        messageDiv.style.color = 'red';
        messageDiv.style.display = 'block';
    } else {
        messageDiv.textContent = "사용 가능한 아이디입니다.";
        messageDiv.style.color = 'green';
        messageDiv.style.display = 'block';
    }
});

// 비밀번호 입력 시 유효성 검사
document.getElementById('password').addEventListener('input', function () {
    const password = this.value.trim();
    const messageDiv = document.getElementById('password-message');

    if (password === '') {
        messageDiv.style.display = 'none';
        return;
    }

    if (!validatePassword(password)) {
        messageDiv.textContent = "비밀번호는 문자, 숫자, 특수문자를 포함해 8~20자여야 합니다.";
        messageDiv.style.color = 'red';
        messageDiv.style.display = 'block';
    } else {
        messageDiv.textContent = "사용 가능한 비밀번호입니다.";
        messageDiv.style.color = 'green';
        messageDiv.style.display = 'block';
    }
});

document.getElementById('password').addEventListener('input', checkPasswordMatch);
document.getElementById('confirm-password').addEventListener('input', checkPasswordMatch);
