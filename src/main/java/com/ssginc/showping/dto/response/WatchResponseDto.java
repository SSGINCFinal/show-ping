package com.ssginc.showping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 시청 내역 정보를 보내주기 위해 정의한 DTO 클래스
 * <p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchResponseDto {

    private Long streamNo;
    private String streamTitle;
    private String productImg;
    private String productName;
    private Long productPrice;
    private LocalDateTime watchTime;

}
