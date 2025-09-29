package com.example.hello.jdbc.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * JDBC Repository Auto Configuration
 *
 * JDBC Repository 모듈의 자동 설정을 담당합니다.
 * 컴포넌트 스캔과 Repository 활성화는 Application 모듈에서 중앙 관리되므로
 * 여기서는 별도의 스캔 설정을 하지 않습니다.
 *
 * 이 설정 클래스는 단순히 "JDBC 모듈이 존재함"을 알리는 역할만 합니다.
 */
@AutoConfiguration
public class JdbcRepositoryAutoConfiguration {
    // 모든 스캔 설정 제거 - Application 모듈에서 중앙 관리
}