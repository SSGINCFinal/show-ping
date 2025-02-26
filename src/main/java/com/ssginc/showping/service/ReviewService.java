package com.ssginc.showping.service;

import com.ssginc.showping.dto.response.ReviewDto;
import com.ssginc.showping.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<ReviewDto> getReviewsByProductNo(Long productNo) {
        return reviewRepository.findByProductProductNo(productNo).stream()
                .map(review -> new ReviewDto(
                        review.getReviewNo(),
                        review.getMember().getMemberName(), // 회원 이름 가져오기
                        review.getReviewRating(),
                        review.getReviewComment(),
                        review.getReviewCreateAt(),
                        review.getReviewUrl()
                ))
                .collect(Collectors.toList());
    }
}
