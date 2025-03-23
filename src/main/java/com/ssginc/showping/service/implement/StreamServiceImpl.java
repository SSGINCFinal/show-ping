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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * 페이징 정보가 포함된 Vod 목록을 반환해주는 메소드
     * @param pageable 페이징 정보 객체
     * @return 페이징 정보가 있는 vod 목록
     */
    @Override
    public Page<StreamResponseDto> getAllVodByPage(Pageable pageable) {
        return streamRepository.findAllVodByPage(pageable);
    }

    /**
     * 특정 카테고리의 vod 목록을 반환하는 메소드
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

    /**
     * 영상번호로 vod 정보를 반환하는 메소드
     * @param streamNo 영상번호
     * @return vod 정보 객체
     */
    @Override
    public StreamResponseDto getVodByNo(Long streamNo) {
        return streamRepository.findVodByNo(streamNo);
    }

    /**
     * 영상 제목으로 vod 파일을 받아오는 메소드
     * @param title 영상 제목
     * @return vod 파일
     */
    @Override
    public Mono<Resource> getVideo(String title) {
        // 가져올 VOD 전체 경로저장
        String filePath = VIDEO_PATH + title + ".mp4";

        // 지정된 경로의 VOD 파일을 가져오기
        return Mono.fromSupplier(() ->
                resourceLoader.getResource(String.format(filePath)));
    }

}