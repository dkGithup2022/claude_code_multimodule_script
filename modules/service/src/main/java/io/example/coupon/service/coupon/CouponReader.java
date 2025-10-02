package io.example.coupon.service.coupon;

import io.example.coupon.model.coupon.Coupon;

import java.util.List;

public interface CouponReader {

    Coupon findById(Long id);

    List<Coupon> findByOwnerId(Long ownerId);

}
