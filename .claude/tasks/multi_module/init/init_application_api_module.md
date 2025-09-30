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
        │   └── config/
        │       ├── ModuleScanConfig.java
        │       └── SpringDocConfiguration.java
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
    implementation(project(":${감지된루트모듈}:schema"))  // DDL/DML 스크립트

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

/**
 * Multi-Module Spring Boot Application
 *
 * 컴포넌트 스캔 설정은 application.config.ModuleScanConfig에서 중앙 관리됩니다.
 */
@SpringBootApplication(
    scanBasePackages = "${감지된패키지명}.application.config"
)
public class ${루트모듈대문자}Application {

    public static void main(String[] args) {
        SpringApplication.run(${루트모듈대문자}Application.class, args);
    }
}
```

#### 4-3. ModuleScanConfig.java
```java
package ${감지된패키지명}.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Module Scan Configuration
 *
 * 멀티모듈 프로젝트의 컴포넌트 스캔 범위를 중앙에서 관리합니다.
 *
 * @ComponentScan: 일반 컴포넌트(@Component, @Service 등) 스캔
 * @EnableJdbcRepositories: Spring Data JDBC Repository 인터페이스 스캔 및 구현체 자동 생성
 */
@Configuration
@ComponentScan(basePackages = {
    "${감지된패키지명}.api",
    "${감지된패키지명}.service",
    "${감지된패키지명}.infrastructure",
    "${감지된패키지명}.jdbc"  // JDBC 구현체 클래스 스캔
})
@EnableJdbcRepositories(basePackages = "${감지된패키지명}.jdbc")  // CrudRepository 인터페이스 스캔
public class ModuleScanConfig {
}
```

#### 4-4. SpringDocConfiguration.java
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

#### 4-5. application.yml
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
      mode: embedded

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