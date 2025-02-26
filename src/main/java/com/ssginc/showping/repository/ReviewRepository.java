package com.ssginc.showping.repository;

import com.ssginc.showping.entity.Product;
import com.ssginc.showping.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductProductNo(Long productNo);
}