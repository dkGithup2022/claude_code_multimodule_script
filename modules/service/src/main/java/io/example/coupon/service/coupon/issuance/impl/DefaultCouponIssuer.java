package io.example.coupon.service.coupon.issuance.impl;

import io.example.coupon.exception.coupon.*;
import io.example.coupon.infrastructure.coupon.repository.CouponRepository;
import io.example.coupon.infrastructure.couponissuance.repository.CouponIssuanceRepository;
import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceStatus;
import io.example.coupon.model.user.UserIdentity;
import io.example.coupon.service.coupon.CouponReader;
import io.example.coupon.service.coupon.issuance.CouponIssuer;
import io.example.coupon.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultCouponIssuer implements CouponIssuer {
    private final CouponReader couponReader;
    private final UserReader userReader;
    private final CouponRepository couponRepository;

    private final CouponIssuanceRepository couponIssuanceRepository;

    @Override
    @Transactional
    public CouponIssuance issueCoupon(Long couponId, Long userId) {
        log.info("Issuing coupon: couponId={}, userId={}", couponId, userId);

        validateUser(userId);

        // 1. 중복 발행 체크
        checkDuplicateIssuance(couponId, userId);

        // 2. 쿠폰 증가 시도 (DB 레벨에서 동시성 제어)
        var affected = couponRepository.tryIncreaseIssuedCount(couponId);
        if(!affected) {
            // 구체적인 실패 원인 파악
            var coupon = couponReader.findById(couponId);
            if (coupon == null) {
                throw new CouponNotFoundException(couponId);
            }
            if (coupon.getIssuedCount() >= coupon.getTotalQuantity()) {
                throw new CouponSoldOutException(couponId);
            }
            if (Instant.now().isAfter(coupon.getEndDate()) ||
                Instant.now().isBefore(coupon.getStartDate())) {
                throw new CouponExpiredException(couponId);
            }
            throw new CouponIssuanceFailedException(couponId);
        }

        // 3. 발행 기록 생성
        var coupon = couponReader.findById(couponId);
        var couponIssuance = newCouponIssuance(coupon, userId);

        return couponIssuanceRepository.save(couponIssuance);
    }

    private void checkDuplicateIssuance(Long couponId, Long userId) {
        var existing = couponIssuanceRepository.findByCouponIdAndUserId(couponId, userId);
        if (!existing.isEmpty()) {
            throw new DuplicateCouponIssuanceException(couponId, userId);
        }
    }


    private CouponIssuance newCouponIssuance(Coupon coupon, Long userId) {

        return new CouponIssuance(
                null,
                coupon.getCouponId(),
                userId,
                Instant.now(),
                null,
                CouponIssuanceStatus.ISSUED,
                Instant.now(),
                Instant.now()
        );

    }

    private void validateUser(Long userId) {
        var user = userReader.findById(new UserIdentity(userId));
        if (user == null) {
            throw new RuntimeException("User with id " + userId + " not found");
        }
    }


}
