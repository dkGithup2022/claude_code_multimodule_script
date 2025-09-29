package com.example.hello.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Quick Multimodule Application
 *
 * 모든 모듈을 통합하는 메인 애플리케이션 클래스입니다.
 * 중앙화된 컴포넌트 스캔을 통해 모든 모듈의 컴포넌트를 관리합니다.
 */
@SpringBootApplication(
        scanBasePackages = {
                "com.example.hello"  // 모든 모듈의 패키지를 포함
        }
)
@EnableJdbcRepositories(
        basePackages = {
                "com.example.hello.jdbc",       // JDBC Repository 스캔
                "com.example.hello.infrastructure"  // 추가 Repository 패키지
        }
)
public class QuickMultimoduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickMultimoduleApplication.class, args);
    }
}