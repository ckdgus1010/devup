const answerBox = document.getElementById("answer-box");
const answerText = document.getElementById("answer-text");
const userAnswerBox = document.getElementById("user-answer");

const questionId = document.getElementById("container").dataset.questionId;

const toggleBtn = document.getElementById("toggle-answer-btn");
toggleBtn.addEventListener("click", handleAnswerToggle);

const correctBtn = document.getElementById("correct-btn");
correctBtn.addEventListener("click", () => send(true));

const wrongBtn = document.getElementById("wrong-btn");
wrongBtn.addEventListener("click", () => send(false));

const bookmarkBtn = document.getElementById("bookmark-btn");
bookmarkBtn.addEventListener('click', () => toggleBookmark(bookmarkBtn));

async function toggleBookmark(button) {
    const isBookmarked = button.classList.contains('bookmarked');

    try {
        const response = await fetch(`/api/bookmarks/${questionId}`, {
            method: isBookmarked ? 'DELETE' : 'POST'
        });

        switch (response.status) {
            case 200: {
                confirm(isBookmarked ? '북마크를 삭제했습니다.' : '북마크를 저장했습니다.');

                button.classList.toggle('bookmarked');
                button.innerText = isBookmarked ? '🔖 북마크 추가' : '❌ 북마크 삭제';

                break;
            }
            case 401: {
                const confirmed = confirm('로그인이 필요합니다.\n[확인]을 누르면 로그인 화면으로 이동합니다.');
                if (confirmed) {
                    window.location.href="/auth/signin";
                }
                break;
            }
            default: {
                console.log(response.status);
                alert('알 수 없는 에러가 발생했습니다.');
            }
        }
    } catch (err) {
        alert(err);
    }
}

async function handleAnswerToggle() {
    const isAnswerShown = toggleAnswerBox();
    updateToggleButton(isAnswerShown);

    if (!isAnswerShown || answerText.textContent !== '') {
        return;
    }

    await fetchAndShowAnswer();
}

function toggleAnswerBox() {
    answerBox.classList.toggle('show');
    return answerBox.classList.contains('show');
}

function updateToggleButton(isVisible) {
    toggleBtn.textContent = isVisible ? "정답 숨기기" : "정답 보기";
}

async function fetchAndShowAnswer() {
    try {
        answerText.textContent = '정답을 불러오는 중입니다.';

        const res = await fetch('/api/answers/' + questionId);
        const data = await res.json();

        answerText.innerHTML = marked.parse(data.answerText);
    } catch (err) {
        alert(err);
    }
}

async function send(isCorrect) {
    const userAnswer = userAnswerBox.value.trim();

    if (userAnswer === '') {
        alert('정답을 입력해주세요.');
        return;
    }

    try {
        const response = await fetch('/api/user/answer', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'questionId': questionId,
                'answerText': userAnswer,
                'isCorrect': isCorrect
            })
        });

        if (response.status === 401) {
            alert('로그인을 해주세요.');
            window.location.href = '/auth/signin';
        } else if (!response.ok) {
            alert(`${response.status} :: 풀이를 저장할 수 없습니다.`);
        } else {
            alert('풀이를 저장했습니다.');
        }
    } catch (err) {
        alert(err);
    }
}