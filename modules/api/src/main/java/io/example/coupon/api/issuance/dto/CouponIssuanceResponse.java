package io.example.coupon.api.issuance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceStatus;
import lombok.Getter;

import java.time.Instant;

/**
 * CouponIssuance 응답 DTO
 */
@Getter
public class CouponIssuanceResponse {

    private final Long issuanceId;
    private final Long couponId;
    private final Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant issuedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant usedAt;

    private final CouponIssuanceStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant updatedAt;

    public CouponIssuanceResponse(Long issuanceId, Long couponId, Long userId, Instant issuedAt,
                                  Instant usedAt, CouponIssuanceStatus status, Instant createdAt, Instant updatedAt) {
        this.issuanceId = issuanceId;
        this.couponId = couponId;
        this.userId = userId;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CouponIssuanceResponse from(CouponIssuance issuance) {
        return new CouponIssuanceResponse(
                issuance.getIssuanceId(),
                issuance.getCouponId(),
                issuance.getUserId(),
                issuance.getIssuedAt(),
                issuance.getUsedAt(),
                issuance.getStatus(),
                issuance.getCreatedAt(),
                issuance.getUpdatedAt()
        );
    }
}
