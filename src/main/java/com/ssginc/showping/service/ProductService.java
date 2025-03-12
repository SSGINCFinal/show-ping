package com.ssginc.showping.service;

import com.ssginc.showping.dto.response.ProductDto;
import com.ssginc.showping.entity.Product;
import com.ssginc.showping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Value("${ncp.storage.product-url}")
    private String productUrl;

    public Page<ProductDto> getProductsByCategory(Long categoryNo, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryCategoryNo(categoryNo, pageable);

        List<ProductDto> productDtoList = productPage.getContent().stream()
                .map(product -> new ProductDto(
                        product.getProductNo(),
                        product.getProductName(),
                        product.getProductPrice(),
                        product.getProductQuantity(),
                        product.getProductImg(),
                        product.getProductDescript()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(productDtoList, pageable, productPage.getTotalElements());
    }

    public ProductDto getProductById(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return new ProductDto(
                    product.getProductNo(),
                    product.getProductName(),
                    product.getProductPrice(),
                    product.getProductQuantity(),
                    product.getProductImg(),
                    product.getProductDescript()
            );
        }else{
            throw new RuntimeException("상품을 찾을 수 없습니다: " + productId);
        }
    }
}
