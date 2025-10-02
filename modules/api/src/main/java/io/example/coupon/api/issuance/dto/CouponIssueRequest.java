package io.example.coupon.api.issuance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 쿠폰 발행 요청 DTO
 */
@Getter
@Setter
public class CouponIssueRequest {

    @NotNull(message = "User ID is required")
    private Long userId;
}
