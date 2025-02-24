package com.ssginc.showping.service;

import com.ssginc.showping.dto.response.ProductDto;
import com.ssginc.showping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDto> getProductsByCategory(Long categoryNo) {
        return productRepository.findByCategoryCategoryNo(categoryNo)
                .stream()
                .map(product -> new ProductDto(
                        product.getProductNo(),
                        product.getProductName(),
                        product.getProductPrice(),
                        product.getProductQuantity(),
                        product.getProductImg(),
                        product.getProductDescript()
                ))
                .collect(Collectors.toList());
    }
}
