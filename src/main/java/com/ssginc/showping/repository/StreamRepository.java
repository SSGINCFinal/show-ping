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
        (s.streamNo, s.streamTitle, p.productName, p.productPrice, p.productSale, p.productImg, s.streamStartTime, s.streamEndTime)
        FROM Stream s JOIN Product p ON s.product.productNo = p.productNo WHERE s.streamStatus = 'ENDED'
    """)
    List<VodResponseDto> findAllVod();
}