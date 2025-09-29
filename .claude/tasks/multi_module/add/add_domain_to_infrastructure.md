# Infrastructure Module에 Repository 인터페이스 추가 스크립트 (대화형 + 유연한 하위 엔티티 지원)

## 사용법
```bash
"run add_domain_to_infrastructure.md"
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

### 2-1. Model 엔티티 존재 확인 (대화형)
- 입력받은 도메인의 Model 클래스 존재 확인:
  - `${도메인명}.java` 파일 존재 확인
  - `${도메인명}Identity.java` 파일 존재 확인
- **Model 존재하는 경우:**
  ```
  ✓ ${도메인명} Model 클래스를 발견했습니다:
     경로: ${루트모듈}/model/src/main/java/${패키지경로}/${도메인명소문자}/
     ├── ${도메인명}.java
     ├── ${도메인명}Model.java
     └── ${도메인명}Identity.java

     이 Model을 기반으로 Repository를 생성하시겠습니까? (Y/n)
  ```
- **Model 존재하지 않는 경우:**
  ```
  ❌ ${도메인명} 도메인의 Model 클래스가 존재하지 않습니다.

     다음 중 하나를 선택해주세요:
     1. 먼저 Model 생성 후 재시도 ("add_domain_to_model.md 실행해줘. 도메인명: ${도메인명}")
     2. 다른 도메인명 입력

     선택 (1/2):
  ```

### 3. 하위 엔티티명 입력 받기
- "하위 엔티티명을 입력해주세요 (비어두면 ${도메인명} 자체의 Repository 생성):"
- "예: Profile, Setting, Address (여러 개는 쉼표로 구분)"
- **입력 옵션:**
  - **빈 입력** → `${도메인명}Repository` 생성
  - **단일 입력** → `${도메인명}${엔티티명}Repository` 생성
  - **다중 입력** → 각각에 대해 `${도메인명}${엔티티명}Repository` 생성

### 4. 생성할 Repository 목록 확인 및 승인
```
도메인명: ${입력받은도메인명}
하위 엔티티: ${입력받은엔티티목록}

생성될 Repository 인터페이스:
${엔티티목록이_비어있으면:}
  ✓ ${도메인명}Repository
${엔티티목록이_있으면:}
  ✓ ${도메인명}${엔티티1}Repository
  ✓ ${도메인명}${엔티티2}Repository
  ...

패키지: ${감지된패키지}.${도메인명소문자}.repository
경로: ${루트모듈}/infrastructure/src/main/java/${패키지경로}/${도메인명소문자}/repository/

위 내용으로 진행하시겠습니까? (Y/n)
```

### 5. 디렉토리 구조 생성 (사용자 승인 후)
```
${루트모듈}/infrastructure/src/main/java/${패키지경로}/
└── ${도메인명소문자}/
    └── repository/
        ${각_Repository별로_생성}
        ├── ${도메인명}Repository.java (빈 입력인 경우)
        ├── ${도메인명}${엔티티1}Repository.java (하위 엔티티인 경우)
        └── ${도메인명}${엔티티2}Repository.java (하위 엔티티인 경우)
```

### 6. 파일 생성 (각 Repository별로 반복)

#### 6-1. Repository 인터페이스 템플릿 (${Repository명}.java)
```java
package ${패키지명}.${도메인명소문자}.repository;

import ${패키지명}.${도메인명소문자}.${엔티티클래스명};
import ${패키지명}.${도메인명소문자}.${엔티티클래스명}Identity;
import java.util.List;
import java.util.Optional;

/**
 * ${엔티티클래스명} Repository 인터페이스
 *
 * 헥사고날 아키텍처에서 Port 역할을 수행하며,
 * 비즈니스 로직에서 데이터 접근을 위한 인터페이스를 정의합니다.
 */
public interface ${Repository명} {

    /**
     * ID로 ${엔티티클래스명} 조회
     *
     * @param identity ${엔티티클래스명} 식별자
     * @return ${엔티티클래스명} 엔티티 (존재하지 않으면 Optional.empty())
     */
    Optional<${엔티티클래스명}> findById(${엔티티클래스명}Identity identity);

    /**
     * 모든 ${엔티티클래스명} 조회
     *
     * @return ${엔티티클래스명} 목록
     */
    List<${엔티티클래스명}> findAll();

    /**
     * 이름으로 ${엔티티클래스명} 조회
     *
     * @param name 이름
     * @return ${엔티티클래스명} 목록
     */
    List<${엔티티클래스명}> findByName(String name);

