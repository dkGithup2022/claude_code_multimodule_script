package io.example.coupon.infrastructure.coupon.repository;

import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.model.coupon.CouponIdentity;
import java.util.List;
import java.util.Optional;

/**
 * Coupon Repository 인터페이스
 *
 * 헥사고날 아키텍처에서 Port 역할을 수행하며,
 * 비즈니스 로직에서 데이터 접근을 위한 인터페이스를 정의합니다.
 */
public interface CouponRepository {

    Optional<Coupon> findById(CouponIdentity identity);

    List<Coupon> findAll();

    List<Coupon> findByName(String name);

    Coupon save(Coupon coupon);

    void deleteById(CouponIdentity identity);

    // affected row is exited
    boolean tryIncreaseIssuedCount(long couponId);

}
