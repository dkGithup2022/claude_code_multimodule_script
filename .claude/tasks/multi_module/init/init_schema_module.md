# Schema Module 초기화 스크립트 (Java 21)

## 사용법
```bash
"init_schema_module.md 실행해줘"
```

## 실행 단계

### 1. 프로젝트 정보 자동 감지
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출
- 사용자 확인: "searchkim 모듈에 schema 모듈을 생성하겠습니다. 맞나요?"

### 2. settings.gradle.kts 업데이트
```kotlin
include(":${감지된루트모듈}:schema")
```

### 3. 모듈 디렉토리 구조 생성
```
${감지된루트모듈}/
└── schema/
    ├── build.gradle.kts
    └── src/main/resources/
        ├── schema.sql
        └── data.sql
```

### 4. 파일 생성

#### 4-1. ${감지된루트모듈}/schema/build.gradle.kts
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

// Schema 모듈은 순수 리소스만 포함하므로 별도 의존성 불필요
```

#### 4-2. schema.sql
```sql
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

-- Examples 테이블 생성 (H2용 - 대문자 테이블명 사용)
CREATE TABLE IF NOT EXISTS EXAMPLES (
    EXAMPLE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 4-3. data.sql
```sql
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

-- 초기 테스트 데이터
INSERT INTO EXAMPLES (EXAMPLE_ID, NAME, CREATED_AT, UPDATED_AT) VALUES
(1, 'First Example', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Second Example', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Third Example', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

### 5. Application-API 모듈 의존성 추가
- application-api/build.gradle.kts에 schema 모듈 의존성 추가
```kotlin
dependencies {
    // ... 기존 의존성들
    implementation(project(":${감지된루트모듈}:schema"))  // DDL/DML 스크립트
}
```

### 6. application.yml 확인
- `spring.sql.init.mode: embedded` 설정 확인
- JPA 설정 제거 확인

### 7. 검증
- `./gradlew :${감지된루트모듈}:schema:build` 실행하여 컴파일 확인
- `./gradlew :${감지된루트모듈}:application-api:build` 실행하여 통합 확인
- 생성된 파일 구조 출력

## 예상 실행 결과
```
searchkim/
└── schema/
    ├── build.gradle.kts
    └── src/main/resources/
        ├── schema.sql
        └── data.sql
```

## 주요 특징
- **순수 리소스 모듈**: Java 코드 없이 SQL 스크립트만 포함
- **Spring Boot 자동 실행**: application-api 실행시 schema.sql, data.sql 자동 실행
- **H2 호환**: 개발환경에 최적화된 DDL/DML
- **대문자 테이블명**: H2 데이터베이스 규약 준수
- **완전한 DDL 분리**: 데이터베이스 스키마 관리의 독립성 보장