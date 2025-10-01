package io.multi.hello.api.monitoring.dto;

import java.time.Instant;

/**
 * 링크 모니터링 응답 DTO
 */
public record LinkMonitoringResponse(
        Long linkId,
        Long userId,
        String originalUrl,
        String shortCode,
        String shortUrl,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt
) {
}
