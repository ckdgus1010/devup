const form = document.getElementById("login-form");
form.addEventListener('submit', handleLoginSubmit);

const submitBtn = document.getElementById("submit-btn");

async function handleLoginSubmit(event) {
    event.preventDefault();

    submitBtn.disabled = true;

    const loginId = form.loginId.value;
    const password = form.password.value;

    try {
        const url = '/api/auth/signin';
        const requestInit = {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                loginId,
                password
            })
        };

        const response = await fetch(url, requestInit);
        const data = await response.json();
        console.log(data);

        if (!response.ok) {
            throw new Error(data.message);
        }

        // 토큰 저장, 페이지 이동 등 후속 처리
        window.location.href = data.redirectUrl;
    } catch (err) {
        alert(err);
    } finally {
        submitBtn.disabled = false;
    }
}