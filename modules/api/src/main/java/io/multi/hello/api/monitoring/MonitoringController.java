package io.multi.hello.api.monitoring;

import io.multi.hello.api.monitoring.dto.LinkClickResponse;
import io.multi.hello.api.monitoring.dto.LinkClickStatisticsResponse;
import io.multi.hello.api.monitoring.dto.LinkMonitoringResponse;
import io.multi.hello.model.link.Link;
import io.multi.hello.model.linkclick.LinkClick;
import io.multi.hello.service.link.LinkReader;
import io.multi.hello.service.linkclick.LinkClickReader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 모니터링 API Controller
 *
 * 링크와 클릭 통계 정보를 제공합니다.
 */
@Tag(name = "Monitoring", description = "링크 및 클릭 통계 모니터링 API")
@RestController
@RequestMapping("/api/v1/monitoring")
@RequiredArgsConstructor
@Slf4j
public class MonitoringController {

    private final LinkReader linkReader;
    private final LinkClickReader linkClickReader;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * 모든 링크 조회
     *
     * GET /api/v1/monitoring/links
     */
    @Operation(summary = "모든 링크 조회", description = "시스템의 모든 링크 정보를 조회합니다.")
    @GetMapping("/links")
    public ResponseEntity<List<LinkMonitoringResponse>> getAllLinks() {
        log.info("Getting all links for monitoring");
        List<Link> links = linkReader.findAll();

        List<LinkMonitoringResponse> response = links.stream()
                .map(link -> new LinkMonitoringResponse(
                        link.getLinkId(),
                        link.getUserId(),
                        link.getOriginalUrl(),
                        link.getShortCode(),
                        baseUrl + "/" + link.getShortCode(),
                        link.getExpiresAt(),
                        link.getCreatedAt(),
                        link.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 링크의 클릭 통계 조회
     *
     * GET /api/v1/monitoring/links/{linkId}/clicks
     */
    @Operation(summary = "링크 클릭 통계 조회", description = "특정 링크의 클릭 수와 클릭 상세 정보를 조회합니다.")
    @GetMapping("/links/{linkId}/clicks")
    public ResponseEntity<LinkClickStatisticsResponse> getLinkClickStatistics(
            @PathVariable Long linkId
    ) {
        log.info("Getting click statistics for linkId: {}", linkId);

        long count = linkClickReader.countByLinkId(linkId);
        List<LinkClick> clicks = linkClickReader.findByLinkId(linkId);

        List<LinkClickResponse> clickResponses = clicks.stream()
                .map(click -> new LinkClickResponse(
                        click.getClickId(),
                        click.getLinkId(),
                        click.getClickedAt(),
                        click.getIpAddress(),
                        click.getUserAgent(),
                        click.getReferer()
                ))
                .collect(Collectors.toList());

        LinkClickStatisticsResponse response = new LinkClickStatisticsResponse(
                linkId,
                count,
                clickResponses
        );

        return ResponseEntity.ok(response);
    }
}
