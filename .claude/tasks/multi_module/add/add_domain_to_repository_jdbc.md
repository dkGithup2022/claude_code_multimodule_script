# Repository-JDBC Module에 JDBC 구현체 추가 스크립트 (대화형 + Model 스펙 감지 + 2-depth 지원)

## 사용법
```bash
"add_domain_to_repository_jdbc.md 실행해줘"
```

## 대화형 입력 프로세스

### 1. 프로젝트 정보 자동 감지 및 확인
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출
- "감지된 설정: 루트모듈=${루트모듈}, 패키지=${패키지} 맞나요? (Y/n)"

### 2. 도메인명 입력 받기
- "추가할 도메인명을 입력해주세요 (예: User, Product, Order):"
- 입력값 검증:
  - PascalCase 형식 확인
  - 영문자만 포함 확인
  - 첫 글자 대문자 확인
- 유효하지 않으면 다시 입력 요청

### 3. Model 클래스 자동 감지 및 검증
- "${루트모듈}/model/src/main/java/${패키지경로}/${도메인명소문자}/${도메인명}.java" 경로에서 Model 클래스 검색
- **Case 1: 정확한 위치에 클래스 발견**
  ```
  ✓ Model 클래스를 발견했습니다:
     경로: ${루트모듈}/model/src/main/java/${패키지경로}/${도메인명소문자}/${도메인명}.java

     발견된 필드들:
     - ${필드명1}: ${필드타입1}
     - ${필드명2}: ${필드타입2}
     - ${필드명3}: ${필드타입3}
     ...

     이 클래스 스펙을 사용하시겠습니까? (Y/n)
  ```

- **Case 2: 다른 위치에 클래스 발견**
  ```
  ❌ 예상 위치에서 ${도메인명} 클래스를 찾을 수 없습니다.
     예상 경로: ${루트모듈}/model/src/main/java/${패키지경로}/${도메인명소문자}/${도메인명}.java

     하지만 다른 위치에서 유사한 클래스들을 발견했습니다:
     1. ${경로1}/${클래스명1}.java
     2. ${경로2}/${클래스명2}.java

     위 중 하나를 선택하시겠습니까? (1, 2, or 직접입력을 위해 'c')
  ```

- **Case 3: 클래스를 전혀 찾을 수 없음**
  ```
  ❌ ${도메인명} 관련 Model 클래스를 찾을 수 없습니다.

     다음 중 하나를 선택해주세요:
     1. 정확한 클래스명을 직접 입력 (예: UserProfile, OrderItem)
     2. 기본 템플릿으로 진행 (Long id, String name, String description, Instant createdAt, Instant updatedAt)

     선택 (1/2):
  ```

- **클래스명 직접 입력 대화형** (사용자가 '1' 또는 'c' 선택시)
  ```
  정확한 클래스명을 입력해주세요:
  → 사용자 입력: "UserProfile"

  입력하신 클래스명으로 검색 중: UserProfile

  ✓ 클래스를 발견했습니다:
     경로: ${루트모듈}/model/src/main/java/${패키지경로}/user/UserProfile.java

     발견된 필드들:
     - profileId: Long
     - userId: Long
     - avatar: String
     - bio: String
     - createdAt: Instant
     - updatedAt: Instant

     이 클래스를 사용하시겠습니까? (Y/n)
  ```

### 4. 하위 엔티티명 입력 받기
- "하위 엔티티명을 입력해주세요 (비어두면 ${도메인명} 자체의 JDBC Repository 생성):"
- "예: Profile, Setting, Address (여러 개는 쉼표로 구분)"
- **입력 옵션:**
  - **빈 입력** → `${도메인명}RepositoryImpl` 생성
  - **단일 입력** → `${도메인명}${엔티티명}RepositoryImpl` 생성
  - **다중 입력** → 각각에 대해 `${도메인명}${엔티티명}RepositoryImpl` 생성

