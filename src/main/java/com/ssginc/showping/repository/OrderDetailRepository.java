package com.ssginc.showping.repository;

import com.ssginc.showping.entity.OrderDetail;
import com.ssginc.showping.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // 특정 주문의 상세 목록 조회
    List<OrderDetail> findByOrder(Orders order);
}
