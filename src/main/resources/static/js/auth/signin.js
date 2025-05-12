const form = document.getElementById("login-form");
form.addEventListener('submit', handleLoginSubmit);

async function handleLoginSubmit(event) {
    event.preventDefault();

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
        localStorage.setItem("token", data.token);
        window.location.href = data.redirectUrl;
    } catch (err) {
        alert(err);
    }
}