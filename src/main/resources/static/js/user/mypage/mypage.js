const tabContainer = document.getElementById('tab-container');
const buttons = document.querySelectorAll(".tab-button");
let currButton = null;


document.addEventListener('DOMContentLoaded', () => {
    showTab('stats');
    currButton = document.getElementById("stats-tab-btn");
});

buttons.forEach(btn => {
    btn.addEventListener("click", () => {
        changeButton(btn);
        showTab(btn.dataset.tab);
    });
});

function changeButton(btn) {
    if (currButton !== null) {
        currButton.classList.remove('active');
    }

    btn.classList.add('active');
    currButton = btn;
}

function showTab(tabName) {
    const template = document.getElementById(tabName);

    if (template) {
        tabContainer.innerHTML = "";

        const content = template.content.cloneNode(true);
        tabContainer.appendChild(content);
    }
}