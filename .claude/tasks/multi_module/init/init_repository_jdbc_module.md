# Repository JDBC Module 초기화 스크립트 (Java 21)

## 사용법
```bash
"init_repository_jdbc_module.md 실행해줘"
```

## 실행 단계

### 1. 프로젝트 정보 자동 감지
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출

### 2. settings.gradle.kts 업데이트
```kotlin
include(":${감지된루트모듈}:repository-jdbc")
```

### 3. 모듈 디렉토리 구조 생성
```
${감지된루트모듈}/
└── repository-jdbc/
    ├── build.gradle.kts
    └── src/main/
        ├── java/${감지된패키지경로}/
        │   ├── config/
        │   │   └── JdbcRepositoryAutoConfiguration.java
        │   └── example/repository/
        │       ├── ExampleEntity.java
        │       ├── ExampleEntityRepository.java
        │       └── ExampleJdbcRepository.java
        └── resources/META-INF/spring/
            └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

### 4. 파일 생성

#### 4-1. ${감지된루트모듈}/repository-jdbc/build.gradle.kts
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":${감지된루트모듈}:model"))
    implementation(project(":${감지된루트모듈}:exception"))
    implementation(project(":${감지된루트모듈}:infrastructure"))

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

#### 4-2. JdbcRepositoryAutoConfiguration.java
```java
package ${감지된패키지명}.jdbc.config;

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
```

#### 4-3. ExampleEntity.java
```java
package ${감지된패키지명}.jdbc.example.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Example Spring Data JDBC Entity
 *
 * Model 클래스 스펙을 기반으로 생성된 데이터베이스 매핑용 엔티티
 */
@Table("examples")
@Getter
@AllArgsConstructor
public class ExampleEntity {
    @Id
    private Long exampleId;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
```

#### 4-4. ExampleEntityRepository.java
```java
package ${감지된패키지명}.jdbc.example.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Example Entity CRUD API 인터페이스
 *
 * Spring Data JDBC를 활용한 ExampleEntity 데이터 접근 계층
 * Infrastructure Repository 인터페이스 기반으로 필요한 메서드만 생성
 */
@Repository
public interface ExampleEntityRepository extends CrudRepository<ExampleEntity, Long> {
    List<ExampleEntity> findByName(String name);
}
```

#### 4-5. ExampleJdbcRepository.java
```java
package ${감지된패키지명}.jdbc.example.repository;

import ${감지된패키지명}.model.example.Example;
import ${감지된패키지명}.model.example.ExampleIdentity;
import ${감지된패키지명}.infrastructure.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Example Repository 구현체
 *
 * 헥사고날 아키텍처에서 Adapter 역할을 수행하며,
 * Infrastructure의 ExampleRepository 인터페이스를
 * Spring Data JDBC를 활용하여 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class ExampleJdbcRepository implements ExampleRepository {

    private final ExampleEntityRepository entityRepository;

    @Override
    public Optional<Example> findById(ExampleIdentity identity) {
        return entityRepository.findById(identity.getExampleId())
                .map(this::toDomain);
    }

    @Override
    public List<Example> findAll() {
        return StreamSupport.stream(entityRepository.findAll().spliterator(), false)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Example> findByName(String name) {
        return entityRepository.findByName(name).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Example save(Example example) {
        ExampleEntity entity = toEntity(example);
        ExampleEntity saved = entityRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(ExampleIdentity identity) {
        entityRepository.deleteById(identity.getExampleId());
    }

    @Override
    public boolean existsById(ExampleIdentity identity) {
        return entityRepository.existsById(identity.getExampleId());
    }

    @Override
    public long count() {
        return entityRepository.count();
    }

    /**
     * Entity ↔ Domain 변환 메서드 (Model 스펙 기반 자동 생성)
     */
    private Example toDomain(ExampleEntity entity) {
        return new Example(
                entity.getExampleId(),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ExampleEntity toEntity(Example domain) {
        return new ExampleEntity(
                domain.getExampleId(),
                domain.getName(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
```

#### 4-6. AutoConfiguration.imports
```
${감지된패키지명}.jdbc.config.JdbcRepositoryAutoConfiguration
```

### 5. 검증
- `./gradlew :${감지된루트모듈}:repository-jdbc:build` 실행하여 컴파일 확인
- 생성된 파일 구조 출력

## 예상 실행 결과
```
modules/
└── repository-jdbc/
    ├── build.gradle.kts
    └── src/main/
        ├── java/io/dkGithup2022/hello/
        │   ├── config/
        │   │   └── JdbcRepositoryAutoConfiguration.java
        │   └── example/repository/
        │       ├── ExampleEntity.java
        │       ├── ExampleEntityRepository.java
        │       └── ExampleJdbcRepository.java
        └── resources/META-INF/spring/
            └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## 주요 특징
- **Spring Data JDBC** 사용
- **별도 EntityRepository 인터페이스** - Inner interface 문제 해결
- **Auto Configuration** - 헥사고날 아키텍처 준수
- **Entity-Domain** 변환 로직
- **Repository Pattern** 구현
- **@RequiredArgsConstructor** 사용으로 코드 간소화

## 🔧 **기존 버전과의 차이점**
### ❌ 기존 문제점:
1. **Inner Interface**: Spring Data JDBC가 스캔하지 못함
2. **잘못된 AutoConfiguration**: Repository 구현체를 직접 등록
3. **아키텍처 위반**: Application 모듈에서 JDBC 직접 참조 필요

### ✅ 수정된 버전:
1. **별도 EntityRepository**: Spring Data JDBC가 정상 스캔
2. **올바른 AutoConfiguration**: 설정 클래스만 등록
3. **헥사고날 아키텍처**: Application은 runtimeOnly 의존성만 사용