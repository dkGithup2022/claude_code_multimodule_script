package io.example.coupon.api.coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.example.coupon.model.coupon.Coupon;
import lombok.Getter;

import java.time.Instant;

/**
 * Coupon 응답 DTO
 */
@Getter
public class CouponResponse {

    private final Long couponId;
    private final String name;
    private final Integer discountAmount;
    private final Integer totalQuantity;
    private final Integer issuedCount;
    private final Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant updatedAt;

    public CouponResponse(Long couponId, String name, Integer discountAmount, Integer totalQuantity,
                          Integer issuedCount, Long userId, Instant startDate, Instant endDate,
                          Instant createdAt, Instant updatedAt) {
        this.couponId = couponId;
        this.name = name;
        this.discountAmount = discountAmount;
        this.totalQuantity = totalQuantity;
        this.issuedCount = issuedCount;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getCouponId(),
                coupon.getName(),
                coupon.getDiscountAmount(),
                coupon.getTotalQuantity(),
                coupon.getIssuedCount(),
                coupon.getUserId(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt()
        );
    }
}
