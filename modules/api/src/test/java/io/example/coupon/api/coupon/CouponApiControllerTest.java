package io.example.coupon.api.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.coupon.api.coupon.dto.CouponRegisterRequest;
import io.example.coupon.api.coupon.dto.CouponResponse;
import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.service.coupon.CouponReader;
import io.example.coupon.service.coupon.CouponRegister;
import io.example.coupon.service.coupon.dto.CouponRegisterCommand;
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
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class CouponApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CouponReader couponReader;

    @Mock
    private CouponRegister couponRegister;

    @InjectMocks
    private CouponApiController couponApiController;

    private ObjectMapper objectMapper;

    // Test data based on Coupon model spec
    private final Coupon sampleCoupon = new Coupon(
            1L,                             // couponId
            "testCoupon",                   // name
            1000,                           // discountAmount
            100,                            // totalQuantity
            0,                              // issuedCount
            1L,                             // userId
            Instant.now(),                  // startDate
            Instant.now().plus(30, ChronoUnit.DAYS),  // endDate
            Instant.now(),                  // createdAt
            Instant.now()                   // updatedAt
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponApiController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JavaTimeModule 등록
    }

    // POST /api/coupons - 쿠폰 등록

    @Test
    void registerCoupon_validRequest_returnsCreatedWithCoupon() throws Exception {
        // given
        CouponRegisterRequest request = new CouponRegisterRequest();
        request.setName("testCoupon");
        request.setDiscountAmount(1000);
        request.setTotalQuantity(100);
        request.setIssuedCount(0);
        request.setUserId(1L);
        request.setStartDate(Instant.now());
        request.setEndDate(Instant.now().plus(30, ChronoUnit.DAYS));

        when(couponRegister.register(any(CouponRegisterCommand.class))).thenReturn(sampleCoupon);

        // when & then
        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponId").value(1))
                .andExpect(jsonPath("$.name").value("testCoupon"))
                .andExpect(jsonPath("$.discountAmount").value(1000))
                .andExpect(jsonPath("$.totalQuantity").value(100))
                .andExpect(jsonPath("$.issuedCount").value(0))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.endDate").exists());

        verify(couponRegister).register(any(CouponRegisterCommand.class));
    }

    // GET /api/coupons/{couponId} - ID 조회

    @Test
    void getCoupon_existingId_returnsOkWithCoupon() throws Exception {
        // given
        when(couponReader.findById(1L)).thenReturn(sampleCoupon);

        // when & then
        mockMvc.perform(get("/api/coupons/{couponId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponId").value(1))
                .andExpect(jsonPath("$.name").value("testCoupon"))
                .andExpect(jsonPath("$.discountAmount").value(1000))
                .andExpect(jsonPath("$.totalQuantity").value(100))
                .andExpect(jsonPath("$.issuedCount").value(0));

        verify(couponReader).findById(1L);
    }

    @Test
    void getCoupon_nonExistingId_returnsNotFound() throws Exception {
        // given
        when(couponReader.findById(999L)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/coupons/{couponId}", 999L))
                .andExpect(status().isNotFound());

        verify(couponReader).findById(999L);
    }

    // GET /api/coupons?ownerId=xxx - Owner 조회

    @Test
    void getCouponsByOwnerId_existingOwnerId_returnsOkWithList() throws Exception {
        // given
        Coupon anotherCoupon = new Coupon(
                2L, "anotherCoupon", 2000, 50, 0, 1L,
                Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS),
                Instant.now(), Instant.now()
        );
        when(couponReader.findByOwnerId(1L)).thenReturn(List.of(sampleCoupon, anotherCoupon));

        // when & then
        mockMvc.perform(get("/api/coupons").param("ownerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].couponId").value(1))
                .andExpect(jsonPath("$[1].couponId").value(2));

        verify(couponReader).findByOwnerId(1L);
    }

    @Test
    void getCouponsByOwnerId_nonExistingOwnerId_returnsOkWithEmptyArray() throws Exception {
        // given
        when(couponReader.findByOwnerId(999L)).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/coupons").param("ownerId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(couponReader).findByOwnerId(999L);
    }
}
