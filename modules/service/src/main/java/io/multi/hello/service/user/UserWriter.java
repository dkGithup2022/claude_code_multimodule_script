package io.multi.hello.service.user;

import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;

/**
 * User 도메인 변경 서비스 인터페이스
 *
 * CQRS 패턴의 Command 책임을 담당하며,
 * Infrastructure Repository 기반으로 변경 로직을 제공합니다.
 */
public interface UserWriter {

    /**
     * User 저장 (생성/수정)
     *
     * @param user 저장할 User
     * @return 저장된 User
     */
    User upsert(User user);

    /**
     * ID로 User 삭제
     *
     * @param identity User 식별자
     */
    void deleteById(UserIdentity identity);
}