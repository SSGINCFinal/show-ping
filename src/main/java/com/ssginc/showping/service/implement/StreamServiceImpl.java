package com.ssginc.showping.service.implement;

import com.ssginc.showping.dto.response.StreamResponseDto;
import com.ssginc.showping.repository.StreamRepository;
import com.ssginc.showping.service.StreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    @Value("${download.path}")
    private String VIDEO_PATH;

    private final StreamRepository streamRepository;

    @Qualifier("webApplicationContext")
    private final ResourceLoader resourceLoader;

    /**
     * 전체 Vod 목록을 반환해주는 메소드
     * @return vod 목록
     */
    @Override
    public List<StreamResponseDto> getAllVod() {
        return streamRepository.findAllVod();
    }

    /**
     * 특정 카테고리의 Vod 목록을 반환하는 메소드
     * @param categoryNo 카테고리 번호
     * @return vod 목록
     */
    @Override
    public List<StreamResponseDto> getAllVodByCategory(Long categoryNo) {
        return streamRepository.findAllVodByCategory(categoryNo);
    }

    /**
     * 방송중인 라이브 방송 하나를 반환하는 메소드
     * @return 라이브 방송정보 1개
     */
    @Override
    public StreamResponseDto getLive() {
        List<StreamResponseDto> liveList = streamRepository.findLive();
        return liveList.isEmpty() ? null : liveList.get(0);
    }

    @Override
    public StreamResponseDto getVodByNo(Long streamNo) {
        return streamRepository.findVodByNo(streamNo);
    }

    @Override
    public Mono<Resource> getVideo(String title) {
        String filePath = VIDEO_PATH + title + ".mp4";
        return Mono.fromSupplier(() ->
                resourceLoader.getResource(String.format(filePath)));
    }

}