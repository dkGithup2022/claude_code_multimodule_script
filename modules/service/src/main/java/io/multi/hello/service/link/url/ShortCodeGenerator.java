package io.multi.hello.service.link.url;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Short Code 생성기
 *
 * Snowflake ID를 Base62로 인코딩하여 짧은 코드를 생성합니다.
 *
 * 생성 과정:
 * 1. Snowflake ID 생성 (64bit, 외부 통신 없음)
 * 2. Base62 인코딩 (약 11자)
 * 3. 선택적 Salt 적용 (예측 방지)
 *
 * 특징:
 * - 충돌 없음 (Snowflake ID가 unique 보장)
 * - 빠름 (DB 조회 0번)
 * - 예측 불가능 (Salt 적용 시)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ShortCodeGenerator {

    private final SnowflakeIdGenerator idGenerator;

    // Salt 값 (예측 방지용)
    private static final long SALT = 987654321L;
    private static final long PRIME = 1000003L;

    /**
     * Short Code 생성 (기본)
     *
     * @return 생성된 short code
     */
    public String generate() {
        long id = idGenerator.nextId();
        String shortCode = Base62Encoder.encode(id);
        log.debug("Generated short code: {} from id: {}", shortCode, id);
        return shortCode;
    }

    /**
     * Short Code 생성 (Salt 적용)
     *
     * ID에 Salt를 적용하여 예측 불가능하게 만듭니다.
     *
     * @return 생성된 short code
     */
    public String generateWithSalt() {
        long id = idGenerator.nextId();
        long saltedId = (id * PRIME + SALT) & Long.MAX_VALUE; // 양수 보장
        String shortCode = Base62Encoder.encode(saltedId);
        log.debug("Generated short code with salt: {} from id: {} (salted: {})",
                shortCode, id, saltedId);
        return shortCode;
    }

    /**
     * Short Code 생성 (최소 길이 보장)
     *
     * @param minLength 최소 길이
     * @return 생성된 short code (최소 길이 보장)
     */
    public String generateWithMinLength(int minLength) {
        long id = idGenerator.nextId();
        String shortCode = Base62Encoder.encodeWithPadding(id, minLength);
        log.debug("Generated short code with min length {}: {} from id: {}",
                minLength, shortCode, id);
        return shortCode;
    }

    /**
     * Short Code로부터 원본 ID 복원 (디버깅/분석용)
     *
     * @param shortCode short code
     * @return 원본 Snowflake ID
     */
    public long decodeToId(String shortCode) {
        return Base62Encoder.decode(shortCode);
    }
}