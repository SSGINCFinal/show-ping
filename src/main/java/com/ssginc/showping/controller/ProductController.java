package com.ssginc.showping.controller;

import com.ssginc.showping.dto.response.ProductDto;
import com.ssginc.showping.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{categoryNo}")
    public List<ProductDto> getProductsByCategory(@PathVariable Long categoryNo) {
        return productService.getProductsByCategory(categoryNo);
    }
}