### 5. Infrastructure Repository 인터페이스 확인 및 메서드 분석
- "${루트모듈}/infrastructure/src/main/java/${패키지경로}/${도메인명소문자}/repository/" 디렉토리 확인
- 대응하는 Repository 인터페이스 존재 확인
- 존재하지 않으면 안내: "먼저 add_domain_to_infrastructure.md로 Repository 인터페이스를 생성해주세요."
- **Infrastructure Repository 메서드 분석**:
  - findById(${도메인명}Identity) → EntityRepository.findById(Long) 매핑
  - findAll() → EntityRepository.findAll() 매핑
  - findByXxx(타입) → EntityRepository.findByXxx(타입) 매핑
  - save(${도메인명}) → EntityRepository.save(${도메인명}Entity) 매핑
  - deleteById(${도메인명}Identity) → EntityRepository.deleteById(Long) 매핑
  - 기타 메서드들은 Spring Data의 named query 규칙에 따라 자동 매핑

### 6. 생성할 JDBC Repository 목록 확인 및 승인
```
도메인명: ${입력받은도메인명}
하위 엔티티: ${입력받은엔티티목록}
Model 스펙: ${감지된_Model_필드들}

생성될 JDBC Repository 구현체:
${엔티티목록이_비어있으면:}
  ✓ ${도메인명}Entity (Spring Data JDBC, Model 스펙 기반)
  ✓ ${도메인명}EntityRepository (Spring Data Repository)
  ✓ ${도메인명}RepositoryImpl (implements ${도메인명}Repository)
${엔티티목록이_있으면:}
  ✓ ${도메인명}${엔티티1}Entity (Spring Data JDBC, Model 스펙 기반)
  ✓ ${도메인명}${엔티티1}EntityRepository (Spring Data Repository)
  ✓ ${도메인명}${엔티티1}RepositoryImpl (implements ${도메인명}${엔티티1}Repository)
  ...

패키지: ${감지된패키지}.${도메인명소문자}.repository
경로: ${루트모듈}/repository-jdbc/src/main/java/${패키지경로}/${도메인명소문자}/repository/

위 내용으로 진행하시겠습니까? (Y/n)
```

### 7. 디렉토리 구조 생성 (사용자 승인 후)
```
${루트모듈}/repository-jdbc/src/main/java/${패키지경로}/
└── ${도메인명소문자}/
    └── repository/
        ${각_Repository별로_생성}
        ├── ${도메인명}Entity.java (빈 입력인 경우)
        ├── ${도메인명}EntityRepository.java
        ├── ${도메인명}RepositoryImpl.java
        └── ... (하위 엔티티인 경우 반복)
```

### 8. 파일 생성 (각 Repository별로 반복)

#### 8-1. Entity 클래스 템플릿 (${엔티티클래스명}Entity.java)
```java
package ${패키지명}.${도메인명소문자}.repository;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * ${엔티티클래스명} Spring Data JDBC Entity
 *
 * Model 클래스 스펙을 기반으로 생성된 데이터베이스 매핑용 엔티티
 */
@Table("${테이블명_대문자_복수}")
@Getter
@AllArgsConstructor
public class ${엔티티클래스명}Entity {

    @Id
    @Generated
    private Long id;

    // Model 클래스에서 추출한 필드들 (ID 필드 제외, Instant 그대로 사용)
    ${Model클래스의_필드들_매핑}

    public static ${엔티티클래스명}Entity newOne(${비즈니스_필드_파라미터들}) {
        return new ${엔티티클래스명}Entity(null, ${비즈니스_필드들}, Instant.now(), Instant.now());
    }
}
```

**Model 필드 매핑 예시:**
- Model: `private final Long exampleId;` → Entity: 제외 (ID는 자동생성)
- Model: `private final String name;` → Entity: `private String name;`
- Model: `private final Instant createdAt;` → Entity: `private Instant createdAt;`
- Model: `private final Instant updatedAt;` → Entity: `private Instant updatedAt;`

**기본 템플릿 (Model을 찾을 수 없는 경우):**
```java
@Table("${엔티티명_대문자_복수}")
@Getter
@AllArgsConstructor
public class ${엔티티클래스명}Entity {

    @Id
    @Generated
    private Long id;

    // 기본 템플릿 필드들
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    public static ${엔티티클래스명}Entity newOne(String name, String description) {
        return new ${엔티티클래스명}Entity(null, name, description, Instant.now(), Instant.now());
    }
}
```

#### 8-2. EntityRepository 인터페이스 템플릿 (${엔티티클래스명}EntityRepository.java)
```java
package ${패키지명}.${도메인명소문자}.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ${엔티티클래스명} Entity CRUD API 인터페이스
 *
 * Spring Data JDBC를 활용한 ${엔티티클래스명}Entity 데이터 접근 계층
 * Infrastructure Repository 인터페이스 기반으로 필요한 메서드만 생성
 */
@Repository
public interface ${엔티티클래스명}EntityRepository extends CrudRepository<${엔티티클래스명}Entity, Long> {

    // Infrastructure Repository 인터페이스에서 감지된 메서드들만 생성
    ${Infrastructure_Repository_기반_메서드들}
}
```

