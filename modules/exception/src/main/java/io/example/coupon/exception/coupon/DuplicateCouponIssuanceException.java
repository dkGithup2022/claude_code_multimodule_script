package io.example.coupon.exception.coupon;

public class DuplicateCouponIssuanceException extends RuntimeException {
    public DuplicateCouponIssuanceException(Long couponId, Long userId) {
        super("Coupon already issued to user. couponId: " + couponId + ", userId: " + userId);
    }
}
