# Model Module 초기화 스크립트 (자동 감지)

## 사용법
```bash
"init_model_module.md 실행해줘"
```

## 실행 단계

### 1. 루트 모듈명 자동 감지

#### 1-1. settings.gradle.kts 파일 읽기 및 분석
- 기존 include 문 패턴 분석 (예: `include(":corehr:api")`)
- 루트 모듈명 추출 (예: "corehr")
- 감지된 루트 모듈명 사용자에게 확인
- "기존 모듈 구조를 감지했습니다: corehr. 맞나요?"

#### 1-2. 패키지명 감지
- build.gradle.kts에서 group 정보 추출
- 예: `group = "com.searchkim"` → "com.searchkim"

### 2. settings.gradle.kts 업데이트
```kotlin
// 기존 include 문들 사이에 추가
include(":${감지된루트모듈}:model")
```

### 3. 모듈 디렉토리 구조 생성

```
${감지된루트모듈}/
└── model/
    ├── build.gradle.kts
    └── src/
        └── main/
            └── java/
                └── ${감지된패키지경로}/
                    ├── AuditProps.java
                    └── example/
                        ├── Example.java
                        ├── ExampleModel.java
                        └── ExampleIdentity.java
```

### 4. 파일 생성

#### 4-1. ${감지된루트모듈}/model/build.gradle.kts
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

dependencies {
    // Model module has no external dependencies
}
```

#### 4-2. AuditProps.java
```java
package ${감지된패키지명}.model;

import java.time.Instant;

public interface AuditProps {
    Instant getCreatedAt();
    Instant getUpdatedAt();
}
```

#### 4-3. ExampleIdentity.java
```java
package ${감지된패키지명}.model.example;

import lombok.Value;

@Value
public class ExampleIdentity {
    Long exampleId;
}
```

#### 4-4. ExampleModel.java
```java
package ${감지된패키지명}.model.example;

import ${감지된패키지명}.AuditProps;

public interface ExampleModel extends AuditProps {
    Long getExampleId();
    String getName();
}
```

#### 4-5. Example.java
```java
package ${감지된패키지명}.model.example;

import lombok.Value;
import java.time.Instant;

@Value
public class Example implements ExampleModel {
    Long exampleId;
    String name;
    Instant createdAt;
    Instant updatedAt;
}
```

### 5. 검증 및 스펙 작성 안내
- `./gradlew :${감지된루트모듈}:model:build` 실행하여 컴파일 확인
- 생성된 파일 구조 및 스펙 출력
- 스펙 작성 안내 메시지 출력

#### 5-1. 생성된 파일 위치 및 스펙 출력
```
✓ Model 모듈이 성공적으로 초기화되었습니다!

📁 생성된 파일 위치:
   ${루트모듈}/model/src/main/java/${패키지경로}/
   ├── AuditProps.java (공통 감사 인터페이스)
   └── example/
       ├── ExampleIdentity.java
       ├── ExampleModel.java
       └── Example.java

📋 현재 기본 스펙:
   AuditProps (공통 인터페이스):
   - getCreatedAt(): Instant
   - getUpdatedAt(): Instant

   ExampleIdentity:
   - exampleId: Long

   ExampleModel (인터페이스):
   - getExampleId(): Long
   - getName(): String
   - getCreatedAt(): Instant (AuditProps)
   - getUpdatedAt(): Instant (AuditProps)

   Example (구현체):
   - exampleId: Long
   - name: String
   - createdAt: Instant
   - updatedAt: Instant

🔧 기본 스펙의 도메인이 만들어졌습니다. 파일의 스펙을 작성하세요.

💡 스펙 작성 가이드:
   1. ExampleModel.java: 도메인 인터페이스에 필요한 getter 메서드 추가
   2. Example.java: 구현체에 실제 필드 추가 (@Value로 불변 객체)
   3. 새로운 도메인 추가시: add_domain_to_model.md 사용

📝 Example 도메인 확장 예시:
   ExampleModel에 추가할 수 있는 메서드:
   - getDescription(): String
   - getCategory(): String
   - getStatus(): ExampleStatus
   - isActive(): Boolean

   Example 구현체에 추가할 수 있는 필드:
   - description: String
   - category: String
   - status: ExampleStatus
   - active: Boolean

🚀 다음 단계:
   1. Example 도메인 스펙을 실제 요구사항에 맞게 수정
   2. 새로운 도메인 추가: "add_domain_to_model.md 실행해줘. 도메인명: [도메인명]"
   3. Infrastructure 모듈 생성: "init_infrastructure_module.md 실행해줘"
```

## 자동 감지 로직
1. **settings.gradle.kts** 읽기 → 루트 모듈명 추출
2. **build.gradle.kts** 읽기 → 패키지명 추출
3. **사용자 확인** → "corehr 모듈에 com.searchkim 패키지로 생성하겠습니다. 맞나요?"
4. **모듈 생성**

## 예상 실행 결과
```
corehr/
└── model/
    ├── build.gradle.kts
    └── src/main/java/com/searchkim/
        ├── AuditProps.java
        └── example/
            ├── Example.java
            ├── ExampleModel.java
            └── ExampleIdentity.java
```