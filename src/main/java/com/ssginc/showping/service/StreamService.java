package com.ssginc.showping.service;

import com.ssginc.showping.dto.response.VodResponseDto;

import java.util.List;

public interface StreamService {

    List<VodResponseDto> getAllVod();

    List<VodResponseDto> getAllVodByCategory(Long categoryNo);

}