package io.example.coupon.model.couponissuance;

import lombok.Value;
import java.time.Instant;

@Value
public class CouponIssuance implements CouponIssuanceModel {
    Long issuanceId;
    Long couponId;
    Long userId;
    Instant issuedAt;
    Instant usedAt;
    CouponIssuanceStatus status;
    Instant createdAt;
    Instant updatedAt;
}
