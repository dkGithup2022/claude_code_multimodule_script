package io.example.coupon.infrastructure.couponissuance.repository.jdbc;

import io.example.coupon.model.couponissuance.CouponIssuanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("COUPON_ISSUANCES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssuanceEntity {

    @Id
    private Long issuanceId;

    private Long couponId;

    private Long userId;

    private Instant issuedAt;

    private Instant usedAt;

    private CouponIssuanceStatus status;

    private Instant createdAt;

    private Instant updatedAt;
}
