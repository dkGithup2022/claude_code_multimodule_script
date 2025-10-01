package io.multi.hello.service.user;

import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;

import java.util.List;
import java.util.Optional;

/**
 * User 도메인 조회 서비스 인터페이스
 * <p>
 * CQRS 패턴의 Query 책임을 담당하며,
 * Infrastructure Repository 기반으로 조회 로직을 제공합니다.
 */
public interface UserReader {

    /**
     * ID로 User 조회
     *
     * @param identity User 식별자
     * @return User 엔티티 (존재하지 않으면 Optional.empty())
     */
    Optional<User> findById(UserIdentity identity);

    /**
     * 이름으로 User 목록 조회
     *
     * @param name 이름
     * @return User 목록
     */
    User findByName(String name);

    /**
     * 이메일로 User 조회
     *
     * @param email 이메일
     * @return User (존재하지 않으면 Optional.empty())
     */
    User findByEmail(String email);

    /**
     * User 존재 여부 확인
     *
     * @param identity User 식별자
     * @return 존재하면 true, 없으면 false
     */
    boolean existsById(UserIdentity identity);
}