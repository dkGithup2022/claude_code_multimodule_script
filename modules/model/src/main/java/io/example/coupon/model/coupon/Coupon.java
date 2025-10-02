package io.example.coupon.model.coupon;

import lombok.Value;

import java.time.Instant;
import java.time.LocalDateTime;

@Value
public class Coupon implements CouponModel {
    Long couponId;
    String name;
    Integer discountAmount;
    Integer totalQuantity;
    Integer issuedCount;
    Long userId;
    Instant startDate;
    Instant endDate;
    Instant createdAt;
    Instant updatedAt;


}
