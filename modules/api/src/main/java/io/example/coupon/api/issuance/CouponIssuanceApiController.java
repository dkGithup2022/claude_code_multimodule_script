package io.example.coupon.api.issuance;

import io.example.coupon.api.issuance.dto.CouponIssueRequest;
import io.example.coupon.api.issuance.dto.CouponIssuanceResponse;
import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceIdentity;
import io.example.coupon.service.coupon.issuance.CouponIssuanceReader;
import io.example.coupon.service.coupon.issuance.CouponIssuer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CouponIssuance REST API 컨트롤러
 */
@Tag(name = "Coupon Issuance", description = "쿠폰 발급 관리 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CouponIssuanceApiController {

    private final CouponIssuer couponIssuer;
    private final CouponIssuanceReader couponIssuanceReader;

    @Operation(summary = "쿠폰 발급", description = "특정 쿠폰을 사용자에게 발급합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "쿠폰 발급 성공",
                    content = @Content(schema = @Schema(implementation = CouponIssuanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음", content = @Content)
    })
    @PostMapping("/coupons/{couponId}/issue")
    public ResponseEntity<CouponIssuanceResponse> issueCoupon(
            @Parameter(description = "쿠폰 ID", required = true) @PathVariable("couponId") Long couponId,
            @Valid @RequestBody CouponIssueRequest request) {

        log.info("Issuing coupon: couponId={}, userId={}", couponId, request.getUserId());

        CouponIssuance issuance = couponIssuer.issueCoupon(couponId, request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponIssuanceResponse.from(issuance));
    }

    @Operation(summary = "쿠폰 발급 내역 조회", description = "발급 ID로 쿠폰 발급 내역을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CouponIssuanceResponse.class))),
            @ApiResponse(responseCode = "404", description = "발급 내역을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/coupons/issuances/{issuanceId}")
    public ResponseEntity<CouponIssuanceResponse> getIssuance(
            @Parameter(description = "발급 ID", required = true) @PathVariable("issuanceId") Long issuanceId) {
        log.info("Getting issuance: issuanceId={}", issuanceId);

        return couponIssuanceReader.findById(new CouponIssuanceIdentity(issuanceId))
                .map(CouponIssuanceResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "전체 쿠폰 발급 내역 조회", description = "모든 쿠폰 발급 내역을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CouponIssuanceResponse.class)))
    })
    @GetMapping("/coupons/issuances")
    public ResponseEntity<List<CouponIssuanceResponse>> getAllIssuances() {
        log.info("Getting all issuances");

        List<CouponIssuanceResponse> issuances = couponIssuanceReader.findAll().stream()
                .map(CouponIssuanceResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(issuances);
    }

    @Operation(summary = "쿠폰별 발급 내역 조회", description = "특정 쿠폰의 모든 발급 내역을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CouponIssuanceResponse.class)))
    })
    @GetMapping("/coupons/{couponId}/issuances")
    public ResponseEntity<List<CouponIssuanceResponse>> getIssuancesByCoupon(
            @Parameter(description = "쿠폰 ID", required = true) @PathVariable("couponId") Long couponId) {
        log.info("Getting issuances by couponId: {}", couponId);

        List<CouponIssuanceResponse> issuances = couponIssuanceReader.findByCouponId(couponId).stream()
                .map(CouponIssuanceResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(issuances);
    }

    @Operation(summary = "사용자별 쿠폰 발급 내역 조회", description = "특정 사용자가 발급받은 모든 쿠폰 내역을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CouponIssuanceResponse.class)))
    })
    @GetMapping("/users/{userId}/coupons")
    public ResponseEntity<List<CouponIssuanceResponse>> getUserCoupons(
            @Parameter(description = "사용자 ID", required = true) @PathVariable("userId") Long userId) {
        log.info("Getting user coupons: userId={}", userId);

        List<CouponIssuanceResponse> issuances = couponIssuanceReader.findByUserId(userId).stream()
                .map(CouponIssuanceResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(issuances);
    }
}
