package io.multi.hello.service.link.url;

import org.springframework.stereotype.Component;

/**
 * Snowflake 알고리즘 기반 분산 ID 생성기
 *
 * 64bit 구조:
 * - 41 bits: timestamp (밀리초)
 * - 5 bits: datacenter id
 * - 5 bits: machine id
 * - 12 bits: sequence number
 *
 * 특징:
 * - 외부 통신 불필요 (완전 로컬 생성)
 * - 초당 약 400만 개 생성 가능
 * - 분산 환경에서 충돌 없음
 */
@Component
public class SnowflakeIdGenerator {

    // Epoch: 2024-01-01 00:00:00 UTC
    private static final long EPOCH = 1704067200000L;

    // 비트 시프트
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long MACHINE_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    // 최대값
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 비트 시프트 위치
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATACENTER_ID_BITS;

    private final long datacenterId;
    private final long machineId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * 기본 생성자 (단일 서버용)
     */
    public SnowflakeIdGenerator() {
        this(1L, 1L);
    }

    /**
     * 분산 환경용 생성자
     *
     * @param datacenterId 데이터센터 ID (0-31)
     * @param machineId 머신 ID (0-31)
     */
    public SnowflakeIdGenerator(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("Datacenter ID must be between 0 and %d", MAX_DATACENTER_ID));
        }
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException(
                    String.format("Machine ID must be between 0 and %d", MAX_MACHINE_ID));
        }

        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 다음 ID 생성
     *
     * @return 고유한 64bit ID
     */
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();

        // 시계가 뒤로 가는 경우 예외 처리
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // 같은 밀리초 내 요청
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence 오버플로우 → 다음 밀리초까지 대기
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 새로운 밀리초 → sequence 리셋
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 비트 연산으로 ID 조합
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    /**
     * 다음 밀리초까지 대기
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 현재 타임스탬프 (테스트 용이성을 위해 메서드로 분리)
     */
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}