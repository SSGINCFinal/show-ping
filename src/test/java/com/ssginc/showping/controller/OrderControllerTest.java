package com.ssginc.showping.controller;

import com.ssginc.showping.dto.request.OrderRequestDto;
import com.ssginc.showping.dto.response.OrderItemDto;
import com.ssginc.showping.repository.OrdersRepository;
import com.ssginc.showping.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
public class OrderControllerTest {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderService orderService;

    @Test
    public void testCreateBulkOrders() {
        int numberOfOrders = 10000;
        List<OrderRequestDto> orderRequestDtos = new ArrayList<>();

        for (int i = 0; i < numberOfOrders; i++) {
            OrderRequestDto dto = new OrderRequestDto();
            dto.setMemberNo(1L);
            dto.setTotalPrice(10000L + i);
            dto.setOrderItems(List.of(new OrderItemDto(1L, 2L, 5000L)));
            orderRequestDtos.add(dto);
        }

        // 10000개의 주문 생성
        orderRequestDtos.forEach(orderService::createOrder);

        // 주문 수 확인
        long orderCount = ordersRepository.count();
        assertEquals(numberOfOrders, orderCount, "10000개의 주문이 정상적으로 저장되지 않았습니다.");
    }

    @Test
    void testConcurrentOrders() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                OrderRequestDto requestDto = new OrderRequestDto();
                requestDto.setMemberNo(1L);
                requestDto.setTotalPrice(10000L);
                requestDto.setOrderItems(List.of(new OrderItemDto(1L, 1L, 10000L)));

                orderService.createOrder(requestDto);
                latch.countDown();
            });
        }

        latch.await(); // 모든 스레드가 실행 완료될 때까지 대기

        long orderCount = ordersRepository.count();
        System.out.println("저장된 주문 개수: " + orderCount);
        assertEquals(100, orderCount, "100개의 주문이 정상적으로 저장되지 않았습니다.");
    }
}
