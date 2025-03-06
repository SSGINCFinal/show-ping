package com.ssginc.showping.service.implement;

import com.ssginc.showping.dto.response.WatchResponseDto;
import com.ssginc.showping.repository.WatchRepository;
import com.ssginc.showping.service.WatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchServiceImpl implements WatchService {

    private final WatchRepository watchRepository;

    @Override
    public List<WatchResponseDto> getWatchHistoryByMemberNo(Long memberNo) {
        return watchRepository.getWatchListByMemberNo(memberNo);
    }

}
