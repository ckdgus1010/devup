function validateSignup() {
    if (!validateLoginId()) return false;
    if (!validatePassword()) return false;
    if (!validateNickname()) return false;
    if (!validateEmail()) return false;
    return true;
}

function validateLoginId() {
    const loginIdInput = document.getElementById('login_id');
    const raw = loginIdInput.value;
    const trimmed = raw.trim();

    if (!trimmed) {
        alert("아이디를 입력해주세요.");
        return false;
    }

    if (raw.length !== trimmed.length) {
        alert("아이디의 앞뒤에 공백을 포함할 수 없습니다.");
        return false;
    }

    return true;
}

function validatePassword() {
    const password = document.getElementById('password').value;
    const passwordConfirm = document.getElementById('password_confirm').value;

    if (!password) {
        alert("비밀번호를 입력해주세요.");
        return false;
    }

    if (!isStrongPassword(password)) {
        alert("비밀번호는 8자 이상, 영대소문자/숫자/특수문자를 각각 1개 이상 포함해야 합니다.");
        return false;
    }

    if (password !== passwordConfirm) {
        alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        return false;
    }

    return true;
}

function validateNickname() {
    const nickname = document.getElementById('nickname').value.trim();

    if (!nickname) {
        alert("닉네임을 입력해주세요.");
        return false;
    }

    return true;
}

function validateEmail() {
    const email = document.getElementById('email').value.trim();
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email) {
        alert("이메일을 입력해주세요.");
        return false;
    }

    if (!emailPattern.test(email)) {
        alert("올바른 이메일 형식이 아닙니다.");
        return false;
    }

    return true;
}

function isStrongPassword(pw) {
    return (
        pw.length >= 8 &&
        /[A-Z]/.test(pw) &&
        /[a-z]/.test(pw) &&
        /\d/.test(pw) &&
        /[!@#$%^&*(),.?":{}|<>]/.test(pw)
    );
}
