package com.ssginc.showping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long reviewNo;
    private String memberName;
    private Long reviewRating;
    private String reviewComment;
    private LocalDateTime reviewCreateAt;
    private String reviewUrl;
}