#### 8-3. Repository 구현체 템플릿 (${엔티티클래스명}RepositoryImpl.java)
```java
package ${패키지명}.${도메인명소문자}.repository;

import ${패키지명}.${도메인명소문자}.${엔티티클래스명};
import ${패키지명}.${도메인명소문자}.${엔티티클래스명}Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * ${엔티티클래스명} Repository 구현체
 *
 * 헥사고날 아키텍처에서 Adapter 역할을 수행하며,
 * Infrastructure의 ${엔티티클래스명}Repository 인터페이스를
 * Spring Data JDBC를 활용하여 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class ${엔티티클래스명}RepositoryImpl implements com.${패키지명}.${도메인명소문자}.repository.${엔티티클래스명}Repository {

    private final ${엔티티클래스명}EntityRepository entityRepository;

    // Infrastructure Repository 인터페이스의 모든 메서드를 동적으로 구현
    ${Infrastructure_Repository_기반_모든_메서드_구현}

    /**
     * Entity ↔ Domain 변환 메서드 (Model 스펙 기반 자동 생성)
     */
    private ${엔티티클래스명} toDomain(${엔티티클래스명}Entity entity) {
        return new ${엔티티클래스명}(
                entity.getId(),
                ${Model_필드_매핑_Entity_to_Domain}
        );
    }

    private ${엔티티클래스명}Entity toEntity(${엔티티클래스명} domain) {
        return new ${엔티티클래스명}Entity(
                domain.get${엔티티클래스명}Id(),
                ${Model_필드_매핑_Domain_to_Entity}
        );
    }
}
```

**변환 메서드 예시 (Model 필드 기반 자동 생성):**
- Model 필드들은 Entity ↔ Domain 간 동일한 타입으로 직접 매핑
- Instant 타입은 변환 없이 그대로 전달

### 9. 검증 및 결과 확인
- `./gradlew :${루트모듈}:repository-jdbc:build` 실행하여 컴파일 확인
- 생성된 파일 구조 출력
- "성공적으로 Spring Data JDBC Repository 구현체들이 생성되었습니다!"

## 대화형 시나리오 예시

### 시나리오 1: Model 스펙 기반 단일 도메인 JDBC Repository 생성
```
사용자: "add_domain_to_repository_jdbc.md 실행해줘"

봇: "프로젝트 정보를 감지했습니다.
     루트모듈: searchkim
     패키지: com.searchkim.multimodule
     위 설정이 맞나요? (Y/n)"

사용자: "Y"

봇: "추가할 도메인명을 입력해주세요 (예: User, Product, Order):"

사용자: "User"

봇: "Model 모듈에서 User 클래스를 검색 중...

     ✓ Model 클래스를 발견했습니다:
        경로: searchkim/model/src/main/java/com/searchkim/multimodule/user/User.java

        발견된 필드들:
        - userId: Long (ID 필드로 인식, Entity에서는 자동생성 적용)
        - name: String
        - email: String
        - phoneNumber: String
        - createdAt: Instant (변환 불필요)
        - updatedAt: Instant (변환 불필요)

        이 클래스 스펙을 사용하시겠습니까? (Y/n)"

사용자: "Y"

봇: "하위 엔티티명을 입력해주세요 (비어두면 User 자체의 JDBC Repository 생성):
     예: Profile, Setting, Address (여러 개는 쉼표로 구분)"

사용자: "" (빈 입력)

봇: "Infrastructure Repository 인터페이스를 확인했습니다. ✓
     - UserRepository 존재

     입력하신 정보를 확인해주세요:
     도메인명: User
     하위 엔티티: (없음)
     Model 스펙: name, email, phoneNumber, createdAt, updatedAt

     생성될 JDBC Repository 구현체:
     ✓ UserEntity (Spring Data JDBC, Model 스펙 기반)
     ✓ UserEntityRepository (Spring Data Repository)
     ✓ UserRepositoryImpl (implements UserRepository)

     패키지: com.searchkim.multimodule.user.repository
     경로: searchkim/repository-jdbc/src/main/java/com/searchkim/multimodule/user/repository/

     위 내용으로 진행하시겠습니까? (Y/n)"

사용자: "Y"

봇: [파일 생성 및 빌드 수행]
    "성공적으로 User JDBC Repository 구현체가 생성되었습니다!
     ✓ UserEntity (Model 스펙 기반)
     ✓ UserEntityRepository
     ✓ UserRepositoryImpl"
```

