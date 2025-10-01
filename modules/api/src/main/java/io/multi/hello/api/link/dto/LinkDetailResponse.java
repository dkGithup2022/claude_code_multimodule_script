package io.multi.hello.api.link.dto;

import io.multi.hello.model.link.Link;

import java.time.Instant;

/**
 * 링크 상세 응답 DTO (통계 포함)
 */
public record LinkDetailResponse(
        Long linkId,
        String originalUrl,
        String shortCode,
        String shortUrl,
        Long userId,
        Long clickCount,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt
) {
    /**
     * Link 도메인 모델과 클릭 수로부터 DTO 생성
     */
    public static LinkDetailResponse from(Link link, String baseUrl, long clickCount) {
        return new LinkDetailResponse(
                link.getLinkId(),
                link.getOriginalUrl(),
                link.getShortCode(),
                baseUrl + "/" + link.getShortCode(),
                link.getUserId(),
                clickCount,
                link.getExpiresAt(),
                link.getCreatedAt(),
                link.getUpdatedAt()
        );
    }
}