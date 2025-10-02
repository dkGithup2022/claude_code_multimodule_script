package io.example.coupon.api.coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Coupon 등록 요청 DTO
 */
@Getter
@Setter
public class CouponRegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Discount amount is required")
    @Positive(message = "Discount amount must be positive")
    private Integer discountAmount;

    @NotNull(message = "Total quantity is required")
    @Positive(message = "Total quantity must be positive")
    private Integer totalQuantity;

    @NotNull(message = "Issued count is required")
    @PositiveOrZero(message = "Issued count must be zero or positive")
    private Integer issuedCount;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant endDate;
}
