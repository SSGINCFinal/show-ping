package com.ssginc.showping.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PortOneService {
    private static final String PORTONE_API_URL = "https://api.portone.io";
    private static final String API_KEY = "Z93IX13AumKAYSZxgF1rEfUxwiSds9YvjAyO79aTdWEF6jw4jYT5XaHX9PH6u37FQZzCVPTuqUh5TElIvwDc/Q=="; // PortOne API Key
    private static final String API_SECRET = "o4J1PjhlvKX8ZfB2mTgKWGSHV5ytz35ehdnV1SZc0ZWRRdDvM7NYZbyhcv6CoC6QzEoR6FIWnKkRVj9j"; // PortOne API Secret

    public String getPortOneAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("imp_key", API_KEY);
        body.put("imp_secret", API_SECRET);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(PORTONE_API_URL + "/users/getToken", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return (String) ((Map<String, Object>) response.getBody().get("response")).get("access_token");
        } else {
            throw new RuntimeException("PortOne 인증 토큰 발급 실패");
        }
    }
}
