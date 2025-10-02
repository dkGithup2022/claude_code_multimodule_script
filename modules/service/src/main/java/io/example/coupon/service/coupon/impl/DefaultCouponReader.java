package io.example.coupon.service.coupon.impl;

import io.example.coupon.infrastructure.coupon.repository.CouponRepository;
import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.model.coupon.CouponIdentity;
import io.example.coupon.service.coupon.CouponReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultCouponReader implements CouponReader {
    private final CouponRepository couponRepository;

    @Override
    public Coupon findById(Long id) {
        log.info("Finding coupon by id: {}", id);
        var optional = couponRepository.findById(new CouponIdentity(id));
        return optional.orElse(null);
    }

    @Override
    public List<Coupon> findByOwnerId(Long ownerId) {
        log.info("Finding coupons by ownerId: {}", ownerId);
        // CouponRepository에 findByUserId 메서드가 필요합니다
        // 임시로 findAll을 사용하고 필터링
        return couponRepository.findAll().stream()
                .filter(coupon -> coupon.getUserId().equals(ownerId))
                .toList();
    }
}