    /**
     * ${엔티티클래스명} 저장 (생성/수정)
     *
     * @param ${엔티티변수명} 저장할 ${엔티티클래스명}
     * @return 저장된 ${엔티티클래스명}
     */
    ${엔티티클래스명} save(${엔티티클래스명} ${엔티티변수명});

    /**
     * ID로 ${엔티티클래스명} 삭제
     *
     * @param identity ${엔티티클래스명} 식별자
     */
    void deleteById(${엔티티클래스명}Identity identity);

    /**
     * ${엔티티클래스명} 존재 여부 확인
     *
     * @param identity ${엔티티클래스명} 식별자
     * @return 존재하면 true, 없으면 false
     */
    boolean existsById(${엔티티클래스명}Identity identity);
    
}
```

#### 6-2. 엔티티별 클래스명 및 변수명 변환 규칙
```
입력 예시와 변환 결과:

빈 입력:
- Repository명: UserRepository
- 엔티티클래스명: User
- 엔티티변수명: user

"Profile" 입력:
- Repository명: UserProfileRepository
- 엔티티클래스명: UserProfile
- 엔티티변수명: userProfile

"Setting, Address" 입력:
- UserSettingRepository (엔티티: UserSetting, 변수: userSetting)
- UserAddressRepository (엔티티: UserAddress, 변수: userAddress)
```

### 7. 검증 및 완료 안내
- `./gradlew :${루트모듈}:infrastructure:build` 실행하여 컴파일 확인
- 생성된 파일 구조 및 스펙 출력
- 완료 메시지 및 다음 단계 안내

#### 7-1. 생성된 파일 위치 및 스펙 출력
```
✓ ${도메인명} Repository 인터페이스가 성공적으로 생성되었습니다!

📁 생성된 파일 위치:
   ${루트모듈}/infrastructure/src/main/java/${패키지경로}/${도메인명소문자}/repository/
   ${하위엔티티목록이_비어있으면:}
   └── ${도메인명}Repository.java
   ${하위엔티티목록이_있으면:}
   ├── ${도메인명}${엔티티1}Repository.java
   ├── ${도메인명}${엔티티2}Repository.java
   └── ...

📋 현재 기본 스펙:
   각 Repository 인터페이스는 다음 메서드를 제공합니다:
   - findById(${엔티티}Identity): Optional<${엔티티}>
   - findAll(): List<${엔티티}>
   - findByName(String): List<${엔티티}>
   - save(${엔티티}): ${엔티티}
   - deleteById(${엔티티}Identity): void
   - existsById(${엔티티}Identity): boolean
   - count(): long

🔧 기본값이 생성되었으니 확인하라.

💡 Repository 인터페이스 역할:
   - Port 인터페이스: 헥사고날 아키텍처의 경계 역할
   - 비즈니스 로직과 데이터 접근 계층 분리
   - 테스트 가능한 구조 (Mock 객체 활용)
   - 구현체는 repository-jdbc 모듈에서 제공

📝 Repository 확장 가이드:
   비즈니스 요구사항에 따라 다음과 같은 메서드를 추가할 수 있습니다:
   - 조건별 조회: findByStatus(), findByCategory()
   - 정렬 조회: findAllOrderByCreatedAt()
   - 페이징: findWithPagination(int page, int size)
   - 통계: countByStatus(), sumByAmount()
   - 배치 작업: saveAll(), deleteAll()

🚀 다음 단계:
   1. Repository 인터페이스를 비즈니스 요구사항에 맞게 확장
   2. Service 모듈에서 비즈니스 로직 구현: "add_domain_to_service.md 실행해줘"
   3. 구현체 생성: "add_domain_to_repository_jdbc.md 실행해줘"
   4. 추가 도메인 Repository: "add_domain_to_infrastructure.md 실행해줘"
```

## 대화형 시나리오 예시

### 시나리오 1: Model 엔티티 확인 후 Repository 생성
```
사용자: "add_domain_to_infrastructure.md 실행해줘"

봇: "프로젝트 정보를 감지했습니다.
     루트모듈: searchkim
     패키지: com.searchkim.multimodule
     위 설정이 맞나요? (Y/n)"

사용자: "Y"

봇: "추가할 도메인명을 입력해주세요 (예: User, Product, Order):"

사용자: "User"

봇: "User Model 클래스 존재를 확인 중...

     ✓ User Model 클래스를 발견했습니다:
        경로: searchkim/model/src/main/java/com/searchkim/multimodule/user/
        ├── User.java
        ├── UserModel.java
        └── UserIdentity.java

        이 Model을 기반으로 Repository를 생성하시겠습니까? (Y/n)"

사용자: "Y"

봇: "하위 엔티티명을 입력해주세요 (비어두면 User 자체의 Repository 생성):
     예: Profile, Setting, Address (여러 개는 쉼표로 구분)"

