package io.multi.hello.infrastructure.link.repository;

import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;
import java.util.List;
import java.util.Optional;

/**
 * Link Repository 인터페이스
 *
 * 헥사고날 아키텍처에서 Port 역할을 수행하며,
 * 비즈니스 로직에서 데이터 접근을 위한 인터페이스를 정의합니다.
 */
public interface LinkRepository {

    /**
     * ID로 Link 조회
     *
     * @param identity Link 식별자
     * @return Link 엔티티 (존재하지 않으면 Optional.empty())
     */
    Optional<Link> findById(LinkIdentity identity);

    /**
     * User ID로 Link 목록 조회
     *
     * @param userId 사용자 ID
     * @return Link 목록
     */
    List<Link> findByUserId(Long userId);

    /**
     * Original URL로 Link 조회
     *
     * @param originalUrl 원본 URL
     * @return Link (존재하지 않으면 Optional.empty())
     */
    Optional<Link> findByOriginalUrl(String originalUrl);

    /**
     * Short Code로 Link 조회
     *
     * @param shortCode 짧은 코드
     * @return Link (존재하지 않으면 Optional.empty())
     */
    Optional<Link> findByShortCode(String shortCode);

    /**
     * Link 저장 (생성/수정)
     *
     * @param link 저장할 Link
     * @return 저장된 Link
     */
    Link save(Link link);

    /**
     * ID로 Link 삭제
     *
     * @param identity Link 식별자
     */
    void deleteById(LinkIdentity identity);

    /**
     * Link 존재 여부 확인
     *
     * @param identity Link 식별자
     * @return 존재하면 true, 없으면 false
     */
    boolean existsById(LinkIdentity identity);

    /**
     * Short Code 존재 여부 확인
     *
     * @param shortCode 짧은 코드
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByShortCode(String shortCode);

    /**
     * 모든 Link 조회
     *
     * @return Link 목록
     */
    List<Link> findAll();
}