package com.ssginc.showping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTestService {

    private final StringRedisTemplate redisTemplate;

    public void testRedis() {
        String key = "testKey";
        String value = "Redis 연결 성공!";

        // Redis에 값 저장 (5분간 유지)
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(5));

        // 저장된 값 가져오기
        String storedValue = redisTemplate.opsForValue().get(key);
        System.out.println("✅ Redis에서 가져온 값: " + storedValue);
    }
}
