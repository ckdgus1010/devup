const form = document.getElementById("login-form");
form.addEventListener('submit', handleLoginSubmit);

const submitBtn = document.getElementById("submit-btn");

async function handleLoginSubmit(event) {
    event.preventDefault();

    submitBtn.disabled = true;

    try {
        const response = await fetch('/api/auth/signin', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                'loginId': form.loginId.value,
                'password': form.password.value
            })
        });

        if (response.status === 401) {
            const data = await response.json();
            throw new Error(data.message);
        }

        if (!response.ok) {
            throw new Error("로그인 실패");
        }

        const data = await response.json();
        window.location.href = data.redirectUrl;
    } catch (err) {
        alert(err);
    } finally {
        submitBtn.disabled = false;
    }
}