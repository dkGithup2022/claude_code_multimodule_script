# Exception Module 초기화 스크립트 (Java 21)

## 사용법
```bash
"init_exception_module.md 실행해줘"
```

## 실행 단계

### 1. 프로젝트 정보 자동 감지
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출
- 사용자 확인: "corehr 모듈에 com.searchkim 패키지로 생성하겠습니다. 맞나요?"

### 2. settings.gradle.kts 업데이트
```kotlin
include(":${감지된루트모듈}:exception")
```

### 3. 모듈 디렉토리 구조 생성
```
${감지된루트모듈}/
└── exception/
    ├── build.gradle.kts
    └── src/main/java/${감지된패키지경로}/exception/
        └── ExampleNotFoundException.java
```

### 4. 파일 생성

#### 4-1. ${감지된루트모듈}/exception/build.gradle.kts
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

dependencies {
    implementation(project(":${감지된루트모듈}:model"))
}
```

#### 4-2. ExampleNotFoundException.java
```java
package ${감지된패키지명}.exception;

public class ExampleNotFoundException extends RuntimeException {

    public ExampleNotFoundException(Long exampleId) {
        super("Example not found with id: " + exampleId);
    }

    public ExampleNotFoundException(String message) {
        super(message);
    }

    public ExampleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 5. 검증
- `./gradlew :${감지된루트모듈}:exception:build` 실행하여 컴파일 확인
- 생성된 파일 구조 출력

## 예상 실행 결과
```
corehr/
└── exception/
    ├── build.gradle.kts
    └── src/main/java/com/searchkim/exception/
        ├── ExampleNotFoundException.java

```

## 주요 특징
- **Model 모듈** 의존성 포함
- **표준 Exception 패턴** 적용
- **Java 21** 호환
- **각 도메인별** Exception 클래스 제공