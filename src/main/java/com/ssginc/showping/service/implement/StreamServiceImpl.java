package com.ssginc.showping.service.implement;

import com.ssginc.showping.dto.response.VodResponseDto;
import com.ssginc.showping.repository.StreamRepository;
import com.ssginc.showping.service.StreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 영상과 관련된 서비스 layer 클래스
 * <p>
 * 쿼리 수행 전.후로 데이터의 전처리.후처리 작업 진행
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StreamServiceImpl implements StreamService {

    private final StreamRepository streamRepository;

    /**
     * 전체 Vod 목록을 반환해주는 메소드
     * @return vod 목록
     */
    @Override
    public List<VodResponseDto> getAllVod() {
        return streamRepository.findAllVod();
    }

}