package com.ssginc.showping.controller;

import com.ssginc.showping.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, String> request) {
        String impUid = request.get("impUid");
        if (impUid == null) {
            return ResponseEntity.badRequest().body("impUid가 없습니다.");
        }

        String result = paymentService.verifyPayment(impUid);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestBody Map<String, Object> requestData) {
        String paymentId = (String) requestData.get("paymentId");

        if (paymentId == null || paymentId.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid payment ID.");
        }

        // 결제 완료 처리 (예: 데이터베이스에 저장 등)
        System.out.println("결제 완료: " + paymentId);

        // 결제 상태 응답
        Map<String, String> response = new HashMap<>();
        response.put("status", "PAID");
        return ResponseEntity.ok(response);
    }
}