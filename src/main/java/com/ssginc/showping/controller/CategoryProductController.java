package com.ssginc.showping.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class CategoryProductController {

    @GetMapping("/category/{categoryNo}")
    public String viewCategoryProducts(@PathVariable Long categoryNo, Model model) {
        model.addAttribute("categoryNo", categoryNo);
        return "product/product_list"; // category.html 렌더링
    }

    @GetMapping("/product/detail/{productNo}")
    public String viewProductDetail(@PathVariable Long productNo, Model model) {
        model.addAttribute("productNo", productNo);
        model.addAttribute("reviewNo", productNo);
        return "product/product_detail"; // templates/product/productDetail.html 반환
    }
}