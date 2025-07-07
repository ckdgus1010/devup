const answerBox = document.getElementById("answer-box");
const userAnswerBox = document.getElementById("user-answer");

const questionId = document.getElementById("container").dataset.questionId;

const checkAnswerBtn = document.getElementById("check-answer-btn");
checkAnswerBtn.addEventListener("click", checkAnswer);

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
                    window.location.href = "/auth/signin";
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

async function checkAnswer() {
    const userAnswer = userAnswerBox.value.trim();

    if (userAnswer === '') {
        alert('정답을 입력해주세요.');
        return;
    }

    if (answerBox.classList.contains('show')) {
        alert('이미 정답을 확인했습니다.');
        return;
    }

    await fetchAndShowAnswer(userAnswer);
}

async function fetchAndShowAnswer(userAnswer) {
    const loadingMessageEl = document.getElementById('loading-message');
    loadingMessageEl.classList.toggle('show');

    try {
        const res = await fetch('/api/ai/answers', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'questionId': questionId,
                'userAnswer': userAnswer
            })
        });

        if (res.status === 401) {
            alert('로그인을 해주세요.');
            window.location.href = '/auth/signin';
        } else if (!res.ok) {
            const msg = `${res.status} :: 정답을 확인할 수 없습니다. 잠시 후 다시 시도해 주세요.`
            alert(msg);
            loadingMessageEl.innerText = msg;
        } else {
            const data = await res.json();

            // 결과 처리
            document.getElementById("result-sign").innerHTML = data.isCorrect
                ? '<p id="correct-sign" class="result-sign correct">✅ 맞았어요</p>'
                : '<p id="wrong-sign" class="result-sign wrong">❌ 틀렸어요</p>';

            document.getElementById("feedback-text").innerHTML = marked.parse(data.feedback);
            document.getElementById("model-answer-text").innerHTML = marked.parse(data.modelAnswer);

            loadingMessageEl.classList.toggle('show');
            answerBox.classList.toggle('show');
        }
    } catch (err) {
        alert(err);
    }
}