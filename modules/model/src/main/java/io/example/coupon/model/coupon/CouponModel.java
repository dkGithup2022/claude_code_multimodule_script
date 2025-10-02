package io.example.coupon.model.coupon;

import io.example.coupon.model.AuditProps;
import java.time.Instant;

public interface CouponModel extends AuditProps {
    Long getCouponId();
    String getName();
    Integer getDiscountAmount();
    Integer getTotalQuantity();
    Integer getIssuedCount();
    Long getUserId();
    Instant getStartDate();
    Instant getEndDate();
}
