package io.example.coupon.api.coupon;

import io.example.coupon.api.coupon.dto.CouponRegisterRequest;
import io.example.coupon.api.coupon.dto.CouponResponse;
import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.service.coupon.CouponReader;
import io.example.coupon.service.coupon.CouponRegister;
import io.example.coupon.service.coupon.dto.CouponRegisterCommand;
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
 * Coupon REST API 컨트롤러
 */
@Tag(name = "Coupon", description = "쿠폰 관리 API")
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Slf4j
public class CouponApiController {

    private final CouponReader couponReader;
    private final CouponRegister couponRegister;

    @Operation(summary = "쿠폰 등록", description = "새로운 쿠폰을 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "쿠폰 등록 성공",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CouponResponse> registerCoupon(@Valid @RequestBody CouponRegisterRequest request) {
        log.info("Registering coupon: name={}, userId={}", request.getName(), request.getUserId());

        CouponRegisterCommand command = new CouponRegisterCommand(
                request.getName(),
                request.getDiscountAmount(),
                request.getTotalQuantity(),
                request.getIssuedCount(),
                request.getUserId(),
                request.getStartDate(),
                request.getEndDate()
        );

        Coupon coupon = couponRegister.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponResponse.from(coupon));
    }

    @Operation(summary = "쿠폰 조회", description = "쿠폰 ID로 쿠폰 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{couponId}")
    public ResponseEntity<CouponResponse> getCoupon(
            @Parameter(description = "쿠폰 ID", required = true) @PathVariable("couponId") Long couponId) {
        log.info("Getting coupon: couponId={}", couponId);

        Coupon coupon = couponReader.findById(couponId);
        return coupon != null
                ? ResponseEntity.ok(CouponResponse.from(coupon))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "소유자별 쿠폰 목록 조회", description = "쿠폰 소유자 ID로 쿠폰 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getCouponsByOwnerId(
            @Parameter(description = "소유자 ID", required = true) @RequestParam("ownerId") Long ownerId) {
        log.info("Getting coupons by ownerId: {}", ownerId);

        List<CouponResponse> coupons = couponReader.findByOwnerId(ownerId).stream()
                .map(CouponResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(coupons);
    }
}
