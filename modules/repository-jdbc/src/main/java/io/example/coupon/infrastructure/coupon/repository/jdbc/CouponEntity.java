package io.example.coupon.infrastructure.coupon.repository.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDateTime;

@Table("COUPONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponEntity {

    @Id
    private Long couponId;

    private String name;

    private Integer discountAmount;

    private Integer totalQuantity;

    private Integer issuedCount;

    private Long userId;

    private Instant startDate;

    private Instant endDate;

    private Instant createdAt;

    private Instant updatedAt;
}
