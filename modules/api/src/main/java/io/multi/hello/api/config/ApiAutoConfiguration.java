package io.multi.hello.api.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * API Auto Configuration
 *
 * API 모듈의 자동 설정을 담당합니다.
 * 컴포넌트 스캔은 Application 모듈에서 중앙 관리되므로
 * 여기서는 별도의 스캔 설정을 하지 않습니다.
 */
@AutoConfiguration
public class ApiAutoConfiguration {
    // 스캔 설정 제거 - Application 모듈에서 중앙 관리
}