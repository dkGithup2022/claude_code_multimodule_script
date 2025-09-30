package io.multi.hello.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Module Scan Configuration
 *
 * 멀티모듈 프로젝트의 컴포넌트 스캔 범위를 중앙에서 관리합니다.
 */
@Configuration
@ComponentScan(basePackages = {
    "io.multi.hello.api",
    "io.multi.hello.service",
    "io.multi.hello.infrastructure",
    "io.multi.hello.jdbc"
})
@EnableJdbcRepositories ("io.multi.hello.jdbc")
public class ModuleScanConfig {
}