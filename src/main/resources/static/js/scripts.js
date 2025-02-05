// 버튼 요소 가져오기
const scrollToTopButton = document.getElementById("scrollToTop");

// 스크롤 이벤트 추가
window.addEventListener("scroll", () => {
    if (window.scrollY > 200) {
        scrollToTopButton.classList.add("show");
        scrollToTopButton.classList.remove("hide");
    } else {
        scrollToTopButton.classList.add("hide");
        scrollToTopButton.classList.remove("show");
    }
});

// 버튼 클릭 이벤트
scrollToTopButton.addEventListener("click", () => {
    window.scrollTo({
        top: 0,           // 맨 위로 이동
        behavior: "smooth" // 부드럽게 스크롤
    });
});

