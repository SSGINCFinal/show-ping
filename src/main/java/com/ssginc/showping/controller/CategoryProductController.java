package com.ssginc.showping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CategoryProductController {

    @GetMapping("/category/{categoryNo}")
    public String viewCategoryProducts(@PathVariable Long categoryNo, Model model) {
        model.addAttribute("categoryNo", categoryNo);
        return "product/product_list"; // category.html 렌더링
    }
}
