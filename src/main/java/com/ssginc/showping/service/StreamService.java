package com.ssginc.showping.service;

import com.ssginc.showping.dto.response.StreamResponseDto;

import java.util.List;

public interface StreamService {

    List<StreamResponseDto> getAllVod();

    List<StreamResponseDto> getAllVodByCategory(Long categoryNo);

}