export async function showProfile() {
    const loginIdEl = document.getElementById("login_id");
    const nicknameEl = document.getElementById("nickname");
    const emailEl = document.getElementById("email");

    loginIdEl.value = '';
    nicknameEl.value = '';
    emailEl.value = '';

    try {
        const url = '/api/user/account';
        const response = await fetch(url, {method: "GET"});
        const data = await response.json();

        loginIdEl.value = data.loginId;
        nicknameEl.value = data.nickname;
        emailEl.value = data.email;

        setHandlersToProfileButtons();
    } catch (err) {
        alert(err);
    }
}

export function setHandlersToProfileButtons() {
    const nicknameBtn = document.getElementById("nickname-btn");
    const emailBtn = document.getElementById("email-btn");

    nicknameBtn.addEventListener('click', updateNickname);
    emailBtn.addEventListener('click', updateEmail);
}

// 닉네임 수정
async function updateNickname() {
    const nicknameInput = document.getElementById("nickname");
    const newNickname = nicknameInput.value;

    if (newNickname == null || newNickname.trim().length === 0) {
        alert("닉네임을 확인해주세요.");
        return;
    }

    try {
        const response = await fetch('/api/user/account', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'type': 'nickname',
                'nickname': newNickname
            })
        });
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message);
        }

        alert("닉네임을 수정했습니다.")
        nicknameInput.value = data.nickname;
    } catch (err) {
        alert(err);
    }
}

// 이메일 수정
async function updateEmail() {
    const emailInput = document.getElementById("email");
    const newEmail = emailInput.value;

    if (newEmail == null || newEmail.trim().length === 0) {
        alert("이메일을 확인해주세요.");
        return;
    }

    try {
        const response = await fetch('/api/user/account', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'type': 'email',
                'email': newEmail
            })
        });
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message);
        }

        alert("이메일을 수정했습니다.")
        emailInput.value = data.email;
    } catch (err) {
        alert(err);
    }
}