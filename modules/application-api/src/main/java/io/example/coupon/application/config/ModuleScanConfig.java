package io.example.coupon.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@ComponentScan(basePackages = {
        "io.example.coupon.service",
        "io.example.coupon.infrastructure",
        "io.example.coupon.api"
})
@EnableJdbcRepositories(
        basePackages = {
                "io.example.coupon.infrastructure" // 추가 Repository 패키지
        }
)
public class ModuleScanConfig {
}
