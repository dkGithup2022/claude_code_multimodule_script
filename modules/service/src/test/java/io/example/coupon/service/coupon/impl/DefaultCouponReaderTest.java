package io.example.coupon.service.coupon.impl;

import io.example.coupon.infrastructure.coupon.repository.CouponRepository;
import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.model.coupon.CouponIdentity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCouponReaderTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private DefaultCouponReader couponReader;

    // Test data based on Coupon model spec
    private final Coupon sampleCoupon = new Coupon(
            1L,                             // couponId
            "testCoupon",                   // name
            1000,                           // discountAmount
            100,                            // totalQuantity
            0,                              // issuedCount
            1L,                             // userId
            Instant.now(),                  // startDate
            Instant.now().plus(30, ChronoUnit.DAYS),  // endDate
            Instant.now(),                  // createdAt
            Instant.now()                   // updatedAt
    );

    private final CouponIdentity testIdentity = new CouponIdentity(1L);

    // findById tests

    @Test
    void findById_existingId_returnsCoupon() {
        // given
        when(couponRepository.findById(testIdentity))
                .thenReturn(Optional.of(sampleCoupon));

        // when
        Coupon result = couponReader.findById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(sampleCoupon.getName());
        assertThat(result.getDiscountAmount()).isEqualTo(sampleCoupon.getDiscountAmount());
        verify(couponRepository).findById(testIdentity);
    }

    @Test
    void findById_nonExistingId_returnsNull() {
        // given
        when(couponRepository.findById(testIdentity))
                .thenReturn(Optional.empty());

        // when
        Coupon result = couponReader.findById(1L);

        // then
        assertThat(result).isNull();
        verify(couponRepository).findById(testIdentity);
    }

    // findByOwnerId tests

    @Test
    void findByOwnerId_existingOwnerId_returnsCouponsList() {
        // given
        Coupon anotherCoupon = new Coupon(
                2L, "anotherCoupon", 2000, 50, 0, 1L,
                Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS),
                Instant.now(), Instant.now()
        );
        when(couponRepository.findAll())
                .thenReturn(List.of(sampleCoupon, anotherCoupon));

        // when
        List<Coupon> result = couponReader.findByOwnerId(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(coupon -> coupon.getUserId().equals(1L));
        verify(couponRepository).findAll();
    }

    @Test
    void findByOwnerId_nonExistingOwnerId_returnsEmptyList() {
        // given
        when(couponRepository.findAll())
                .thenReturn(List.of(sampleCoupon));

        // when
        List<Coupon> result = couponReader.findByOwnerId(999L);

        // then
        assertThat(result).isEmpty();
        verify(couponRepository).findAll();
    }

    @Test
    void findByOwnerId_filtersCorrectly() {
        // given
        Coupon coupon1 = new Coupon(
                1L, "coupon1", 1000, 100, 0, 1L,
                Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS),
                Instant.now(), Instant.now()
        );
        Coupon coupon2 = new Coupon(
                2L, "coupon2", 2000, 50, 0, 2L,
                Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS),
                Instant.now(), Instant.now()
        );
        Coupon coupon3 = new Coupon(
                3L, "coupon3", 1500, 75, 0, 1L,
                Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS),
                Instant.now(), Instant.now()
        );
        when(couponRepository.findAll())
                .thenReturn(List.of(coupon1, coupon2, coupon3));

        // when
        List<Coupon> result = couponReader.findByOwnerId(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Coupon::getCouponId).containsExactlyInAnyOrder(1L, 3L);
        assertThat(result).allMatch(coupon -> coupon.getUserId().equals(1L));
        verify(couponRepository).findAll();
    }
}
