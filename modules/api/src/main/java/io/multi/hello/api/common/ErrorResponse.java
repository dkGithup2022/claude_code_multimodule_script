package io.multi.hello.api.common;

import java.time.Instant;

/**
 * API 에러 응답 DTO
 */
public record ErrorResponse(
        String code,
        String message,
        Instant timestamp
) {
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, Instant.now());
    }
}