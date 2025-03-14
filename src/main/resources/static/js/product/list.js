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
        const formattedPrice = product.productPrice.toLocaleString('ko-KR'); // 가격 콤마 포맷팅
        const formattedFinalPrice = product.discountedPrice.toLocaleString('ko-KR');
        const productSale = product.productSale;

        console.log(productSale);

        productDiv.innerHTML = `
            <div class="product-img-container">
                <img id="product-sale-icon" src="/img/icon/sale.png" alt="product-sale" class="sale-icon" style="width: 50px" />
                <img src="${product.productImg}" alt="${product.productName}" class="product-img" />
            </div>
            <p class="product-name">${product.productName}</p>
            <p class="product-sale" id="product-sale" style="text-decoration: line-through; font-size: 15px">${formattedPrice}원</p>
            <p class="product-sale-percent" id="product-sale-percent" style="color: red; font-size: 15px">${product.productSale} %</p>
            <p class="product-price-final" id="product-price-final" style="font-size: 20px">${formattedFinalPrice}원</p>
        `;

        // productSale이 0일 경우 product-sale과 sale 아이콘을 숨기고 product-price-final만 보이기
        if (productSale === 0) {
            productDiv.querySelector(".product-sale").style.display = "none";
            productDiv.querySelector("#product-sale-icon").style.display = "none";
            productDiv.querySelector("#product-sale-percent").style.display = "none";
        } else {
            productDiv.querySelector(".product-sale").style.display = "block";
            productDiv.querySelector("#product-sale-icon").style.display = "block";
            productDiv.querySelector("#product-sale-percent").style.display = "block";
        }

        productDiv.addEventListener('click', () => {
            window.location.href = `/product/detail/${product.productNo}`;
        });

        productGrid.appendChild(productDiv);
    });
}
