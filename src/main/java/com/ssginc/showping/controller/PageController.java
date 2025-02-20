package com.ssginc.showping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class PageController {
    @GetMapping("/signup")
    public String signup() {
        return "login/signup"; // signup.html 파일을 반환
    }
}
