package com.ssginc.showping.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PaymentService {
    private final PortOneService portOneService;
    private final RestTemplate restTemplate = new RestTemplate();

    public PaymentService(PortOneService portOneService) {
        this.portOneService = portOneService;
    }

    public String verifyPayment(String impUid) {
        String url = "https://api.portone.io/payment/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + portOneService.getPortOneAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> paymentData = (Map<String, Object>) response.getBody().get("response");

            if (paymentData != null && paymentData.get("status").equals("paid")) {
                System.out.println("결제 검증 성공: " + paymentData);
                return "결제 검증 성공";
            } else {
                System.out.println("결제 상태가 paid가 아님: " + paymentData);
                return "결제 실패: 상태가 paid가 아님";
            }
        }
        throw new IllegalStateException("결제 검증 실패");
    }
}
