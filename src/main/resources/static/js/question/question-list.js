import {getPageNumbers, formatDate} from "/js/util/util.js";

const searchBtn = document.getElementById('search-btn');
searchBtn.addEventListener('click', () => search(0));

async function search(pageNumber) {
    searchBtn.disabled = true;

    try {
        const word = document.getElementById('search-input').value;

        if (word === undefined || word === null || word.trim().length < 2) {
            alert('검색어는 2자 이상이어야 합니다.');
            return;
        }

        const response = await fetch(
            `/api/questions/search?title=${word}&pageNumber=${pageNumber}`,
            {method: 'GET'});

        const data = await response.json();

        if (!response.ok) {
            alert(data.message);
            return;
        }

        if (data.empty) {
            alert('검색 결과가 없습니다. 다른 단어로 검색해주세요');
            return;
        }

        renderSearchResult(data.content);
        renderPagination(data.number, data.totalPages);
    } catch (err) {
        alert(err);
    } finally {
        searchBtn.disabled = false;
    }
}

function renderSearchResult(content) {
    const tbody = document.getElementById("table-body");
    tbody.innerHTML = '';

    content.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="q_no">${item.id}</td>
            <td class="q_title"><a href="/questions/${item.id}">${item.title}</a></td>
            <td class="q_category">${item.category}</td>
            <td class="q_level">${item.level}</td>
            <td class="q_created_at">${formatDate(item.createdAt)}</td>
        `;

        tbody.appendChild(row);
    });
}

function renderPagination(currentPage, totalPages) {
    const paginationEl = document.getElementById("pagination");
    paginationEl.innerHTML = '';

    const pageNumbers = getPageNumbers(currentPage, totalPages);
    pageNumbers.forEach(page => {
        const a = document.createElement("a");
        a.textContent = page + 1;

        if (page === currentPage) {
            a.classList.add("active");
        } else {
            a.addEventListener("click", () => search(page));
        }

        const div = document.createElement("div");
        div.appendChild(a);

        paginationEl.appendChild(div);
    });
}