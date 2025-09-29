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
        │   ├── example/repository/
        │   │   ├── ExampleEntity.java
        │   │   └── ExampleJdbcRepository.java
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

#### 4-2. ExampleEntity.java
```java
package ${감지된패키지명}.example.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("examples")
public class ExampleEntity {
    @Id
    Long exampleId;
    String name;
    Instant createdAt;
    Instant updatedAt;
}
```

#### 4-3. ExampleJdbcRepository.java
```java
package ${감지된패키지명}.example.repository;

import ${감지된패키지명}.example.Example;
import ${감지된패키지명}.example.ExampleIdentity;
import ${감지된패키지명}.example.repository.ExampleRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class ExampleJdbcRepository implements ExampleRepository {

    private final ExampleEntityRepository entityRepository;

    public ExampleJdbcRepository(ExampleEntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

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
    public Example save(Example example) {
        ExampleEntity entity = toEntity(example);
        ExampleEntity saved = entityRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(ExampleIdentity identity) {
        entityRepository.deleteById(identity.getExampleId());
    }

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

    interface ExampleEntityRepository extends CrudRepository<ExampleEntity, Long> {
    }
}
```


#### 4-5. AutoConfiguration.imports
```
${감지된패키지명}.example.repository.ExampleJdbcRepository
```

### 5. 검증
- `./gradlew :${감지된루트모듈}:repository-jdbc:build` 실행하여 컴파일 확인
- 생성된 파일 구조 출력

## 예상 실행 결과
```
corehr/
└── repository-jdbc/
    ├── build.gradle.kts
    └── src/main/
        ├── java/com/searchkim/
        │   ├── example/repository/
        │   │   ├── ExampleEntity.java
        │   │   └── ExampleJdbcRepository.java
        └── resources/META-INF/spring/
            └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## 주요 특징
- **Spring Data JDBC** 사용
- **Entity-Domain** 변환 로직
- **Repository Pattern** 구현
- **Auto Configuration** 지원