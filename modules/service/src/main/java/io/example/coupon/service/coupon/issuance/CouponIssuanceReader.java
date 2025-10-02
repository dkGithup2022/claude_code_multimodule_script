package io.example.coupon.service.coupon.issuance;

import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceIdentity;

import java.util.List;
import java.util.Optional;

/**
 * CouponIssuance 도메인 조회 서비스 인터페이스
 *
 * CQRS 패턴의 Query 책임을 담당하며,
 * Infrastructure Repository 기반으로 조회 로직을 제공합니다.
 *
 * 모니터링에만 사용 .
 */
public interface CouponIssuanceReader {

    Optional<CouponIssuance> findById(CouponIssuanceIdentity identity);

    List<CouponIssuance> findAll();

    List<CouponIssuance> findByCouponId(Long couponId);

    List<CouponIssuance> findByUserId(Long userId);
}
