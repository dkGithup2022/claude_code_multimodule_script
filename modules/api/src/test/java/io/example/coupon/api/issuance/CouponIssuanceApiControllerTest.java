package io.example.coupon.api.issuance;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.coupon.api.issuance.dto.CouponIssueRequest;
import io.example.coupon.api.issuance.dto.CouponIssuanceResponse;
import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceIdentity;
import io.example.coupon.model.couponissuance.CouponIssuanceStatus;
import io.example.coupon.service.coupon.issuance.CouponIssuanceReader;
import io.example.coupon.service.coupon.issuance.CouponIssuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class CouponIssuanceApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CouponIssuer couponIssuer;

    @Mock
    private CouponIssuanceReader couponIssuanceReader;

    @InjectMocks
    private CouponIssuanceApiController couponIssuanceApiController;

    private ObjectMapper objectMapper;

    // Test data based on CouponIssuance model spec
    private final CouponIssuance sampleIssuance = new CouponIssuance(
            1L,                               // issuanceId
            1L,                               // couponId
            1L,                               // userId
            Instant.now(),                    // issuedAt
            null,                             // usedAt (not used yet)
            CouponIssuanceStatus.ISSUED,      // status
            Instant.now(),                    // createdAt
            Instant.now()                     // updatedAt
    );

    private final CouponIssuanceIdentity testIdentity = new CouponIssuanceIdentity(1L);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponIssuanceApiController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JavaTimeModule 등록
    }

    // POST /api/coupons/{couponId}/issue - 쿠폰 발급

    @Test
    void issueCoupon_validRequest_returnsCreatedWithIssuance() throws Exception {
        // given
        CouponIssueRequest request = new CouponIssueRequest();
        request.setUserId(1L);

        when(couponIssuer.issueCoupon(1L, 1L)).thenReturn(sampleIssuance);

        // when & then
        mockMvc.perform(post("/api/coupons/{couponId}/issue", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.issuanceId").value(1))
                .andExpect(jsonPath("$.couponId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("ISSUED"))
                .andExpect(jsonPath("$.issuedAt").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(couponIssuer).issueCoupon(1L, 1L);
    }

    // GET /api/coupons/issuances/{issuanceId} - ID 조회

    @Test
    void getIssuance_existingId_returnsOkWithIssuance() throws Exception {
        // given
        when(couponIssuanceReader.findById(testIdentity)).thenReturn(Optional.of(sampleIssuance));

        // when & then
        mockMvc.perform(get("/api/coupons/issuances/{issuanceId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuanceId").value(1))
                .andExpect(jsonPath("$.couponId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("ISSUED"));

        verify(couponIssuanceReader).findById(testIdentity);
    }

    @Test
    void getIssuance_nonExistingId_returnsNotFound() throws Exception {
        // given
        CouponIssuanceIdentity nonExistingIdentity = new CouponIssuanceIdentity(999L);
        when(couponIssuanceReader.findById(nonExistingIdentity)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/coupons/issuances/{issuanceId}", 999L))
                .andExpect(status().isNotFound());

        verify(couponIssuanceReader).findById(nonExistingIdentity);
    }

    // GET /api/coupons/issuances - 전체 조회

    @Test
    void getAllIssuances_returnsOkWithList() throws Exception {
        // given
        CouponIssuance anotherIssuance = new CouponIssuance(
                2L, 2L, 2L, Instant.now(), null,
                CouponIssuanceStatus.ISSUED, Instant.now(), Instant.now()
        );
        when(couponIssuanceReader.findAll()).thenReturn(List.of(sampleIssuance, anotherIssuance));

        // when & then
        mockMvc.perform(get("/api/coupons/issuances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].issuanceId").value(1))
                .andExpect(jsonPath("$[1].issuanceId").value(2));

        verify(couponIssuanceReader).findAll();
    }

    @Test
    void getAllIssuances_emptyList_returnsOkWithEmptyArray() throws Exception {
        // given
        when(couponIssuanceReader.findAll()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/coupons/issuances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(couponIssuanceReader).findAll();
    }

    // GET /api/coupons/{couponId}/issuances - Coupon별 조회

    @Test
    void getIssuancesByCoupon_existingCouponId_returnsOkWithList() throws Exception {
        // given
        CouponIssuance anotherIssuance = new CouponIssuance(
                2L, 1L, 2L, Instant.now(), null,
                CouponIssuanceStatus.ISSUED, Instant.now(), Instant.now()
        );
        when(couponIssuanceReader.findByCouponId(1L)).thenReturn(List.of(sampleIssuance, anotherIssuance));

        // when & then
        mockMvc.perform(get("/api/coupons/{couponId}/issuances", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].couponId").value(1))
                .andExpect(jsonPath("$[1].couponId").value(1));

        verify(couponIssuanceReader).findByCouponId(1L);
    }

    @Test
    void getIssuancesByCoupon_nonExistingCouponId_returnsOkWithEmptyArray() throws Exception {
        // given
        when(couponIssuanceReader.findByCouponId(999L)).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/coupons/{couponId}/issuances", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(couponIssuanceReader).findByCouponId(999L);
    }

    // GET /api/users/{userId}/coupons - User별 조회

    @Test
    void getUserCoupons_existingUserId_returnsOkWithList() throws Exception {
        // given
        CouponIssuance anotherIssuance = new CouponIssuance(
                2L, 2L, 1L, Instant.now(), null,
                CouponIssuanceStatus.ISSUED, Instant.now(), Instant.now()
        );
        when(couponIssuanceReader.findByUserId(1L)).thenReturn(List.of(sampleIssuance, anotherIssuance));

        // when & then
        mockMvc.perform(get("/api/users/{userId}/coupons", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].userId").value(1));

        verify(couponIssuanceReader).findByUserId(1L);
    }

    @Test
    void getUserCoupons_nonExistingUserId_returnsOkWithEmptyArray() throws Exception {
        // given
        when(couponIssuanceReader.findByUserId(999L)).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/users/{userId}/coupons", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(couponIssuanceReader).findByUserId(999L);
    }
}
