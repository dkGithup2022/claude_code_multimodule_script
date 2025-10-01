package io.multi.hello.infrastructure.linkclick.repository;

import io.multi.hello.model.linkclick.LinkClick;
import io.multi.hello.model.linkclick.LinkClickIdentity;
import java.util.List;
import java.util.Optional;

/**
 * LinkClick Repository 인터페이스
 *
 * 헥사고날 아키텍처에서 Port 역할을 수행하며,
 * 클릭 기록 데이터 접근을 위한 인터페이스를 정의합니다.
 */
public interface LinkClickRepository {

    /**
     * ID로 LinkClick 조회
     *
     * @param identity LinkClick 식별자
     * @return LinkClick 엔티티 (존재하지 않으면 Optional.empty())
     */
    Optional<LinkClick> findById(LinkClickIdentity identity);

    /**
     * Link ID로 LinkClick 목록 조회
     *
     * @param linkId Link ID
     * @return LinkClick 목록
     */
    List<LinkClick> findByLinkId(Long linkId);

    /**
     * Link ID로 클릭 수 조회
     *
     * @param linkId Link ID
     * @return 클릭 수
     */
    long countByLinkId(Long linkId);

    /**
     * LinkClick 저장
     *
     * @param linkClick 저장할 LinkClick
     * @return 저장된 LinkClick
     */
    LinkClick save(LinkClick linkClick);
}