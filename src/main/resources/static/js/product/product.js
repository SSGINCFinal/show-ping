document.addEventListener("DOMContentLoaded", function () {
    const categoryNo = window.location.pathname.split('/').pop(); // URL에서 categoryNo 추출
    loadProducts(categoryNo);
});

function loadProducts(categoryNo) {
    axios.get(`/api/products/${categoryNo}`)
        .then(response => {
            const products = response.data;
            const productGrid = document.getElementById('product-grid');
            productGrid.innerHTML = '';

            if (products.length === 0) {
                productGrid.innerHTML = '<p>등록된 상품이 없습니다.</p>';
            } else {
                products.forEach(product => {
                    const productDiv = document.createElement('div');
                    productDiv.classList.add('product-item');
                    productDiv.innerHTML = `
                        <img src="/img/product_img/${product.productImg}" alt="${product.productName}" />
                        <p id="product-name">${product.productName}</p>
                        <p id="product-price">${product.productPrice}원</p>
                    `;
                    productGrid.appendChild(productDiv);
                });
            }
        })
        .catch(error => {
            console.error("상품 목록을 불러오는 중 오류 발생:", error);
        });
}


const products = Array.from({length: 36}, (_, i) => ({
    name: `상품명 ${i + 1}`,
    price: `${(i + 1) * 1000}원`
}));

const productsPerPage = 12;
let currentPage = 1;

function displayProducts(page) {
    const start = (page - 1) * productsPerPage;
    const end = start + productsPerPage;
    const productContainer = document.getElementById("product-container");
    productContainer.innerHTML = "";

    products.slice(start, end).forEach(product => {
        const productCard = document.createElement("div");
        productCard.classList.add("product-card");
        productCard.innerHTML = `
                    <div class="product-box"></div>
                    <h3>${product.name}</h3>
                    <p class="price">${product.price}</p>
                `;
        productContainer.appendChild(productCard);
    });
}

function setupPagination() {
    const paginationContainer = document.getElementById("pagination");
    paginationContainer.innerHTML = "";
    const totalPages = Math.ceil(products.length / productsPerPage);

    for (let i = 1; i <= totalPages; i++) {
        const pageLink = document.createElement("a");
        pageLink.textContent = i;
        pageLink.href = "#";
        pageLink.classList.toggle("active", i === currentPage);
        pageLink.addEventListener("click", function (event) {
            event.preventDefault();
            currentPage = i;
            displayProducts(currentPage);
            setupPagination();
        });
        paginationContainer.appendChild(pageLink);
    }
}

displayProducts(currentPage);
setupPagination();