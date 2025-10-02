package io.example.coupon.model.couponissuance;

import io.example.coupon.model.AuditProps;
import java.time.Instant;

public interface CouponIssuanceModel extends AuditProps {
    Long getIssuanceId();
    Long getCouponId();
    Long getUserId();
    Instant getIssuedAt();
    Instant getUsedAt();
    CouponIssuanceStatus getStatus();
}
