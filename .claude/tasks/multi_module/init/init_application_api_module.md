# Application API Module 초기화 스크립트 (Java 21)

## 사용법
```bash
"init_application_api_module.md 실행해줘"
```

## 실행 단계

### 1. 프로젝트 정보 자동 감지
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출

### 2. settings.gradle.kts 업데이트
```kotlin
include(":${감지된루트모듈}:application-api")
```

### 3. 모듈 디렉토리 구조 생성
```
${감지된루트모듈}/
└── application-api/
    ├── build.gradle.kts
    └── src/main/
        ├── java/${감지된패키지경로}/application/
        │   ├── ${루트모듈대문자}Application.java
        │   └── config/SpringDocConfiguration.java
        └── resources/
            └── application.yml
```

### 4. 파일 생성

#### 4-1. ${감지된루트모듈}/application-api/build.gradle.kts
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":${감지된루트모듈}:api"))
    implementation(project(":${감지된루트모듈}:service"))
    implementation(project(":${감지된루트모듈}:repository-jdbc"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    archiveClassifier = ""
}
```

#### 4-2. ${루트모듈대문자}Application.java
```java
package ${감지된패키지명}.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Multi-Module Spring Boot Application
 *
 * 헥사고날 아키텍처의 Entry Point로서 모든 필요한 모듈들의
 * 컴포넌트 스캔을 중앙에서 관리합니다.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "${감지된패키지명}.application",    // Application 모듈
    "${감지된패키지명}.api",            // API 모듈 (Controllers, DTOs)
    "${감지된패키지명}.service",        // Service 모듈 (Business Logic)
    "${감지된패키지명}.jdbc",           // JDBC 모듈 (Repository 구현체)
    "${감지된패키지명}.config"          // Auto Configuration 클래스들
})
@EnableJdbcRepositories(basePackages = "${감지된패키지명}.jdbc")
public class ${루트모듈대문자}Application {

    public static void main(String[] args) {
        SpringApplication.run(${루트모듈대문자}Application.class, args);
    }
}
```

#### 4-3. SpringDocConfiguration.java
```java
package ${감지된패키지명}.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("${루트모듈대문자} API")
                        .version("1.0.0")
                        .description("${루트모듈대문자} 도메인 API 문서"));
    }
}
```

#### 4-4. application.yml
```yaml
spring:
  application:
    name: ${감지된루트모듈}-application-api

  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

  sql:
    init:
      mode: always

  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080

logging:
  level:
    ${감지된패키지명}: DEBUG
    org.springframework.jdbc: DEBUG

springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /api-docs
```

### 5. 검증
- `./gradlew :${감지된루트모듈}:application-api:bootRun` 실행하여 애플리케이션 시작 확인
- `http://localhost:8080/swagger-ui.html` 접속하여 API 문서 확인
- 생성된 파일 구조 출력

## 예상 실행 결과
```
corehr/
└── application-api/
    ├── build.gradle.kts
    └── src/main/
        ├── java/com/searchkim/application/
        │   ├── CorehrApplication.java
        │   └── config/SpringDocConfiguration.java
        └── resources/
            └── application.yml
```

## 주요 특징
- **Spring Boot** 메인 애플리케이션
- **H2 데이터베이스** 내장
- **Swagger UI** API 문서화
- **전체 모듈** 통합 실행