사용자: "" (빈 입력)

봇: "입력하신 정보를 확인해주세요:
     도메인명: User
     하위 엔티티: (없음)

     생성될 Repository 인터페이스:
     ✓ UserRepository

     패키지: com.searchkim.multimodule.user.repository
     경로: searchkim/infrastructure/src/main/java/com/searchkim/multimodule/user/repository/

     위 내용으로 진행하시겠습니까? (Y/n)"

사용자: "Y"

봇: [파일 생성 및 빌드 수행]
    "✓ User Repository 인터페이스가 성공적으로 생성되었습니다!
     🔧 기본값이 생성되었으니 확인하라."
```

### 시나리오 2: 하위 엔티티 Repository 생성
```
사용자: "add_domain_to_infrastructure.md 실행해줘"

봇: "프로젝트 정보를 감지했습니다.
     루트모듈: corehr
     패키지: com.searchkim
     위 설정이 맞나요? (Y/n)"

사용자: "Y"

봇: "추가할 도메인명을 입력해주세요 (예: User, Product, Order):"

사용자: "User"

봇: "하위 엔티티명을 입력해주세요 (비어두면 User 자체의 Repository 생성):
     예: Profile, Setting, Address (여러 개는 쉼표로 구분)"

사용자: "Profile, Setting, Address"

봇: "입력하신 정보를 확인해주세요:
     도메인명: User
     하위 엔티티: Profile, Setting, Address

     생성될 Repository 인터페이스:
     ✓ UserProfileRepository
     ✓ UserSettingRepository
     ✓ UserAddressRepository

     패키지: com.searchkim.user.repository
     경로: corehr/infrastructure/src/main/java/com/searchkim/user/repository/

     위 내용으로 진행하시겠습니까? (Y/n)"

사용자: "Y"

봇: [파일 생성 및 빌드 수행]
    "성공적으로 3개의 Repository 인터페이스가 생성되었습니다!
     ✓ UserProfileRepository
     ✓ UserSettingRepository
     ✓ UserAddressRepository"
```

## 입력 검증 규칙

### 도메인명 검증
- **필수**: 첫 글자는 대문자
- **필수**: 영문자만 포함 (숫자, 특수문자 불가)
- **권장**: PascalCase 형식 (예: ProductCategory)

### 의존성 검증
- **Model 모듈** 클래스 존재 확인
  - `${도메인명}.java` 파일 존재
  - `${도메인명}Identity.java` 파일 존재
- 존재하지 않으면 안내 메시지 출력

### 에러 처리
```
모델 미존재 시:
"❌ Foo 도메인의 모델 클래스가 존재하지 않습니다.
    먼저 다음 명령을 실행해주세요:
    'add_domain_to_model.md 실행해줘. 도메인명: Foo'"
```

## 예상 실행 결과 예시

### 단일 도메인인 경우 (User)
```
corehr/infrastructure/src/main/java/com/searchkim/
├── example/repository/ExampleRepository.java (기존)
└── user/
    └── repository/
        └── UserRepository.java (신규)
```

### 하위 엔티티인 경우 (User - Profile, Setting, Address)
```
corehr/infrastructure/src/main/java/com/searchkim/
├── example/repository/ExampleRepository.java (기존)
└── user/
    └── repository/
        ├── UserProfileRepository.java (신규)
        ├── UserSettingRepository.java (신규)
        └── UserAddressRepository.java (신규)
```

## Repository 메서드 설명
- **findById**: 기본 조회 (Optional 반환)
- **findAll**: 전체 조회
- **findByName**: 이름 기준 검색
- **save**: 생성/수정 (Upsert)
- **deleteById**: 삭제
- **existsById**: 존재 여부 확인
- **count**: 총 개수

## 주요 특징
- **대화형 입력** 방식
- **유연한 하위 엔티티** 지원 (단일/다중/빈 입력)
- **자동 명명 규칙** 적용 (PascalCase → camelCase)
- **Port 인터페이스** 역할
- **표준 Repository 패턴** 적용
- **완전한 CRUD** 메서드 제공
- **JavaDoc** 주석 포함
- **헥사고날 아키텍처** 준수

## 지원하는 입력 패턴
1. **빈 입력** → `UserRepository` (도메인 자체)
2. **단일 입력** → `UserProfileRepository` (하나의 하위 엔티티)
3. **다중 입력** → `UserProfileRepository, UserSettingRepository, UserAddressRepository` (여러 하위 엔티티)
4. **혼합 사용 가능** → 필요에 따라 여러 번 실행하여 Repository 추가