### 시나리오 2: Model 클래스 직접 입력 시나리오
```
사용자: "add_domain_to_repository_jdbc.md 실행해줘"

사용자: "Product"

봇: "Model 모듈에서 Product 클래스를 검색 중...

     ❌ 예상 위치에서 Product 클래스를 찾을 수 없습니다.
        예상 경로: searchkim/model/src/main/java/com/searchkim/multimodule/product/Product.java

        다음 중 하나를 선택해주세요:
        1. 정확한 클래스명을 직접 입력 (예: ProductInfo, ProductDetail)
        2. 기본 템플릿으로 진행 (Long id, String name, String description, Instant createdAt, Instant updatedAt)

        선택 (1/2):"

사용자: "1"

봇: "정확한 클래스명을 입력해주세요:"

사용자: "ProductInfo"

봇: "입력하신 클래스명으로 검색 중: ProductInfo

     ✓ 클래스를 발견했습니다:
        경로: searchkim/model/src/main/java/com/searchkim/multimodule/product/ProductInfo.java

        발견된 필드들:
        - productId: Long
        - productName: String
        - category: String
        - price: BigDecimal
        - createdAt: Instant
        - updatedAt: Instant

        이 클래스를 사용하시겠습니까? (Y/n)"

사용자: "Y"

봇: [나머지 프로세스 진행...]
    "성공적으로 ProductInfo JDBC Repository 구현체가 생성되었습니다!"
```

## 예상 실행 결과 예시

### 단일 도메인인 경우 (User, Model 스펙 기반)
```
searchkim/repository-jdbc/src/main/java/com/searchkim/multimodule/
└── user/
    └── repository/
        ├── UserEntity.java (Spring Data JDBC Entity, Model 스펙 기반)
        ├── UserEntityRepository.java (Spring Data Repository 인터페이스)
        └── UserRepositoryImpl.java (Infrastructure Repository 구현체)
```

### 하위 엔티티인 경우 (User - Profile, Setting)
```
searchkim/repository-jdbc/src/main/java/com/searchkim/multimodule/
└── user/
    └── repository/
        ├── UserProfileEntity.java
        ├── UserProfileEntityRepository.java
        ├── UserProfileRepositoryImpl.java
        ├── UserSettingEntity.java
        ├── UserSettingEntityRepository.java
        └── UserSettingRepositoryImpl.java
```

## 주요 특징
- **Model 스펙 자동 감지** - Model 클래스의 실제 필드를 분석하여 Entity 생성
- **대화형 클래스 검증** - Model 클래스 위치 확인 및 직접 입력 지원
- **Spring Data JDBC** - Named Query 자동생성 및 CrudRepository 활용
- **ID 자동생성** - @Id @Generated 어노테이션으로 자동 ID 생성
- **Instant** - 시간 타입을 Instant로 통일
- **유연한 하위 엔티티** 지원 (단일/다중/빈 입력)
- **의존성 검증** (Infrastructure Repository 인터페이스 존재 확인)
- **Entity ↔ Domain 변환** - 자동 매핑 및 타입 변환 지원
- **Lombok 활용** - @Getter, @AllArgsConstructor, @RequiredArgsConstructor
- **헥사고날 아키텍처** Adapter 역할

## 생성되는 파일별 역할
- **Entity**: Spring Data JDBC Entity (Model 스펙 기반, @Table, @Id @Generated)
- **EntityRepository**: Spring Data Repository 인터페이스 (Named Query 자동생성)
- **RepositoryImpl**: Infrastructure Repository 구현체 (EntityRepository 주입)

## Model 필드 매핑 규칙
- **ID 필드**: `*Id` 패턴의 Long 타입 → Entity에서 제외 (자동생성)
- **Instant 타입**: `Instant` → `Instant` (변환 불필요)
- **기타 타입**: 동일하게 매핑 (String, BigDecimal, Boolean 등)
- **final 키워드**: 제거 (Entity는 mutable)