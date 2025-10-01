package io.multi.hello.api.monitoring.dto;

import java.time.Instant;

/**
 * 링크 클릭 응답 DTO
 */
public record LinkClickResponse(
        Long clickId,
        Long linkId,
        Instant clickedAt,
        String ipAddress,
        String userAgent,
        String referer
) {
}
