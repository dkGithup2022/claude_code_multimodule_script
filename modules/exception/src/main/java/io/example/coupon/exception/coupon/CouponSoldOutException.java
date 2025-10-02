package io.example.coupon.exception.coupon;

public class CouponSoldOutException extends RuntimeException {
    public CouponSoldOutException(Long couponId) {
        super("Coupon sold out: " + couponId);
    }
}
