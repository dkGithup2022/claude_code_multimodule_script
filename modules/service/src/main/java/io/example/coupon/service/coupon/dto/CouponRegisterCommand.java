package io.example.coupon.service.coupon.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record CouponRegisterCommand(
        String name,
        Integer discountAmount,
        Integer totalQuantity,
        Integer issuedCount,
        Long userId,
        Instant startDate,
        Instant endDate
) {
}
