package io.example.coupon.infrastructure.coupon.repository.jdbc;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponEntityRepository extends CrudRepository<CouponEntity, Long> {

    List<CouponEntity> findByName(String name);

    @Modifying
    @Query("""
    UPDATE coupons
    SET issued_count = issued_count + 1, updated_at = CURRENT_TIMESTAMP
    WHERE coupon_id = :couponId
      AND issued_count < total_quantity
      AND start_date <= CURRENT_TIMESTAMP
      AND end_date   >= CURRENT_TIMESTAMP
    """)
    int tryIncreaseIssuedCount(@Param("couponId") Long couponId);
}
