# Service Module 초기화 스크립트 (Java 21)

## 사용법
```bash
"init_service_module.md 실행해줘"
```

## 실행 단계

### 1. 프로젝트 정보 자동 감지
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출

### 2. settings.gradle.kts 업데이트
```kotlin
include(":${감지된루트모듈}:service")
```

### 3. 모듈 디렉토리 구조 생성
```
${감지된루트모듈}/
└── service/
    ├── build.gradle.kts
    └── src/main/java/${감지된패키지경로}/
        └── example/
            ├── ExampleReader.java (조회 인터페이스)
            ├── ExampleWriter.java (변경 인터페이스)
            └── impl/
                ├── DefaultExampleReader.java (조회 구현체)
                └── DefaultExampleWriter.java (변경 구현체)
```

### 4. 파일 생성

#### 4-1. ${감지된루트모듈}/service/build.gradle.kts
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

dependencies {
    implementation(project(":${감지된루트모듈}:model"))
    implementation(project(":${감지된루트모듈}:exception"))
    implementation(project(":${감지된루트모듈}:infrastructure"))
}
```

#### 4-2. ExampleReader.java (조회 인터페이스)
```java
package ${감지된패키지명}.example;

import ${감지된패키지명}.example.Example;
import ${감지된패키지명}.example.ExampleIdentity;

import java.util.List;

/**
 * Example 도메인 조회 서비스 인터페이스
 *
 * CQRS 패턴의 Query 책임을 담당합니다.
 */
public interface ExampleReader {

    /**
     * ID로 Example 조회
     *
     * @param identity Example 식별자
     * @return Example 엔티티 (없으면 null)
     */
    Example findByIdentity(ExampleIdentity identity);

    /**
     * 모든 Example 조회
     *
     * @return Example 목록
     */
    List<Example> findAll();
}
```

#### 4-3. ExampleWriter.java (변경 인터페이스)
```java
package ${감지된패키지명}.example;

import ${감지된패키지명}.example.Example;
import ${감지된패키지명}.example.ExampleIdentity;

/**
 * Example 도메인 변경 서비스 인터페이스
 *
 * CQRS 패턴의 Command 책임을 담당합니다.
 */
public interface ExampleWriter {

    /**
     * Example 생성 또는 수정 (upsert)
     *
     * @param example 저장할 Example
     * @return 저장된 Example
     */
    Example upsert(Example example);

    /**
     * ID로 Example 삭제
     *
     * @param identity Example 식별자
     */
    void delete(ExampleIdentity identity);
}
```

#### 4-4. DefaultExampleReader.java (조회 구현체)
```java
package ${감지된패키지명}.example.impl;

import ${감지된패키지명}.example.Example;
import ${감지된패키지명}.example.ExampleIdentity;
import ${감지된패키지명}.example.ExampleReader;
import ${감지된패키지명}.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Example 도메인 조회 서비스 구현체
 *
 * CQRS 패턴의 Query 책임을 구현합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultExampleReader implements ExampleReader {

    private final ExampleRepository exampleRepository;

    @Override
    public Example findByIdentity(ExampleIdentity identity) {
        var example = exampleRepository.findById(identity).orElse(null);
        log.info("Find Example by Identity {} , Found Example {}", identity, example);
        return example;
    }

    @Override
    public List<Example> findAll() {
        var examples = exampleRepository.findAll();
        log.info("FindAll examples - {} found", examples.size());
        return examples;
    }
}
```

#### 4-5. DefaultExampleWriter.java (변경 구현체)
```java
package ${감지된패키지명}.example.impl;

import ${감지된패키지명}.example.Example;
import ${감지된패키지명}.example.ExampleIdentity;
import ${감지된패키지명}.example.ExampleWriter;
import ${감지된패키지명}.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Example 도메인 변경 서비스 구현체
 *
 * CQRS 패턴의 Command 책임을 구현합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultExampleWriter implements ExampleWriter {

    private final ExampleRepository exampleRepository;

    @Override
    public Example upsert(Example example) {
        log.info("Upserting example: {}", example);
        return exampleRepository.save(example);
    }

    @Override
    public void delete(ExampleIdentity identity) {
        log.info("Deleting example: {}", identity);
        exampleRepository.deleteById(identity);
    }
}
```


### 5. 검증
- `./gradlew :${감지된루트모듈}:service:build` 실행하여 컴파일 확인
- 생성된 파일 구조 출력

## 예상 실행 결과
```
corehr/
└── service/
    ├── build.gradle.kts
    └── src/main/java/com/searchkim/
        └── example/
            ├── ExampleReader.java (조회 인터페이스)
            ├── ExampleWriter.java (변경 인터페이스)
            └── impl/
                ├── DefaultExampleReader.java (조회 구현체)
                └── DefaultExampleWriter.java (변경 구현체)
```

## 주요 특징
- **CQRS 패턴** 적용 (Command Query Responsibility Segregation)
- **Reader/Writer 분리** - 조회와 변경 책임 분리
- **인터페이스-구현체 분리** 패턴으로 단위 테스트 용이성 확보
- **@Service + @RequiredArgsConstructor + @Slf4j** 적용
- **로깅** 포함 - 모든 작업에 대한 로그 기록
- **Model, Exception, Infrastructure** 의존성
- **upsert 패턴** - 생성/수정 통합
- **Mock 테스트** 가능한 구조