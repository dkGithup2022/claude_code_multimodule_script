package io.example.coupon.service.coupon.issuance.impl;

import io.example.coupon.infrastructure.couponissuance.repository.CouponIssuanceRepository;
import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceIdentity;
import io.example.coupon.service.coupon.issuance.CouponIssuanceReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultCouponIssuanceReader implements CouponIssuanceReader {
    private final CouponIssuanceRepository couponIssuanceRepository;

    @Override
    public Optional<CouponIssuance> findById(CouponIssuanceIdentity identity) {
        log.info("Finding coupon issuance by id: {}", identity.getIssuanceId());
        return couponIssuanceRepository.findById(identity);
    }

    @Override
    public List<CouponIssuance> findAll() {
        log.info("Finding all coupon issuances");
        return couponIssuanceRepository.findAll();
    }

    @Override
    public List<CouponIssuance> findByCouponId(Long couponId) {
        log.info("Finding coupon issuances by couponId: {}", couponId);
        return couponIssuanceRepository.findByCouponId(couponId);
    }

    @Override
    public List<CouponIssuance> findByUserId(Long userId) {
        log.info("Finding coupon issuances by userId: {}", userId);
        return couponIssuanceRepository.findByUserId(userId);
    }
}
