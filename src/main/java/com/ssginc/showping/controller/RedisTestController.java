package com.ssginc.showping.controller;

import com.ssginc.showping.service.RedisTestService;
import groovy.lang.Lazy;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisTestService redisTestService;

    @GetMapping("/redis")
    public String testRedis() {
        redisTestService.testRedis();
        return "Redis 테스트 완료! 콘솔 로그를 확인하세요.";
    }
}
