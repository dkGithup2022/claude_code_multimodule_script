package io.example.coupon.service.coupon.impl;

import io.example.coupon.infrastructure.coupon.repository.CouponRepository;
import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.model.user.UserIdentity;
import io.example.coupon.service.coupon.CouponRegister;
import io.example.coupon.service.coupon.dto.CouponRegisterCommand;
import io.example.coupon.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultCouponRegister implements CouponRegister {
    private final CouponRepository couponRepository;

    private final UserReader userReader;

    @Override
    public Coupon register(CouponRegisterCommand command) {
        log.info("Registering coupon: name={}, userId={}", command.name(), command.userId());

        validateSpecs(command);

        // CouponRegisterCommand -> Coupon 변환
        Coupon coupon = new Coupon(
                null,
                command.name(),
                command.discountAmount(),
                command.totalQuantity(),
                command.issuedCount(),
                command.userId(),
                command.startDate(),
                command.endDate(),
                Instant.now(),
                Instant.now()
        );

        return couponRepository.save(coupon);
    }

    private void validateSpecs(CouponRegisterCommand command) {

        if (command.totalQuantity() <= 0) {
            throw new IllegalArgumentException("Total quantity must be greater than 0");
        }
        if (command.endDate().isBefore(command.startDate())) {
            throw new IllegalArgumentException("End date must be before start date");
        }

        var user = userReader.findById(new UserIdentity(command.userId()));
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
    }
}
