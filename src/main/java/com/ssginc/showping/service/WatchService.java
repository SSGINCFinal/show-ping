package com.ssginc.showping.service;

import com.ssginc.showping.dto.response.WatchResponseDto;

import java.util.List;

public interface WatchService {

    List<WatchResponseDto> getWatchHistoryByMemberNo(Long memberNo);

}
