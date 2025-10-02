package io.example.coupon.service.coupon;

import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.service.coupon.dto.CouponRegisterCommand;

public interface CouponRegister {
    Coupon register(CouponRegisterCommand command);
}
