package io.example.coupon.infrastructure.user.repository;

import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;
import java.util.List;
import java.util.Optional;

/**
 * User Repository 인터페이스
 *
 * 헥사고날 아키텍처에서 Port 역할을 수행하며,
 * 비즈니스 로직에서 데이터 접근을 위한 인터페이스를 정의합니다.
 */
public interface UserRepository {

    Optional<User> findById(UserIdentity identity);

    List<User> findAll();

    Optional<User> findByEmail(String email);

    List<User> findByName(String name);

    User save(User user);

    void deleteById(UserIdentity identity);


}
