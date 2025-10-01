package io.multi.hello.infrastructure.user.repository;

import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;
import java.util.List;
import java.util.Optional;

/**
 * User Repository 인터페이스
 *
 * 헥사고날 아키텍처에서 Port 역할을 수행하며,
 * 사용자 데이터 접근을 위한 인터페이스를 정의합니다.
 */
public interface UserRepository {

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
    List<User> findByName(String name);

    /**
     * 이메일로 User 조회
     *
     * @param email 이메일
     * @return User (존재하지 않으면 Optional.empty())
     */
    List<User> findByEmail(String email);

    /**
     * User 저장 (생성/수정)
     *
     * @param user 저장할 User
     * @return 저장된 User
     */
    User save(User user);

    /**
     * ID로 User 삭제
     *
     * @param identity User 식별자
     */
    void deleteById(UserIdentity identity);

    /**
     * User 존재 여부 확인
     *
     * @param identity User 식별자
     * @return 존재하면 true, 없으면 false
     */
    boolean existsById(UserIdentity identity);
}