package io.example.coupon.service.coupon.issuance;

import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.model.couponissuance.CouponIssuance;

public interface CouponIssuer {

    /**
     * 특정 사용자에게 쿠폰을 발행합니다.
     *
     * @param couponId 발행할 쿠폰 ID
     * @param userId 쿠폰을 받을 사용자 ID
     * @return 발행된 CouponIssuance
     */
    CouponIssuance issueCoupon(Long couponId, Long userId);
}
