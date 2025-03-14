package com.ssginc.showping.service;

import com.ssginc.showping.dto.request.WatchRequestDto;
import com.ssginc.showping.dto.response.WatchResponseDto;
import com.ssginc.showping.entity.Watch;

import java.util.List;

public interface WatchService {

    List<WatchResponseDto> getWatchHistoryByMemberNo(Long memberNo);

    Watch insertWatchHistory(WatchRequestDto watchRequestDto);

}
