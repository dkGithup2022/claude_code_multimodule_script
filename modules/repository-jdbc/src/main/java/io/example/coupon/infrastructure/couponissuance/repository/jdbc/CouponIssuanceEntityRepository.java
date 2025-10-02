package io.example.coupon.infrastructure.couponissuance.repository.jdbc;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CouponIssuanceEntityRepository extends CrudRepository<CouponIssuanceEntity, Long> {

    List<CouponIssuanceEntity> findByCouponId(Long couponId);

    List<CouponIssuanceEntity> findByUserId(Long userId);

    List<CouponIssuanceEntity> findByCouponIdAndUserId(Long couponId, Long userId);
}
