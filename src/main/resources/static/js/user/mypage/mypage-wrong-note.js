import {formatDate, getPageNumbers} from "/js/util/util.js";

/**
 * 오답 노트 목록 보여주기
 *
 * @param pageNumber 페이지 번호
 * @returns {Promise<void>}
 */
export async function showWrongNote(pageNumber) {
    try {
        const tbody = document.getElementById('wrong-body');
        const paginationEl = document.getElementById('wrong-pagination');
        tbody.innerHTML = '';
        paginationEl.innerHTML = '';

        const data = await fetchWrongNote(pageNumber);

        // 오답 노트 목록 보여주기
        renderWrongNoteTable(data, tbody);

        // 페이징 처리
        renderPagination(data, paginationEl);

        // 삭제 버튼에 이벤트 할당
        addDeleteButtonClickHandler();
    } catch (err) {
        alert(err);
    }
}

/**
 * 서버에 오답노트 데이터 요청하기
 *
 * @param pageNumber 페이지 번호
 * @returns {Promise<any>}
 */
async function fetchWrongNote(pageNumber) {
    const url = '/api/wrong?pageNumber=' + pageNumber;
    const response = await fetch(url, {
        method: "GET"
    });

    if (!response.ok) {
        throw new Error('오답노트를 불러오는 데 실패했습니다.');
    }

    return await response.json();
}

/**
 * 오답노트 테이블 렌더링
 *
 * @param data 서버에서 받은 오답노트 데이터
 * @param tbody 테이블 tbody 요소
 */
function renderWrongNoteTable(data, tbody) {
    data.content.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
                <td>${item.questionId}</td>
                <td><a href="/questions/${item.questionId}">${item.title}</a></td>
                <td>${item.category}</td>
                <td>${item.level}</td>
                <td>${formatDate(item.createdAt)}</td>
                <td>
                    <button class="delete-btn" data-question-id="${item.questionId}">
                        <span>삭제</span>
                    </button>
                </td>
            `;

        tbody.appendChild(row);
    })
}

/**
 * 오답노트 페이징 처리
 *
 * @param data 오답노트 데이터
 * @param paginationEl
 */
function renderPagination(data, paginationEl) {
    const pageNumbers = getPageNumbers(data.number, data.totalPages);

    pageNumbers.forEach(page => {
        const btn = document.createElement("button");
        btn.textContent = page + 1;

        if (page === data.number) {
            btn.classList.add("active");
            btn.disabled = true;
        }

        btn.addEventListener("click", () => showWrongNote(page));

        paginationEl.appendChild(btn);
    });
}

/**
 * [삭제] 버튼에 이벤트 할당하기
 */
function addDeleteButtonClickHandler() {
    const buttons = document.querySelectorAll(".delete-btn");

    buttons.forEach(button => {
        const questionId = button.getAttribute('data-question-id');
        button.addEventListener('click', () => deleteWrongNote(questionId));
    })
}

async function deleteWrongNote(questionId) {
    try {
        const response = await fetch(`/api/wrong/${questionId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('오답노트를 삭제했습니다.');
             await showWrongNote(0);
        } else {
            alert('오답노트를 삭제할 수 없습니다.');
        }
    } catch (err) {
        alert(err);
    }
}