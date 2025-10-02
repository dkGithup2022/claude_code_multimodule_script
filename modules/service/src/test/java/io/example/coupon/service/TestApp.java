package io.example.coupon.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;


@SpringBootApplication
@ComponentScan(basePackages = {"io.example.coupon.service", "io.example.coupon.infrastructure"})
@EnableJdbcRepositories(
        basePackages = {
                "io.example.coupon.infrastructure" // 추가 Repository 패키지
        }
)
public class TestApp {
}
