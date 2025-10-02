package io.example.coupon.infrastructure.couponissuance.repository.jdbc;

import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceIdentity;
import io.example.coupon.model.couponissuance.CouponIssuanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ComponentScan("io.example.coupon.infrastructure.couponissuance.repository.jdbc")
@Sql({"/test-users.sql", "/test-coupons.sql"})
class CouponIssuanceJdbcRepositoryTest {

    @Autowired
    private CouponIssuanceJdbcRepository couponIssuanceRepository;

    // Test data based on CouponIssuance model spec
    private final CouponIssuance sampleIssuance = new CouponIssuance(
            null,                               // issuanceId: null (auto-generated)
            1L,                                 // couponId
            1L,                                 // userId
            Instant.now(),                      // issuedAt
            null,                               // usedAt (not used yet)
            CouponIssuanceStatus.ISSUED,        // status
            Instant.now(),                      // createdAt
            Instant.now()                       // updatedAt
    );

    private final CouponIssuanceIdentity testIdentity = new CouponIssuanceIdentity(1L);
    private final CouponIssuanceIdentity nonExistingIdentity = new CouponIssuanceIdentity(999L);

    // Entity↔Domain conversion tests (Required)

    @Test
    void save_withValidDomain_returnsConvertedDomain() {
        // When
        CouponIssuance savedIssuance = couponIssuanceRepository.save(sampleIssuance);

        // Then
        assertThat(savedIssuance).isNotNull();
        assertThat(savedIssuance.getIssuanceId()).isNotNull();
        assertThat(savedIssuance.getCouponId()).isEqualTo(sampleIssuance.getCouponId());
        assertThat(savedIssuance.getUserId()).isEqualTo(sampleIssuance.getUserId());
        assertThat(savedIssuance.getStatus()).isEqualTo(sampleIssuance.getStatus());
    }

    @Test
    void save_withNullId_generatesIdAndReturns() {
        // When
        CouponIssuance savedIssuance = couponIssuanceRepository.save(sampleIssuance);

        // Then
        assertThat(savedIssuance.getIssuanceId()).isNotNull();
        assertThat(savedIssuance.getIssuanceId()).isGreaterThan(0L);
    }

    @Test
    void findById_existingId_returnsConvertedDomain() {
        // Given
        CouponIssuance savedIssuance = couponIssuanceRepository.save(sampleIssuance);
        CouponIssuanceIdentity savedIdentity = new CouponIssuanceIdentity(savedIssuance.getIssuanceId());

        // When
        Optional<CouponIssuance> foundIssuance = couponIssuanceRepository.findById(savedIdentity);

        // Then
        assertThat(foundIssuance).isPresent();
        assertThat(foundIssuance.get().getCouponId()).isEqualTo(sampleIssuance.getCouponId());
        assertThat(foundIssuance.get().getUserId()).isEqualTo(sampleIssuance.getUserId());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // When
        Optional<CouponIssuance> foundIssuance = couponIssuanceRepository.findById(nonExistingIdentity);

        // Then
        assertThat(foundIssuance).isEmpty();
    }

    @Test
    void findAll_withData_returnsConvertedList() {
        // Given
        couponIssuanceRepository.save(sampleIssuance);
        CouponIssuance anotherIssuance = new CouponIssuance(
                null, 2L, 2L, Instant.now(), null,
                CouponIssuanceStatus.ISSUED, Instant.now(), Instant.now()
        );
        couponIssuanceRepository.save(anotherIssuance);

        // When
        List<CouponIssuance> issuances = couponIssuanceRepository.findAll();

        // Then
        assertThat(issuances).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        // When
        List<CouponIssuance> issuances = couponIssuanceRepository.findAll();

        // Then
        assertThat(issuances).isNotNull();
    }

    // Custom Named Query tests (discovered methods)

    @Test
    void findByCouponId_existingCouponId_returnsConvertedList() {
        // Given
        couponIssuanceRepository.save(sampleIssuance);
        CouponIssuance anotherIssuance = new CouponIssuance(
                null, sampleIssuance.getCouponId(), 2L, Instant.now(), null,
                CouponIssuanceStatus.ISSUED, Instant.now(), Instant.now()
        );
        couponIssuanceRepository.save(anotherIssuance);

        // When
        List<CouponIssuance> issuances = couponIssuanceRepository.findByCouponId(sampleIssuance.getCouponId());

        // Then
        assertThat(issuances).hasSizeGreaterThanOrEqualTo(2);
        assertThat(issuances).allMatch(i -> i.getCouponId().equals(sampleIssuance.getCouponId()));
    }

    @Test
    void findByCouponId_nonExistingCouponId_returnsEmptyList() {
        // When
        List<CouponIssuance> issuances = couponIssuanceRepository.findByCouponId(999L);

        // Then
        assertThat(issuances).isEmpty();
    }

    @Test
    void findByUserId_existingUserId_returnsConvertedList() {
        // Given
        couponIssuanceRepository.save(sampleIssuance);
        CouponIssuance anotherIssuance = new CouponIssuance(
                null, 2L, sampleIssuance.getUserId(), Instant.now(), null,
                CouponIssuanceStatus.ISSUED, Instant.now(), Instant.now()
        );
        couponIssuanceRepository.save(anotherIssuance);

        // When
        List<CouponIssuance> issuances = couponIssuanceRepository.findByUserId(sampleIssuance.getUserId());

        // Then
        assertThat(issuances).hasSizeGreaterThanOrEqualTo(2);
        assertThat(issuances).allMatch(i -> i.getUserId().equals(sampleIssuance.getUserId()));
    }

    @Test
    void findByUserId_nonExistingUserId_returnsEmptyList() {
        // When
        List<CouponIssuance> issuances = couponIssuanceRepository.findByUserId(999L);

        // Then
        assertThat(issuances).isEmpty();
    }

    @Test
    void findByCouponIdAndUserId_existingBoth_returnsConvertedList() {
        // Given
        couponIssuanceRepository.save(sampleIssuance);

        // When
        List<CouponIssuance> issuances = couponIssuanceRepository
                .findByCouponIdAndUserId(sampleIssuance.getCouponId(), sampleIssuance.getUserId());

        // Then
        assertThat(issuances).hasSizeGreaterThanOrEqualTo(1);
        assertThat(issuances.get(0).getCouponId()).isEqualTo(sampleIssuance.getCouponId());
        assertThat(issuances.get(0).getUserId()).isEqualTo(sampleIssuance.getUserId());
    }

    @Test
    void findByCouponIdAndUserId_nonExisting_returnsEmptyList() {
        // When
        List<CouponIssuance> issuances = couponIssuanceRepository.findByCouponIdAndUserId(999L, 999L);

        // Then
        assertThat(issuances).isEmpty();
    }

    @Test
    void deleteById_existingId_removesIssuance() {
        // Given
        CouponIssuance savedIssuance = couponIssuanceRepository.save(sampleIssuance);
        CouponIssuanceIdentity savedIdentity = new CouponIssuanceIdentity(savedIssuance.getIssuanceId());

        // When
        couponIssuanceRepository.deleteById(savedIdentity);

        // Then
        Optional<CouponIssuance> foundIssuance = couponIssuanceRepository.findById(savedIdentity);
        assertThat(foundIssuance).isEmpty();
    }
}
