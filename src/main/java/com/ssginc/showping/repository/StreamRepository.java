package com.ssginc.showping.repository;

import com.ssginc.showping.dto.response.StreamResponseDto;
import com.ssginc.showping.entity.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 영상데이터 DB 쿼리를 통해 가져오는 인터페이스
 * <p>
 */
@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {

    /**
     * 전체 Vod 목록을 반환해주는 쿼리 메소드
     * @return vod 목록
     */
    @Query("""
        SELECT new com.ssginc.showping.dto.response.StreamResponseDto
        (s.streamNo, s.streamTitle, c.categoryNo, c.categoryName, p.productName,
        p.productPrice, p.productSale, p.productImg, s.streamStartTime, s.streamEndTime)
        FROM Stream s JOIN Product p ON s.product.productNo = p.productNo
        JOIN Category c ON p.category.categoryNo = c.categoryNo WHERE s.streamStatus = 'ENDED'
    """)
    List<StreamResponseDto> findAllVod();

    /**
     * VOD 목록과 페이지 정보를 반환해주는 쿼리 메소드
     * @param pageable 페이징 정보 객체
     * @return 페이징 정보가 포함된 VOD 목록
     */
    @Query("""
        SELECT new com.ssginc.showping.dto.response.StreamResponseDto
        (s.streamNo, s.streamTitle, c.categoryNo, c.categoryName, p.productName,
        p.productPrice, p.productSale, p.productImg, s.streamStartTime, s.streamEndTime)
        FROM Stream s JOIN Product p ON s.product.productNo = p.productNo
        JOIN Category c ON p.category.categoryNo = c.categoryNo WHERE s.streamStatus = 'ENDED'
    """)
    Page<StreamResponseDto> findAllVodByPage(Pageable pageable);

    /**
     * 특정 카테고리의 Vod 목록을 반환해주는 쿼리 메소드
     * @param categoryNo 카테고리 번호
     * @return vod 목록
     */
    @Query("""
        SELECT new com.ssginc.showping.dto.response.StreamResponseDto
        (s.streamNo, s.streamTitle, c.categoryNo, c.categoryName, p.productName,
        p.productPrice, p.productSale, p.productImg, s.streamStartTime, s.streamEndTime)
        FROM Stream s JOIN Product p ON s.product.productNo = p.productNo
        JOIN Category c ON p.category.categoryNo = c.categoryNo WHERE s.streamStatus = 'ENDED'
        AND c.categoryNo = :categoryNo
    """)
    List<StreamResponseDto> findAllVodByCategory(Long categoryNo);

    /**
     * 진행중인 라이브 방송을 반환해주는 쿼리 메소드
     * @return 라이브 방송 정보 리스트
     */
    @Query("""
        SELECT new com.ssginc.showping.dto.response.StreamResponseDto
        (s.streamNo, s.streamTitle, c.categoryNo, c.categoryName, p.productName,
        p.productPrice, p.productSale, p.productImg, s.streamStartTime, s.streamEndTime)
        FROM Stream s JOIN Product p ON s.product.productNo = p.productNo
        JOIN Category c ON p.category.categoryNo = c.categoryNo WHERE s.streamStatus = 'ONAIR'
    """)
    List<StreamResponseDto> findLive();

    /**
     * 특정 영상번호의 Vod 정보를 반환해주는 쿼리 메소드
     * @param streamNo 영상 번호
     * @return vod 목록
     */
    @Query("""
        SELECT new com.ssginc.showping.dto.response.StreamResponseDto
        (s.streamNo, s.streamTitle, c.categoryNo, c.categoryName, p.productName,
        p.productPrice, p.productSale, p.productImg, s.streamStartTime, s.streamEndTime)
        FROM Stream s JOIN Product p ON s.product.productNo = p.productNo
        JOIN Category c ON p.category.categoryNo = c.categoryNo WHERE s.streamNo = :streamNo
    """)
    StreamResponseDto findVodByNo(Long streamNo);

}