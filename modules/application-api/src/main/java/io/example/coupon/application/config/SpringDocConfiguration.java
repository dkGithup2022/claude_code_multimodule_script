package io.example.coupon.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI couponOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coupon API")
                        .description("쿠폰 시스템 REST API 문서")
                        .version("v1.0.0"));
    }
}
