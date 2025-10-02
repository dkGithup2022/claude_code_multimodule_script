package io.example.coupon.exception.coupon;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException(Long couponId) {
        super("Coupon expired: " + couponId);
    }
}
