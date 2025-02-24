package com.ssginc.showping.repository;

import com.ssginc.showping.dto.response.VodResponseDto;
import com.ssginc.showping.entity.Stream;
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
        SELECT new com.ssginc.showping.dto.response.VodResponseDto
        (s.streamNo, s.streamTitle, c.categoryNo, c.categoryName, p.productName,
        p.productPrice, p.productSale, p.productImg, s.streamStartTime, s.streamEndTime)
        FROM Stream s JOIN Product p ON s.product.productNo = p.productNo
        JOIN Category c ON p.category.categoryNo = c.categoryNo WHERE s.streamStatus = 'ENDED'
    """)
    List<VodResponseDto> findAllVod();

    /**
     * 특정 카테고리의 Vod 목록을 반환해주는 쿼리 메소드
     * @param categoryNo 카테고리 번호
     * @return vod 목록
     */
    @Query("""
        SELECT new com.ssginc.showping.dto.response.VodResponseDto
        (s.streamNo, s.streamTitle, c.categoryNo, c.categoryName, p.productName,
        p.productPrice, p.productSale, p.productImg, s.streamStartTime, s.streamEndTime)
        FROM Stream s JOIN Product p ON s.product.productNo = p.productNo
        JOIN Category c ON p.category.categoryNo = c.categoryNo WHERE s.streamStatus = 'ENDED'
        AND c.categoryNo = :categoryNo
    """)
    List<VodResponseDto> findAllVodByCategory(Long categoryNo);
}