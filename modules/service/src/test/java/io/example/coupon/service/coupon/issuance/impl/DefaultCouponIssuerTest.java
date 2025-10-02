package io.example.coupon.service.coupon.issuance.impl;

import io.example.coupon.infrastructure.coupon.repository.CouponRepository;
import io.example.coupon.infrastructure.couponissuance.repository.CouponIssuanceRepository;
import io.example.coupon.infrastructure.user.repository.UserRepository;
import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.model.coupon.CouponIdentity;
import io.example.coupon.model.user.User;
import io.example.coupon.service.coupon.issuance.CouponIssuer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Slf4j
class DefaultCouponIssuerTest {

    @Autowired private UserRepository userRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private CouponIssuanceRepository couponIssuanceRepository;
    @Autowired private CouponIssuer couponIssuer;

    @Test
    @Transactional
    void race() throws Exception {
        // 1) 픽스처 insert
        int totalCouponQuantity = 1000;
        int threadCount = 1000;
        int userCount = 1000;

        var userIds = createUsers(userCount);
        var coupon = createCoupon(totalCouponQuantity);

        // 2) 커밋하고 트랜잭션 종료 (자식 스레드에서 조회 가능하도록)
        TestTransaction.flagForCommit();
        TestTransaction.end();

        // 3) 이제 동시성 테스트 시작 (새 트랜잭션/오토커밋에서 동작)
        runConcurrency(coupon.getCouponId(), userIds, totalCouponQuantity, threadCount);
    }

    // --------------------------
    // 동시성 실행부 (트랜잭션 밖)
    // --------------------------
    private void runConcurrency(Long couponId,
                                List<Long> userIds,
                                int totalCouponQuantity,
                                int threadCount) throws Exception {

        // 여기서 wait 걸고 한번에 go
        var startGate = new CountDownLatch(1);
        var doneGate  = new CountDownLatch(threadCount);

        // CPU×N 정도로 제한된 풀에 작업 1000개 제출
        int poolSize = Math.min(Runtime.getRuntime().availableProcessors() * 4, threadCount);
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);

        var successCount = new AtomicInteger(0);
        var failCount = new AtomicInteger(0);
        List<Future<?>> futures = new ArrayList<>(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            futures.add(pool.submit(() -> {
                try {
                    // 여기서 wait 걸고 한번에 go
                    startGate.await();
                    couponIssuer.issueCoupon(couponId, userIds.get(idx));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.debug("Failed userId={}, reason={}", userIds.get(idx), e.toString());
                } finally {
                    doneGate.countDown();
                }
            }));
        }

        // 동시에 출발
        startGate.countDown();

        // 타임아웃으로 영원 대기 방지
        boolean finished = doneGate.await(10, TimeUnit.SECONDS);
        pool.shutdownNow(); // 즉시 종료 시도
        assertThat(finished).as("동시 작업이 시간 내 종료되어야 함").isTrue();

        // 숨은 예외 표면화(선택)
        log.info("Success: {}, Fail: {}", successCount.get(), failCount.get());

        // ---- 검증 ----
        // 1) 성공 개수 = 총량
        assertThat(successCount.get()).isEqualTo(totalCouponQuantity);
        // 2) 성공+실패 = 총 시도 수
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
        // 3) 발급 이력 수 = 총량
        var issuances = couponIssuanceRepository.findAll();
        assertThat(issuances).hasSize(totalCouponQuantity);
        // 4) issued_count 정확성
        var updatedCoupon = couponRepository.findById(new CouponIdentity(couponId)).orElseThrow();
        assertThat(updatedCoupon.getIssuedCount()).isEqualTo(totalCouponQuantity);
    }

    // --------------------------
    // 픽스처 생성
    // --------------------------
    private Coupon createCoupon(int totalQuantity) {
        var coupon = new Coupon(
                null, "Race Test Coupon", 10000, totalQuantity, 0,
                1L, Instant.now(), Instant.now().plusSeconds(86400),
                Instant.now(), Instant.now()
        );
        return couponRepository.save(coupon);
    }

    private List<Long> createUsers(int count) {
        List<Long> userIds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            var saved = userRepository.save(
                    new io.example.coupon.model.user.User(
                            null, "test-race-user"+i+"@example.com", "Test Race User "+i,
                            Instant.now(), Instant.now()
                    )
            );
            userIds.add(saved.getUserId());
        }
        return userIds;
    }
}