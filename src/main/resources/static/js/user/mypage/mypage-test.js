const tabContainer = document.getElementById('tab-container');
let currentTabButton = null;

document.addEventListener('DOMContentLoaded', () => {
    currentTabButton = document.getElementById("stats-tab-btn");
    showTab('stats');
});

document.querySelectorAll(".tab-button").forEach(btn => {
    btn.addEventListener("click", () => {
        changeCurrentTabButton(btn);
        showTab(btn.dataset.tab);
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
function showTab(tabName) {
    tabContainer.innerHTML = '';

    const template = document.getElementById(tabName);

    if (template) {
        const content = template.content.cloneNode(true);
        tabContainer.appendChild(content);
    } else {
        alert(`${tabName} 탭을 찾을 수 없습니다.`);
    }

}
