package io.multi.hello.api.link;

import io.multi.hello.api.link.dto.CreateLinkRequest;
import io.multi.hello.api.link.dto.LinkDetailResponse;
import io.multi.hello.api.link.dto.LinkResponse;
import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;
import io.multi.hello.service.link.LinkReader;
import io.multi.hello.service.link.LinkWriter;
import org.springframework.beans.factory.annotation.Value;
import io.multi.hello.service.link.dto.CreateLinkCommand;
import io.multi.hello.service.linkclick.LinkClickReader;
import io.multi.hello.service.linkclick.LinkClickWriter;
import io.multi.hello.model.linkclick.LinkClick;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;

/**
 * Link API 컨트롤러
 *
 * URL 단축, 조회, 삭제 및 리다이렉트 기능을 제공합니다.
 */
@Tag(name = "Link", description = "URL 단축 및 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class LinkController {

    private final LinkWriter linkWriter;
    private final LinkReader linkReader;
    private final LinkClickWriter linkClickWriter;
    private final LinkClickReader linkClickReader;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * 링크 생성 (URL 단축)
     *
     * @param request 링크 생성 요청
     * @return 생성된 링크 정보
     */
    @PostMapping("/api/v1/links")
    public ResponseEntity<LinkResponse> createLink(
            @Valid @RequestBody CreateLinkRequest request) {

        log.info("Creating short link for: url={}, userId={}", request.url(), request.userId());

        CreateLinkCommand command = new CreateLinkCommand(
                request.userId(),
                request.url()
        );

        Link link = linkWriter.create(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(LinkResponse.from(link, baseUrl));
    }

    /**
     * Short Code로 링크 조회
     *
     * @param shortCode 짧은 코드
     * @return 링크 정보
     */
    @GetMapping("/api/v1/links/short/{shortCode}")
    public ResponseEntity<LinkResponse> getLinkByShortCode(
            @PathVariable String shortCode) {

        log.info("Getting link by shortCode: {}", shortCode);

        Link link = linkReader.fromShortUrl(shortCode);

        return ResponseEntity.ok(
                LinkResponse.from(link, baseUrl)
        );
    }

    /**
     * 링크 ID로 상세 조회 (통계 포함)
     *
     * @param linkId 링크 ID
     * @return 링크 상세 정보
     */
    @GetMapping("/api/v1/links/{linkId}")
    public ResponseEntity<LinkDetailResponse> getLink(
            @PathVariable Long linkId) {

        log.info("Getting link detail: linkId={}", linkId);

        // TODO: LinkReader에 findById 메서드 필요 (현재는 NOT_IMPLEMENTED)
        // Link link = linkReader.findById(new LinkIdentity(linkId));

        long clickCount = linkClickReader.countByLinkId(linkId);

        // return ResponseEntity.ok(
        //     LinkDetailResponse.from(link, baseUrl, clickCount)
        // );

        // TODO: LinkReader.findById 구현 후 활성화
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * 링크 삭제
     *
     * @param linkId 링크 ID
     * @return 204 No Content
     */
    @DeleteMapping("/api/v1/links/{linkId}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long linkId) {
        log.info("Deleting link: linkId={}", linkId);

        linkWriter.deleteById(new LinkIdentity(linkId));

        return ResponseEntity.noContent().build();
    }

    /**
     * 🔥 핵심: Short URL 리다이렉트
     *
     * @param shortCode 짧은 코드
     * @param request HTTP 요청
     * @return 원본 URL로 리다이렉트
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {

        log.info("Redirecting shortCode: {}", shortCode);

        // 1. 링크 조회
        Link link = linkReader.fromShortUrl(shortCode);

        // 2. 만료 체크
        if (link.getExpiresAt() != null &&
                link.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Link has expired");
        }

        // 3. 클릭 기록
        LinkClick click = new LinkClick(
            null,
            link.getLinkId(),
            Instant.now(),
            getClientIp(request),
            request.getHeader("User-Agent"),
            request.getHeader("Referer")
        );
        linkClickWriter.record(click);

        // 4. 리다이렉트 URL 처리 (프로토콜 자동 추가)
        String redirectUrl = link.getOriginalUrl();
        if (!redirectUrl.startsWith("http://") && !redirectUrl.startsWith("https://")) {
            redirectUrl = "https://" + redirectUrl;
        }

        return ResponseEntity
                .status(HttpStatus.FOUND)  // 302 Found
                .location(URI.create(redirectUrl))
                .build();
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}