package io.example.coupon.infrastructure.couponissuance.repository;

import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceIdentity;
import java.util.List;
import java.util.Optional;

/**
 * CouponIssuance Repository 인터페이스
 *
 * 헥사고날 아키텍처에서 Port 역할을 수행하며,
 * 비즈니스 로직에서 데이터 접근을 위한 인터페이스를 정의합니다.
 */
public interface CouponIssuanceRepository {

    Optional<CouponIssuance> findById(CouponIssuanceIdentity identity);

    List<CouponIssuance> findAll();

    List<CouponIssuance> findByCouponId(Long couponId);

    List<CouponIssuance> findByUserId(Long userId);

    List<CouponIssuance> findByCouponIdAndUserId(Long couponId, Long userId);

    CouponIssuance save(CouponIssuance couponIssuance);

    void deleteById(CouponIssuanceIdentity identity);

}
