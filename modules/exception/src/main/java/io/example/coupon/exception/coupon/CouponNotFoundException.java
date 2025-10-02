package io.example.coupon.exception.coupon;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(Long couponId) {
        super("Coupon not found: " + couponId);
    }
}
