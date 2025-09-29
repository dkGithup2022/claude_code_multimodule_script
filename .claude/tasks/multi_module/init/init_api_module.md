# API Module 초기화 스크립트 (Java 21)

## 사용법
```bash
"init_api_module.md 실행해줘"
```

## 실행 단계

### 1. 프로젝트 정보 자동 감지
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출

### 2. settings.gradle.kts 업데이트
```kotlin
include(":${감지된루트모듈}:api")
```

### 3. 모듈 디렉토리 구조 생성
```
${감지된루트모듈}/
└── api/
    ├── build.gradle.kts
    └── src/main/
        ├── java/${감지된패키지경로}/
        │   ├── example/
        │   │   ├── ExampleApiController.java
        │   │   └── dto/ExampleResponse.java
        │   └── config/
        │       ├── GlobalExceptionHandler.java
        │       └── ApiAutoConfiguration.java
        └── resources/META-INF/spring/
            └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

### 4. 파일 생성

#### 4-1. ${감지된루트모듈}/api/build.gradle.kts
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":${감지된루트모듈}:model"))
    implementation(project(":${감지된루트모듈}:service"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

#### 4-2. ExampleResponse.java
```java
package ${감지된패키지명}.api.example.dto;

import ${감지된패키지명}.model.example.ExampleModel;
import lombok.Value;

import java.time.Instant;

@Value
public class ExampleResponse {
    Long exampleId;
    String name;
    Instant createdAt;
    Instant updatedAt;

    public static ExampleResponse from(ExampleModel example) {
        return new ExampleResponse(
                example.getExampleId(),
                example.getName(),
                example.getCreatedAt(),
                example.getUpdatedAt()
        );
    }
}
```

#### 4-3. ExampleApiController.java
```java
package ${감지된패키지명}.api.example;

import ${감지된패키지명}.service.example.ExampleReader;
import ${감지된패키지명}.model.example.ExampleIdentity;
import ${감지된패키지명}.api.example.dto.ExampleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/examples")
public class ExampleApiController {

    private final ExampleLookUpService exampleLookUpService;

    public ExampleApiController(ExampleLookUpService exampleLookUpService) {
        this.exampleLookUpService = exampleLookUpService;
    }

    @GetMapping("/{exampleId}")
    public ExampleResponse getExample(@PathVariable Long exampleId) {
        var example = exampleLookUpService.findById(new ExampleIdentity(exampleId));
        return ExampleResponse.from(example);
    }

    @GetMapping
    public List<ExampleResponse> getAllExamples() {
        return exampleLookUpService.findAll()
                .stream()
                .map(ExampleResponse::from)
                .collect(Collectors.toList());
    }
}
```


#### 4-6. GlobalExceptionHandler.java
```java
package ${감지된패키지명}.api.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        if (e.getMessage().contains("not found")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}
```

#### 4-7. ApiAutoConfiguration.java
```java
package ${감지된패키지명}.api.config;

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
```

#### 4-8. AutoConfiguration.imports
```
${감지된패키지명}.config.ApiAutoConfiguration
```

### 5. 검증
- `./gradlew :${감지된루트모듈}:api:build` 실행하여 컴파일 확인
- 생성된 파일 구조 출력

## 예상 실행 결과
```
corehr/
└── api/
    ├── build.gradle.kts
    └── src/main/
        ├── java/com/searchkim/
        │   ├── example/
        │   │   ├── ExampleApiController.java
        │   │   └── dto/ExampleResponse.java
        │   └── config/
        │       ├── GlobalExceptionHandler.java
        │       └── ApiAutoConfiguration.java
        └── resources/META-INF/spring/
            └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## 주요 특징
- **REST API** 컨트롤러 제공
- **DTO 패턴** 적용
- **Service Layer** 연동
- **Global Exception Handler** 내장 (RuntimeException → 404/500 자동 변환)
- **Auto Configuration** 지원