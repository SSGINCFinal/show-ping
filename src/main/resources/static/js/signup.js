document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("password").value = "";
    document.getElementById("confirm-password").value = "";
    document.getElementById("email").value = "";
})

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

document.querySelector('.verify-code-btn').addEventListener('click', function () {
    const email = document.getElementById('email').value;
    const emailCode = document.getElementById('email-code').value;

    fetch(`/signup/verify-code?email=${encodeURIComponent(email)}&code=${encodeURIComponent(emailCode)}`, {
        method: 'POST'
    })
        .then(response => response.json())
        .then(isValid => {
            if (isValid) {
                alert("이메일 인증 완료!");
            } else {
                alert("인증 코드가 일치하지 않습니다.");
            }
        })
        .catch(error => {
            console.error("오류 발생:", error);
            alert("인증 코드 확인 실패");
        });
});

document.querySelector('.signup-btn').addEventListener('click', function (event) {
    event.preventDefault();  // 폼 제출을 막고 자바스크립트로 처리

    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const emailCode = document.getElementById('email-code').value.trim();
    const memberId = document.getElementById('memberId').value.trim();
    const password = document.getElementById('password').value.trim();
    const confirmPassword = document.getElementById('confirm-password').value.trim();
    const address = document.getElementById('address').value.trim();
    const phone = document.getElementById('phone').value.trim();

    if (!memberId) {
        alert("아이디를 입력해주세요.");
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

    // 중복 확인 후에 진행
    axios.post('/register', {
        memberId: memberId,
        memberPassword: password,
        memberName: name,
        memberPhone: phone,
        memberEmail: email,
        memberAddress: address,
    })
        .then(response => {
            alert("회원가입이 완료 되었습니다!");
            window.location.href = "/login";
        })
        .catch(error => {
            console.error("오류 발생:", error);
            alert("회원가입 중 문제가 발생했습니다. 다시 시도해주세요.");
        });
});

// 이메일 인증 코드 전송 버튼 이벤트
document.querySelector('.verify-btn').addEventListener('click', function () {
    const email = document.getElementById('email').value.trim();

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

// 이메일 유효성 검사 함수
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// 전화번호 유효성 검사 함수
function validatePhone(phone) {
    const phoneRegex = /^01[0-9]-\d{3,4}-\d{4}$/;
    return phoneRegex.test(phone);
};

// 중복 확인 버튼 이벤트
function checkDuplicate() {
    const memberId = document.getElementById("memberId").value;

    // 아이디가 비어있지 않으면 Ajax 요청
    if (memberId) {
        fetch(`/check-duplicate?id=${memberId}`)
            .then(response => {
                if (response.ok) {
                    return response.text(); // 서버에서 성공적인 응답을 받으면 메시지 반환
                } else {
                    throw new Error('중복된 아이디입니다.'); // 중복된 경우 오류 메시지
                }
            })
            .then(message => {
                alert("사용 가능한 아이디입니다."); // 중복되지 않은 ID일 경우
            })
            .catch(error => {
                alert(error.message); // 중복된 ID일 경우
            });
    } else {
        alert("아이디를 입력해주세요.");
    }
}

// 비밀번호 유효성 검사 함수 (문자, 숫자, 특수문자 포함 8~20자)
function validatePassword(password) {
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>]).{8,20}$/;
    return passwordRegex.test(password);
}

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

function checkPasswordMatch() {
    const password = document.getElementById('password').value.trim();
    const confirmPassword = document.getElementById('confirm-password').value.trim();
    const messageDiv = document.getElementById('confirm-password-message');

    // 둘 중 하나라도 입력되지 않으면 메시지를 숨김
    if (password === '' || confirmPassword === '') {
        messageDiv.style.display = 'none';
        return;
    }

    // 비밀번호가 다를 때
    if (password !== confirmPassword) {
        messageDiv.textContent = "비밀번호가 같지 않습니다.";
        messageDiv.style.color = 'red';
        messageDiv.style.display = 'block';
    }
    // 비밀번호가 같을 때
    else {
        messageDiv.textContent = "비밀번호가 같습니다.";
        messageDiv.style.color = 'green';
        messageDiv.style.display = 'block';
    }
}

function validateMemberId(memberId) {
    const memberIdRegex = /^[A-Za-z0-9]{6,20}$/;
    return memberIdRegex.test(memberId);
}


// 아이디 입력 시 유효성 검사
document.getElementById('memberId').addEventListener('input', function () {
    const memberId = this.value.trim();
    const messageDiv = document.getElementById('memberId-message'); // 아이디 메시지 div

    if (memberId === '') {
        messageDiv.style.display = 'none';  // 입력이 없으면 숨김
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

document.getElementById('password').addEventListener('input', checkPasswordMatch);
document.getElementById('confirm-password').addEventListener('input', checkPasswordMatch);