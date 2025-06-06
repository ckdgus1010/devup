import {showProfile} from '/js/user/mypage/mypage-profile.js';
import {showWrongNote} from '/js/user/mypage/mypage-wrong-note.js';
import {showBookmarks} from '/js/user/mypage/mypage-bookmarks.js';

const tabContainer = document.getElementById('tab-container');
const buttons = document.querySelectorAll(".tab-button");
let currentTabButton = null;

const tabActionMap = {
    stats: () => {},
    history: () => showUserSolvedQuestions(0),
    wrong: () => showWrongNote(0),
    bookmarks: () => showBookmarks(0),
    profile: showProfile
};

document.addEventListener('DOMContentLoaded', () => {
    currentTabButton = document.getElementById("stats-tab-btn");
    changeTab('stats');
});

buttons.forEach(btn => {
    btn.addEventListener("click", () => {
        changeCurrentTabButton(btn);
        changeTab(btn.dataset.tab);
    });
});

// 탭 버튼 바꾸기
function changeCurrentTabButton(btn) {
    if (currentTabButton !== null) {
        currentTabButton.classList.remove('active');
    }

    btn.classList.add('active');
    currentTabButton = btn;
}

// 탭 전환하기
async function changeTab(tabName) {
    tabContainer.innerHTML = "";
    tabContainer.innerHTML = "데이터를 불러오는 중입니다.";

    const tabTemplate = document.getElementById(tabName);

    if (!tabTemplate) {
        return;
    }

    tabContainer.innerHTML = "";

    const clonedTemplate = tabTemplate.content.cloneNode(true);
    tabContainer.appendChild(clonedTemplate);

    const action = tabActionMap[tabName];
    if (typeof action === 'function') {
        await action();
    }
}

// 풀이 이력 목록 보여주기
async function showUserSolvedQuestions(pageNumber) {
    try {
        const tbody = document.getElementById('history-body');
        const paginationEl = document.getElementById('history-pagination');
        tbody.innerHTML = '';
        paginationEl.innerHTML = '';

        const url = '/api/user/history?pageNumber=' + pageNumber;
        const response = await fetch(url, {method: "GET"});
        const data = await response.json();

        // 표에 서버에서 받은 목록 데이터 보여주기
        renderTable(data.content, tbody, renderHistoryRow);

        // 페이징 처리
        renderPagination(data.number, data.totalPages, paginationEl, showUserSolvedQuestions);
    } catch (err) {
        alert(err);
    }
}

function renderHistoryRow(item) {
    const resultText = item.correct
        ? "<span class='tag correct'>정답</span>"
        : "<span class='tag wrong'>오답</span>";

    return `
        <td>${item.questionId}</td>
        <td><a href="/user/history/${item.id}">${item.questionTitle}</a></td>
        <td>${item.category}</td>
        <td>${item.level}</td>
        <td>${formatDate(item.solvedAt)}</td>
        <td>${resultText}</td>
    `;
}

function renderTable(content, tbody, rowRenderer) {
    content.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = rowRenderer(item);

        tbody.appendChild(row);
    });
}

// 페이징 처리
function renderPagination(currentPage, totalPages, paginationEl, clickHandler) {
    const pageNumbers = getPageNumbers(currentPage, totalPages);

    pageNumbers.forEach(page => {
        const btn = document.createElement("button");
        btn.textContent = page + 1;
        // btn.className = "page-btn";

        if (page === currentPage) {
            btn.classList.add("active");
            btn.disabled = true;
        }

        btn.addEventListener("click", () => clickHandler(page));

        paginationEl.appendChild(btn);
    });
}

// 페이징 처리 시 표시할 페이지 번호 확인
function getPageNumbers(currentPage, totalPages, maxPages = 5) {
    if (totalPages === 0) {
        return [];
    }

    const pages = [];

    // 페이지 번호는 0부터 시작
    const half = Math.floor(maxPages / 2);
    let start = Math.max(0, currentPage - half);
    let end = start + maxPages;

    // 범위를 초과하면 조정
    if (end > totalPages) {
        end = totalPages;
        start = Math.max(0, end - maxPages);
    }

    for (let i = start; i < end; i++) {
        pages.push(i);
    }

    return pages;
}

function formatDate(dateStr) {
    if (dateStr == null) {
        return "";
    }

    const date = new Date(dateStr);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}
