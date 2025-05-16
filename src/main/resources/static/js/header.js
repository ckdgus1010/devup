async function logout() {
    try {
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error("로그아웃을 할 수 없습니다.");
        }

        const data = await response.json();
        window.location.href = data.redirectUrl;
    } catch (err) {
        alert(err);
    }
}