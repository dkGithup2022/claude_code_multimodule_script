package io.multi.hello.service.link;

import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;

import java.util.List;
import java.util.Optional;

/**
 * Link 도메인 조회 서비스 인터페이스
 *
 * CQRS 패턴의 Query 책임을 담당하며,
 * Infrastructure Repository 기반으로 조회 로직을 제공합니다.
 */
public interface LinkReader {

    Link fromLongUrl(String longUrl);

    Link fromShortUrl(String shortCode);

    /**
     * 모든 링크 조회
     *
     * @return Link 목록
     */
    List<Link> findAll();

    /**
     * 사용자별 링크 조회
     *
     * @param userId 사용자 ID
     * @return Link 목록
     */
    List<Link> findByUserId(Long userId);
}