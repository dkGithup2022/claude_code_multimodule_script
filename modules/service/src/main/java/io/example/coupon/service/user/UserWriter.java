package io.example.coupon.service.user;

import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;

/**
 * User 도메인 변경 서비스 인터페이스
 *
 * CQRS 패턴의 Command 책임을 담당하며,
 * Infrastructure Repository 기반으로 변경 로직을 제공합니다.
 */
public interface UserWriter {

    User create(User user);

    User update(User user);


    void deleteById(UserIdentity identity);
}
