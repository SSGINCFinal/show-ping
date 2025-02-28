package com.ssginc.showping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * VOD 응답정보를 보내주기 위해 정의한 DTO 클래스
 * <p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamResponseDto {

    private Long streamNo;                  // 영상 번호
    private String streamTitle;             // 영상 제목
    private Long categoryNo;                // 카테고리 번호
    private String categoryName;            // 카테고리 이름
    private String productName;             // 상품 이름
    private Long productPrice;              // 상품 가격
    private int productSale;                // 상품 할인율
    private String productImg;              // 상품 이미지
    private LocalDateTime streamStartTime;  // 영상 시작 시간
    private LocalDateTime streamEndTime;    // 영상 종료 시간

}
