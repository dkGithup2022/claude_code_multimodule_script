package io.example.coupon.service.coupon.issuance.impl;

import io.example.coupon.infrastructure.couponissuance.repository.CouponIssuanceRepository;
import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceIdentity;
import io.example.coupon.model.couponissuance.CouponIssuanceStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCouponIssuanceReaderTest {

    @Mock
    private CouponIssuanceRepository couponIssuanceRepository;

    @InjectMocks
    private DefaultCouponIssuanceReader couponIssuanceReader;

    // Test data based on CouponIssuance model spec
    private final CouponIssuance sampleIssuance = new CouponIssuance(
            1L,                               // issuanceId
            1L,                               // couponId
            1L,                               // userId
            Instant.now(),                    // issuedAt
            null,                             // usedAt (not used yet)
            CouponIssuanceStatus.ISSUED,      // status
            Instant.now(),                    // createdAt
            Instant.now()                     // updatedAt
    );

    private final CouponIssuanceIdentity testIdentity = new CouponIssuanceIdentity(1L);

    // findById tests

    @Test
    void findById_existingId_returnsOptionalWithIssuance() {
        // given
        when(couponIssuanceRepository.findById(testIdentity))
                .thenReturn(Optional.of(sampleIssuance));

        // when
        Optional<CouponIssuance> result = couponIssuanceReader.findById(testIdentity);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getCouponId()).isEqualTo(sampleIssuance.getCouponId());
        assertThat(result.get().getUserId()).isEqualTo(sampleIssuance.getUserId());
        assertThat(result.get().getStatus()).isEqualTo(CouponIssuanceStatus.ISSUED);
        verify(couponIssuanceRepository).findById(testIdentity);
    }

    @Test
    void findById_nonExistingId_returnsEmptyOptional() {
        // given
        when(couponIssuanceRepository.findById(testIdentity))
                .thenReturn(Optional.empty());

        // when
        Optional<CouponIssuance> result = couponIssuanceReader.findById(testIdentity);

        // then
        assertThat(result).isEmpty();
        verify(couponIssuanceRepository).findById(testIdentity);
    }

    // findAll tests

    @Test
    void findAll_withData_returnsList() {
        // given
        CouponIssuance anotherIssuance = new CouponIssuance(
                2L, 2L, 2L, Instant.now(), null,
                CouponIssuanceStatus.ISSUED, Instant.now(), Instant.now()
        );
        when(couponIssuanceRepository.findAll())
                .thenReturn(List.of(sampleIssuance, anotherIssuance));

        // when
        List<CouponIssuance> result = couponIssuanceReader.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(sampleIssuance, anotherIssuance);
        verify(couponIssuanceRepository).findAll();
    }

    @Test
    void findAll_emptyData_returnsEmptyList() {
        // given
        when(couponIssuanceRepository.findAll())
                .thenReturn(List.of());

        // when
        List<CouponIssuance> result = couponIssuanceReader.findAll();

        // then
        assertThat(result).isEmpty();
        verify(couponIssuanceRepository).findAll();
    }

    // findByCouponId tests

    @Test
    void findByCouponId_existingCouponId_returnsList() {
        // given
        CouponIssuance anotherIssuance = new CouponIssuance(
                2L, 1L, 2L, Instant.now(), null,
                CouponIssuanceStatus.ISSUED, Instant.now(), Instant.now()
        );
        when(couponIssuanceRepository.findByCouponId(1L))
                .thenReturn(List.of(sampleIssuance, anotherIssuance));

        // when
        List<CouponIssuance> result = couponIssuanceReader.findByCouponId(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(issuance -> issuance.getCouponId().equals(1L));
        verify(couponIssuanceRepository).findByCouponId(1L);
    }

    @Test
    void findByCouponId_nonExistingCouponId_returnsEmptyList() {
        // given
        when(couponIssuanceRepository.findByCouponId(999L))
                .thenReturn(List.of());

        // when
        List<CouponIssuance> result = couponIssuanceReader.findByCouponId(999L);

        // then
        assertThat(result).isEmpty();
        verify(couponIssuanceRepository).findByCouponId(999L);
    }

    // findByUserId tests

    @Test
    void findByUserId_existingUserId_returnsList() {
        // given
        CouponIssuance anotherIssuance = new CouponIssuance(
                2L, 2L, 1L, Instant.now(), null,
                CouponIssuanceStatus.ISSUED, Instant.now(), Instant.now()
        );
        when(couponIssuanceRepository.findByUserId(1L))
                .thenReturn(List.of(sampleIssuance, anotherIssuance));

        // when
        List<CouponIssuance> result = couponIssuanceReader.findByUserId(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(issuance -> issuance.getUserId().equals(1L));
        verify(couponIssuanceRepository).findByUserId(1L);
    }

    @Test
    void findByUserId_nonExistingUserId_returnsEmptyList() {
        // given
        when(couponIssuanceRepository.findByUserId(999L))
                .thenReturn(List.of());

        // when
        List<CouponIssuance> result = couponIssuanceReader.findByUserId(999L);

        // then
        assertThat(result).isEmpty();
        verify(couponIssuanceRepository).findByUserId(999L);
    }

    @Test
    void findByUserId_returnsIssuancesWithDifferentStatuses() {
        // given
        CouponIssuance usedIssuance = new CouponIssuance(
                2L, 2L, 1L, Instant.now(), Instant.now(),
                CouponIssuanceStatus.USED, Instant.now(), Instant.now()
        );
        when(couponIssuanceRepository.findByUserId(1L))
                .thenReturn(List.of(sampleIssuance, usedIssuance));

        // when
        List<CouponIssuance> result = couponIssuanceReader.findByUserId(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CouponIssuance::getStatus)
                .containsExactlyInAnyOrder(CouponIssuanceStatus.ISSUED, CouponIssuanceStatus.USED);
        verify(couponIssuanceRepository).findByUserId(1L);
    }
}
