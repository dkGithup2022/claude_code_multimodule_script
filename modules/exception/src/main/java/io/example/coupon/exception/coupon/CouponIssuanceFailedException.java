package io.example.coupon.exception.coupon;

public class CouponIssuanceFailedException extends RuntimeException {
    public CouponIssuanceFailedException(Long couponId) {
        super("Coupon issuance failed: " + couponId);
    }
}
