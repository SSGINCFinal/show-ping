package com.ssginc.showping.service;

import com.ssginc.showping.dto.response.StreamResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StreamService {

    List<StreamResponseDto> getAllVod();

    List<StreamResponseDto> getAllVodByCategory(Long categoryNo);

    StreamResponseDto getLive();

    StreamResponseDto getVodByNo(Long streamNo);

    Mono<Resource> getVideo(String title);

    Page<StreamResponseDto> getAllVodByPage(Pageable pageable);

}