package io.example.coupon.service.user;

import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;

import java.util.List;
import java.util.Optional;

/**
 * User 도메인 조회 서비스 인터페이스
 * <p>
 * CQRS 패턴의 Query 책임을 담당하며,
 * Infrastructure Repository 기반으로 조회 로직을 제공합니다.
 */
public interface UserReader {

    User findById(UserIdentity identity);

    List<User> findAll();

    User findByEmail(String email);

    User findByName(String name);
}
