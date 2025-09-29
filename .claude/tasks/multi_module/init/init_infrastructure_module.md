# Infrastructure Module 초기화 스크립트 (Java 21)

## 사용법
```bash
"init_infrastructure_module.md 실행해줘"
```

## 실행 단계

### 1. 프로젝트 정보 자동 감지 및 확인
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출
- "감지된 설정: 루트모듈=${루트모듈}, 패키지=${패키지} 맞나요? (Y/n)"

### 2. Model 엔티티 확인 (대화형)
- Model 모듈에서 사용 가능한 엔티티 스캔
- "현재 Model 모듈에서 발견된 엔티티들:"
  ```
  ✓ Example (경로: ${루트모듈}/model/src/main/java/${패키지}/example/)
  ✓ 추가 엔티티들...
  ```
- "Example Repository를 생성하시겠습니까? 다른 엔티티 이름을 입력하시면 해당 엔티티로 생성합니다 (기본값: Example):"
- 사용자 입력 옵션:
  - **빈 입력 또는 'Example'**: Example 엔티티 사용
  - **다른 엔티티명**: 해당 엔티티로 Repository 생성 (예: User, Product)
- Model 클래스 존재 확인:
  - `${엔티티명}.java` 파일 존재 확인
  - `${엔티티명}Identity.java` 파일 존재 확인
  - 존재하지 않으면 안내: "먼저 add_domain_to_model.md로 ${엔티티명} 모델을 생성해주세요."

### 3. settings.gradle.kts 업데이트
```kotlin
include(":${감지된루트모듈}:infrastructure")
```

### 4. 모듈 디렉토리 구조 생성
```
${감지된루트모듈}/
└── infrastructure/
    ├── build.gradle.kts
    └── src/main/java/${감지된패키지경로}/
        └── ${선택된엔티티소문자}/repository/${선택된엔티티명}Repository.java
```

### 5. 파일 생성

#### 5-1. ${감지된루트모듈}/infrastructure/build.gradle.kts
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

dependencies {
    implementation(project(":${감지된루트모듈}:model"))
    implementation(project(":${감지된루트모듈}:exception"))
}
```

#### 5-2. ${선택된엔티티명}Repository.java (동적 생성)
```java
package ${감지된패키지명}.infrastructure.${선택된엔티티소문자}.repository;

import ${감지된패키지명}.model.${선택된엔티티소문자}.${선택된엔티티명};
import ${감지된패키지명}.model.${선택된엔티티소문자}.${선택된엔티티명}Identity;
import java.util.List;
import java.util.Optional;

/**
 * ${선택된엔티티명} Repository 인터페이스
 *
 * 헥사고날 아키텍처에서 Port 역할을 수행하며,
 * 비즈니스 로직에서 데이터 접근을 위한 인터페이스를 정의합니다.
 */
public interface ${선택된엔티티명}Repository {

    /**
     * ID로 ${선택된엔티티명} 조회
     *
     * @param identity ${선택된엔티티명} 식별자
     * @return ${선택된엔티티명} 엔티티 (존재하지 않으면 Optional.empty())
     */
    Optional<${선택된엔티티명}> findById(${선택된엔티티명}Identity identity);

    /**
     * 모든 ${선택된엔티티명} 조회
     *
     * @return ${선택된엔티티명} 목록
     */
    List<${선택된엔티티명}> findAll();

    /**
     * 이름으로 ${선택된엔티티명} 조회
     *
     * @param name 이름
     * @return ${선택된엔티티명} 목록
     */
    List<${선택된엔티티명}> findByName(String name);

    /**
     * ${선택된엔티티명} 저장 (생성/수정)
     *
     * @param ${선택된엔티티변수명} 저장할 ${선택된엔티티명}
     * @return 저장된 ${선택된엔티티명}
     */
    ${선택된엔티티명} save(${선택된엔티티명} ${선택된엔티티변수명});

    /**
     * ID로 ${선택된엔티티명} 삭제
     *
     * @param identity ${선택된엔티티명} 식별자
     */
    void deleteById(${선택된엔티티명}Identity identity);

    /**
     * ${선택된엔티티명} 존재 여부 확인
     *
     * @param identity ${선택된엔티티명} 식별자
     * @return 존재하면 true, 없으면 false
     */
    boolean existsById(${선택된엔티티명}Identity identity);

    /**
     * 총 ${선택된엔티티명} 개수 조회
     *
     * @return ${선택된엔티티명} 개수
     */
    long count();
}
```

### 6. 검증 및 완료 안내
- `./gradlew :${감지된루트모듈}:infrastructure:build` 실행하여 컴파일 확인
- 생성된 파일 구조 및 스펙 출력
- 완료 메시지 및 다음 단계 안내

#### 6-1. 생성된 파일 위치 및 스펙 출력
```
✓ Infrastructure 모듈이 성공적으로 초기화되었습니다!

📁 생성된 파일 위치:
   ${루트모듈}/infrastructure/src/main/java/${패키지경로}/
   └── ${선택된엔티티소문자}/
       └── repository/
           └── ${선택된엔티티명}Repository.java

📋 현재 기본 스펙:
   ${선택된엔티티명}Repository (인터페이스):
   - findById(${선택된엔티티명}Identity): Optional<${선택된엔티티명}>
   - findAll(): List<${선택된엔티티명}>
   - findByName(String): List<${선택된엔티티명}>
   - save(${선택된엔티티명}): ${선택된엔티티명}
   - deleteById(${선택된엔티티명}Identity): void
   - existsById(${선택된엔티티명}Identity): boolean
   - count(): long

🔧 기본값이 생성되었으니 확인하라.

💡 Repository 메서드 설명:
   - findById: 기본 조회 (Optional 반환)
   - findAll: 전체 조회
   - findByName: 이름 기준 검색
   - save: 생성/수정 (Upsert 패턴)
   - deleteById: 삭제
   - existsById: 존재 여부 확인
   - count: 총 개수 조회

📝 Repository 확장 예시:
   필요에 따라 다음과 같은 메서드를 추가할 수 있습니다:
   - findByStatus(Status status): 상태별 조회
   - findByCreatedAtBetween(Instant start, Instant end): 기간별 조회
   - findTop10ByOrderByCreatedAtDesc(): 최신 10개 조회
   - deleteByStatus(Status status): 상태별 삭제

🚀 다음 단계:
   1. ${선택된엔티티명}Repository 인터페이스를 실제 요구사항에 맞게 확장
   2. Exception 모듈 생성: "init_exception_module.md 실행해줘"
   3. Service 모듈 생성: "init_service_module.md 실행해줘"
   4. 추가 도메인 Repository: "add_domain_to_infrastructure.md 실행해줘"
```

## 예상 실행 결과
```
corehr/
└── infrastructure/
    ├── build.gradle.kts
    └── src/main/java/com/searchkim/
        ├── example/repository/ExampleRepository.java

```

## 주요 특징
- **Port 인터페이스** 정의
- **Model과 Exception** 의존성
- **Repository 패턴** 적용
- **각 도메인별** Repository 제공