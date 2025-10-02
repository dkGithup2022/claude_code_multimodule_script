# Model Module에 도메인 추가 스크립트

## 사용법
```bash
"run add_domain_to_model.md 도메인명: Foo"
```

## 필수 입력 파라미터
- **도메인명**: Foo (PascalCase, 영문)

## 실행 단계

### 1. 프로젝트 정보 자동 감지
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출
- 도메인명 검증 (PascalCase, 영문자만)

### 2. 도메인명 변환
```
입력: Foo
- 클래스명: Foo, FooModel, FooIdentity
- 패키지명: foo (소문자)
- 변수명: foo, fooId
```

### 3. 디렉토리 구조 생성
```
${감지된루트모듈}/model/src/main/java/${감지된패키지경로}/
└── ${도메인명소문자}/
    ├── ${도메인명}Identity.java
    ├── ${도메인명}Model.java
    └── ${도메인명}.java
```

### 4. 파일 생성

#### 4-1. ${도메인명}Identity.java
```java
package ${감지된패키지명}.${도메인명소문자};

import lombok.Value;

@Value
public class ${도메인명}Identity {
    Long ${도메인명소문자}Id;
}
```

#### 4-2. ${도메인명}Model.java
```java
package ${감지된패키지명}.${도메인명소문자};

import ${감지된패키지명}.AuditProps;

public interface ${도메인명}Model extends AuditProps {
    Long get${도메인명}Id();
    String getName();
    String getDescription();
}
```

#### 4-3. ${도메인명}.java
```java
package ${감지된패키지명}.${도메인명소문자};

import lombok.Value;
import java.time.Instant;

@Value
public class ${도메인명} implements ${도메인명}Model {
    Long ${도메인명소문자}Id;
    String name;
    String description;
    Instant createdAt;
    Instant updatedAt;

    public static ${도메인명} newOne(String name, String description) {
        return new ${도메인명}(null, name, description, Instant.now(), Instant.now());
    }
}
```

### 5. 검증 및 스펙 작성 안내
- `./gradlew :${감지된루트모듈}:model:build` 실행하여 컴파일 확인
- 생성된 파일 구조 및 스펙 출력
- 스펙 작성 안내 메시지 출력

#### 5-1. 생성된 파일 위치 및 스펙 출력
```
✓ ${도메인명} 도메인 모델이 성공적으로 생성되었습니다!

📁 생성된 파일 위치:
   ${루트모듈}/model/src/main/java/${패키지경로}/${도메인명소문자}/
   ├── ${도메인명}Identity.java
   ├── ${도메인명}Model.java
   └── ${도메인명}.java

📋 현재 기본 스펙:
   ${도메인명}Identity:
   - ${도메인명소문자}Id: Long

   ${도메인명}Model (인터페이스):
   - get${도메인명}Id(): Long
   - getName(): String
   - getDescription(): String
   - getCreatedAt(): Instant (AuditProps)
   - getUpdatedAt(): Instant (AuditProps)

   ${도메인명} (구현체):
   - ${도메인명소문자}Id: Long
   - name: String
   - description: String
   - createdAt: Instant
   - updatedAt: Instant

🔧 기본 스펙의 도메인이 만들어졌습니다. 파일의 스펙을 작성하세요.

💡 스펙 작성 가이드:
   1. ${도메인명}Model.java: 도메인 인터페이스에 필요한 getter 메서드 추가
   2. ${도메인명}.java: 구현체에 실제 필드 추가 (@Value로 불변 객체)
   3. ${도메인명}Identity.java: 필요한 경우 복합키 구조로 변경 가능

📝 예시 (User 도메인):
   UserModel에 추가할 수 있는 메서드:
   - getEmail(): String
   - getPhoneNumber(): String
   - getRole(): UserRole
   - isActive(): Boolean

   User 구현체에 추가할 수 있는 필드:
   - email: String
   - phoneNumber: String
   - role: UserRole
   - active: Boolean
```

## 변환 예시
```
도메인명: User
→ 패키지: user/
→ 클래스: User, UserModel, UserIdentity
→ 변수: userId, user

도메인명: ProductCategory
→ 패키지: productcategory/
→ 클래스: ProductCategory, ProductCategoryModel, ProductCategoryIdentity
→ 변수: productCategoryId, productCategory
```

## 예상 실행 결과 (도메인명: Foo)
```
corehr/model/src/main/java/com/searchkim/
├── AuditProps.java (기존)
├── example/ (기존)
└── foo/
    ├── FooIdentity.java
    ├── FooModel.java
    └── Foo.java
```

## 주요 특징
- **파라미터 기반** 동적 생성
- **명명 규칙** 자동 적용 (PascalCase → camelCase → lowercase)
- **템플릿 치환** 방식
- **도메인 중심** 설계
- **AuditProps** 상속
- **Lombok @Value** 사용으로 불변 객체 패턴 자동 적용
- **Boilerplate 코드 자동 생성** (getter/equals/hashCode/toString)