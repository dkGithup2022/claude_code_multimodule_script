package io.multi.hello.api.monitoring.dto;

import java.util.List;

/**
 * 링크 클릭 통계 응답 DTO
 */
public record LinkClickStatisticsResponse(
        Long linkId,
        long count,
        List<LinkClickResponse> clicks
) {
}
