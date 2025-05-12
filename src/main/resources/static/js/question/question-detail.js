const toggleBtn = document.getElementById("toggle-answer-btn");
const answerBox = document.getElementById("answer-box");
const answerText = document.getElementById("answer-text");

const correctBtn = document.getElementById("correct-btn");
const wrongBtn = document.getElementById("wrong-btn");
const userAnswerBox = document.getElementById("user-answer");

toggleBtn.addEventListener("click", handleAnswerToggle);

correctBtn.addEventListener("click", () => send(true));
wrongBtn.addEventListener("click", () => send(false));

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

        const questionId = document.getElementById("container").dataset.questionId;
        const res = await fetch('/api/answers/' + questionId);
        const data = await res.json();

        answerText.textContent = data.answerText;
    } catch (err) {
        alert(err);
    }
}

function send(isCorrect) {
    const userAnswer = userAnswerBox.value.trim();

    if (userAnswer === '') {
        alert('정답을 입력해주세요.');
        return;
    }

    // TODO: send to server
    console.log(userAnswer);
    console.log(isCorrect);
}