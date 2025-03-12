let currentPage = 0; // 현재 페이지 번호
const itemsPerPage = 8; // 한 번에 로드할 상품 개수
const categoryNo = window.location.pathname.split('/').pop(); // URL에서 categoryNo 추출

document.addEventListener("DOMContentLoaded", function () {
    loadProducts();
});

// "더보기" 버튼 클릭 시 추가 상품 요청
document.getElementById('load-more').addEventListener('click', loadProducts);

function loadProducts() {
    axios.get(`/api/products/${categoryNo}?page=${currentPage}&size=${itemsPerPage}`)
        .then(response => {

            const products = response.data.content; // Page 객체의 content 값 (상품 리스트)
            renderProducts(products);
            currentPage++;

            // 모든 상품이 로드되었으면 '더보기' 버튼 숨김
            if (response.data.last) {
                document.getElementById('load-more').style.display = 'none';
            } else {
                document.getElementById('load-more').style.display = 'block';
            }
        })
        .catch(error => {
            console.error("상품 목록을 불러오는 중 오류 발생:", error);
        });
}

function renderProducts(products) {
    const productGrid = document.getElementById('product-grid');

    products.forEach(product => {
        const productDiv = document.createElement('div');
        productDiv.classList.add('product-item');
        const formattedPrice = product.productPrice.toLocaleString('ko-KR');

        productDiv.innerHTML = `
            <img src="/img/product_img/${product.productImg}" alt="${product.productName}" />
            <p class="product-name">${product.productName}</p>
            <p class="product-price">${formattedPrice}원</p>
        `;

        productDiv.addEventListener('click', () => {
            window.location.href = `/product/detail/${product.productNo}`;
        });

        productGrid.appendChild(productDiv);
    });
}