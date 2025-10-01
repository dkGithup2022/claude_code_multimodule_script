package io.multi.hello.service.link.url;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SnowflakeIdGenerator 동시성 테스트
 *
 * 100개 스레드 동시 실행 시 ID 중복 없음을 검증
 */
class SnowflakeIdGeneratorTest {

    private SnowflakeIdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        idGenerator = new SnowflakeIdGenerator();
    }

    @Test
    void generateId_with100Threads_noDuplicates() throws Exception {
        // 1. 준비
        Set<Long> ids = ConcurrentHashMap.newKeySet();
        CountDownLatch startLatch = new CountDownLatch(1);  // 시작 신호
        CountDownLatch doneLatch = new CountDownLatch(100); // 완료 대기
        ExecutorService executor = Executors.newFixedThreadPool(100);

        // 2. 스레드 100개 실행
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();  // 모두 대기
                    Long id = idGenerator.nextId();
                    ids.add(id);
                    doneLatch.countDown();  // 완료 신호
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 3. 동시 시작!
        startLatch.countDown();

        // 4. 모든 스레드 완료 대기
        doneLatch.await();
        executor.shutdown();

        // 5. 검증 - 100개 모두 다른 값
        assertThat(ids).hasSize(100);
    }

    @Test
    void generateId_sequential_allUnique() {
        // given
        Set<Long> ids = ConcurrentHashMap.newKeySet();

        // when - 1000개 순차 생성
        for (int i = 0; i < 1000; i++) {
            Long id = idGenerator.nextId();
            ids.add(id);
        }

        // then - 모두 다른 값
        assertThat(ids).hasSize(1000);
    }
}