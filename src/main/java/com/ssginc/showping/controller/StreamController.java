package com.ssginc.showping.controller;

import com.ssginc.showping.dto.response.StreamResponseDto;
import com.ssginc.showping.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 영상과 관련된 요청-응답을 수행하는 컨트롤러 클래스
 * <p>
 * 각 요청에 따라 method 정의
 */
@Controller
@RequestMapping("stream")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    /**
     * 라이브 메인 페이지 요청 컨틀롤러 메소드
     * @return 라이브 메인 페이지 (타임리프)
     */
    @GetMapping("/list")
    public String streamList() {
        return "stream/list";
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메소드
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> getLive() {
        StreamResponseDto live = streamService.getLive();
        Map<String, Object> result = new HashMap<>();

        result.put("live", live);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메소드
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/list")
    public ResponseEntity<Map<String, Object>> getVodList() {
        List<StreamResponseDto> vodList = streamService.getAllVod();
        Map<String, Object> result = new HashMap<>();

        result.put("vodList", vodList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메소드
     * @param pageNo 요청한 페이지 번호
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/list/page")
    public ResponseEntity<Map<String, Object>> getVodListByPage(@RequestParam(defaultValue = "0", name = "pageNo") int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 4);
        Page<StreamResponseDto> pageInfo = streamService.getAllVodByPage(pageable);
        Map<String, Object> result = new HashMap<>();

        result.put("pageInfo", pageInfo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메소드
     * @param categoryNo 카테고리 번호
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/list/{categoryNo}")
    public ResponseEntity<Map<String, Object>> getVodListByCategory(@PathVariable Long categoryNo) {
        List<StreamResponseDto> vodList = streamService.getAllVodByCategory(categoryNo);
        Map<String, Object> result = new HashMap<>();

        result.put("vodList", vodList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * VOD 파일을 반환해주는 컨트롤러 메소드
     * @param title 요청한 VOD 제목
     * @param range 요청한 데이터 범위 (default: 전체를 받아옴)
     * @return VOD 파일
     */
    @GetMapping(value = "/vod/fetch/{title}", produces = "video/mp4")
    public Mono<ResponseEntity<Resource>> fetchVod(@PathVariable String title,
                                                   @RequestHeader(value = "Range", required = false) String range) {

        return streamService.getVideo(title)
                .map(resource -> {
                    long contentLength;
                    try {
                        contentLength = resource.contentLength();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    if (range == null || range.isEmpty()) {
                        return ResponseEntity.ok()
                                .contentType(MediaTypeFactory.getMediaType(resource)
                                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                                .body(resource);
                    }

                    String[] ranges = range.replace("bytes=", "").split("-");
                    long start = Long.parseLong(ranges[0]);
                    long end = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : contentLength - 1;

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Range", "bytes " + start + "-" + end + "/" + contentLength);
                    headers.setContentLength(end - start + 1);
                    headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");

                    InputStreamResource partialResource;
                    try {
                        partialResource = new InputStreamResource(resource.getInputStream()) {
                            @Override
                            public long contentLength() {
                                return end - start + 1;
                            }
                        };
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                            .headers(headers)
                            .contentType(MediaTypeFactory.getMediaType(resource)
                                    .orElse(MediaType.APPLICATION_OCTET_STREAM))
                            .body(partialResource);
                });
    }
}
