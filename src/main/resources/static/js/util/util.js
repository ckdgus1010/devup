// 페이징 처리 시 표시할 페이지 번호 확인
export function getPageNumbers(currentPage, totalPages, maxPages = 5) {
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

export function formatDate(dateStr) {
    if (dateStr === undefined || dateStr === null) {
        return "";
    }

    const date = new Date(dateStr);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}