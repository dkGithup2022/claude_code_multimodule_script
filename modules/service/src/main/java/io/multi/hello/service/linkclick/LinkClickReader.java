package io.multi.hello.service.linkclick;

import io.multi.hello.model.linkclick.LinkClick;
import io.multi.hello.model.linkclick.LinkClickIdentity;

import java.util.List;
import java.util.Optional;

/**
 * LinkClick 도메인 조회 서비스 인터페이스
 *
 * CQRS 패턴의 Query 책임을 담당하며,
 * Infrastructure Repository 기반으로 조회 로직을 제공합니다.
 */
public interface LinkClickReader {

    /**
     * ID로 조회
     *
     * @param identity LinkClick 식별자
     * @return LinkClick (존재하지 않으면 Optional.empty())
     */
    Optional<LinkClick> findById(LinkClickIdentity identity);

    /**
     * 링크별 조회
     *
     * @param linkId Link ID
     * @return LinkClick 목록
     */
    List<LinkClick> findByLinkId(Long linkId);

    /**
     * 클릭 수
     *
     * @param linkId Link ID
     * @return 클릭 수
     */
    long countByLinkId(Long linkId);
}