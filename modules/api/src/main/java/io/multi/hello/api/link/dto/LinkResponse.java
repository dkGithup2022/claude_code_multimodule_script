package io.multi.hello.api.link.dto;

import io.multi.hello.model.link.Link;

import java.time.Instant;

/**
 * 링크 응답 DTO
 */
public record LinkResponse(
        Long linkId,
        String originalUrl,
        String shortCode,
        String shortUrl,
        Long userId,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt
) {
    /**
     * Link 도메인 모델로부터 DTO 생성
     */
    public static LinkResponse from(Link link, String baseUrl) {
        return new LinkResponse(
                link.getLinkId(),
                link.getOriginalUrl(),
                link.getShortCode(),
                baseUrl + "/" + link.getShortCode(),
                link.getUserId(),
                link.getExpiresAt(),
                link.getCreatedAt(),
                link.getUpdatedAt()
        );
    }
}