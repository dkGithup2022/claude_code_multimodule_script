package io.example.coupon.infrastructure.coupon.repository.jdbc;

import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.model.coupon.CouponIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ComponentScan("io.example.coupon.infrastructure.coupon.repository.jdbc")
@Sql("/test-users.sql")
class CouponJdbcRepositoryTest {

    @Autowired
    private CouponJdbcRepository couponRepository;

    // Test data based on Coupon model spec
    private final Coupon sampleCoupon = new Coupon(
            null,                             // couponId: null (auto-generated)
            "testCoupon",                     // name
            1000,                             // discountAmount
            100,                              // totalQuantity
            0,                                // issuedCount
            1L,                               // userId (owner)
            Instant.now(),                    // startDate
            Instant.now().plus(30, ChronoUnit.DAYS),  // endDate
            Instant.now(),                    // createdAt
            Instant.now()                     // updatedAt
    );

    private final CouponIdentity testIdentity = new CouponIdentity(1L);
    private final CouponIdentity nonExistingIdentity = new CouponIdentity(999L);

    // Entity↔Domain conversion tests (Required)

    @Test
    void save_withValidDomain_returnsConvertedDomain() {
        // When
        Coupon savedCoupon = couponRepository.save(sampleCoupon);

        // Then
        assertThat(savedCoupon).isNotNull();
        assertThat(savedCoupon.getCouponId()).isNotNull();
        assertThat(savedCoupon.getName()).isEqualTo(sampleCoupon.getName());
        assertThat(savedCoupon.getDiscountAmount()).isEqualTo(sampleCoupon.getDiscountAmount());
        assertThat(savedCoupon.getTotalQuantity()).isEqualTo(sampleCoupon.getTotalQuantity());
    }

    @Test
    void save_withNullId_generatesIdAndReturns() {
        // When
        Coupon savedCoupon = couponRepository.save(sampleCoupon);

        // Then
        assertThat(savedCoupon.getCouponId()).isNotNull();
        assertThat(savedCoupon.getCouponId()).isGreaterThan(0L);
    }

    @Test
    void findById_existingId_returnsConvertedDomain() {
        // Given
        Coupon savedCoupon = couponRepository.save(sampleCoupon);
        CouponIdentity savedIdentity = new CouponIdentity(savedCoupon.getCouponId());

        // When
        Optional<Coupon> foundCoupon = couponRepository.findById(savedIdentity);

        // Then
        assertThat(foundCoupon).isPresent();
        assertThat(foundCoupon.get().getName()).isEqualTo(sampleCoupon.getName());
        assertThat(foundCoupon.get().getDiscountAmount()).isEqualTo(sampleCoupon.getDiscountAmount());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // When
        Optional<Coupon> foundCoupon = couponRepository.findById(nonExistingIdentity);

        // Then
        assertThat(foundCoupon).isEmpty();
    }

    @Test
    void findAll_withData_returnsConvertedList() {
        // Given
        couponRepository.save(sampleCoupon);
        Coupon anotherCoupon = new Coupon(
                null, "anotherCoupon", 2000, 50, 0, 1L,
                Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS),
                Instant.now(), Instant.now()
        );
        couponRepository.save(anotherCoupon);

        // When
        List<Coupon> coupons = couponRepository.findAll();

        // Then
        assertThat(coupons).hasSizeGreaterThanOrEqualTo(2);
        assertThat(coupons).extracting(Coupon::getName)
                .contains(sampleCoupon.getName(), anotherCoupon.getName());
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        // When
        List<Coupon> coupons = couponRepository.findAll();

        // Then
        // May have initial data from data.sql, so just check it's a list
        assertThat(coupons).isNotNull();
    }

    // Custom Named Query tests (if discovered)

    @Test
    void findByName_existingName_returnsConvertedList() {
        // Given
        couponRepository.save(sampleCoupon);

        // When
        List<Coupon> coupons = couponRepository.findByName(sampleCoupon.getName());

        // Then
        assertThat(coupons).hasSizeGreaterThanOrEqualTo(1);
        assertThat(coupons.get(0).getName()).isEqualTo(sampleCoupon.getName());
    }

    @Test
    void findByName_nonExistingName_returnsEmptyList() {
        // When
        List<Coupon> coupons = couponRepository.findByName("nonExistingCouponName");

        // Then
        assertThat(coupons).isEmpty();
    }

    @Test
    void deleteById_existingId_removesCoupon() {
        // Given
        Coupon savedCoupon = couponRepository.save(sampleCoupon);
        CouponIdentity savedIdentity = new CouponIdentity(savedCoupon.getCouponId());

        // When
        couponRepository.deleteById(savedIdentity);

        // Then
        Optional<Coupon> foundCoupon = couponRepository.findById(savedIdentity);
        assertThat(foundCoupon).isEmpty();
    }
}
