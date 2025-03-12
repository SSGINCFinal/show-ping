package com.ssginc.showping.controller;

import com.ssginc.showping.dto.response.ProductDto;
import com.ssginc.showping.dto.response.ReviewDto;
import com.ssginc.showping.entity.Product;
import com.ssginc.showping.service.ProductService;
import com.ssginc.showping.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{categoryNo}")
    public Page<ProductDto> getProductsByCategory(
            @PathVariable Long categoryNo,
            @RequestParam(defaultValue = "0") int page, // 현재 페이지 번호
            @RequestParam(defaultValue = "8") int size // 한 번에 가져올 상품 개수
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getProductsByCategory(categoryNo, pageable);
    }

    @GetMapping("/detail/{productNo}")
    public ProductDto getProductDetail(@PathVariable Long productNo) {
        return productService.getProductById(productNo);
    }

    @GetMapping("/reviews/{productNo}")
    public List<ReviewDto> getProductReviews(@PathVariable Long productNo) {
        return reviewService.getReviewsByProductNo(productNo);
    }

}
