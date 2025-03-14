package com.ssginc.showping.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchRequestDto {

    private Long streamNo;
    private Long memberNo;
    private LocalDateTime watchTime;

}
