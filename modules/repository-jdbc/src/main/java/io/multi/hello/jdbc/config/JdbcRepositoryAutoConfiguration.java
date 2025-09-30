package io.multi.hello.jdbc.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * JDBC Repository Auto Configuration
 *
 * JDBC Repository 모듈의 자동 설정을 담당합니다.
 *
 * 이 설정 클래스는 단순히 "JDBC 모듈이 존재함"을 알리는 역할만 합니다.
 */
@AutoConfiguration
public class JdbcRepositoryAutoConfiguration {
    // 모든 스캔 설정 제거 - Application 모듈에서 중앙 관리
}