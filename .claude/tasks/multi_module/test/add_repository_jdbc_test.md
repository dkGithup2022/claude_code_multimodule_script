# Repository-JDBC 테스트 생성 스크립트 (필수 테스트만 + 2-depth 지원)

## 사용법
```bash
"add_repository_jdbc_test.md 실행해줘"
```

## 입력 프로세스

### 1. 프로젝트 정보 감지
- settings.gradle.kts → 루트모듈, build.gradle.kts → 패키지 추출
- "감지된 설정: 루트모듈=${루트모듈}, 패키지=${패키지} 맞나요? (Y/n)"

### 2. 테스트 대상 클래스명 입력 (2-depth 지원)
- "Repository 구현체 클래스명을 입력해주세요:"
- "예: UserRepositoryImpl, ProductCategoryRepositoryImpl, OrderItemRepositoryImpl"
- 도메인명 추출: "ProductCategoryRepositoryImpl" → "ProductCategory"

### 3. 레퍼런스 클래스 탐색 (제한된 범위)
- **Model**: `**/${도메인명}.java`, `**/${도메인명}Identity.java`
- **Infrastructure**: `**/${도메인명}Repository.java`
- **Repository-JDBC**: `**/${입력받은클래스명}.java`

**탐색 결과 예시:**
```
✓ ProductCategory.java 발견: ${패키지}/product/ProductCategory.java
✓ ProductCategoryIdentity.java 발견: ${패키지}/product/ProductCategoryIdentity.java
✓ ProductCategoryRepository.java 발견: ${패키지}/product/repository/ProductCategoryRepository.java
✓ ProductCategoryRepositoryImpl.java 발견: ${패키지}/product/repository/ProductCategoryRepositoryImpl.java
```

### 4. CrudRepository 분석 (필수 테스트만)
- Infrastructure Repository 메서드 추출 및 분석
- **자동 분류:**
  ```
  🟢 무시 (CrudRepository 기본):
    - findById, save, deleteById, existsById → 단순 위임

  🔴 테스트 필수:
    - save() → Entity↔Domain 변환 + 저장
    - findById() → Entity→Domain 변환
    - findAll() → List<Entity>→List<Domain> 변환
    - findByName() → 커스텀 쿼리 + 변환 (발견된 경우만)
  ```

### 5. Model 스펙 수집 (2-depth 지원)
**Model 필드 분석:**
```
ProductCategory.java 스펙:
📋 ID: productCategoryId (Long)
📋 비즈니스: productId (Long), categoryName (String), priority (Integer)
📋 시간: createdAt (Instant), updatedAt (Instant)

ProductCategoryIdentity.java 스펙:
📋 생성자: ProductCategoryIdentity(Long productCategoryId)
```

### 6. 필수 테스트 자동 생성
```
생성 예정 테스트: ${필수_테스트_개수}개 (Entity↔Domain 변환 + 커스텀 쿼리만)
```

## 생성되는 테스트 구조

### 테스트 클래스 기본 템플릿
```java
@DataJdbcTest
@ComponentScan("${탐색된_구현체_패키지}")
class ${입력받은클래스명}Test {

    @Autowired
    private ${입력받은클래스명} ${도메인명소문자}Repository;

    // 탐색된 Model 스펙 기반 테스트 데이터
    private final ${도메인명} sample${도메인명} = new ${도메인명}(
        null,                    // ID: null (자동생성)
        ${Model_필드_기반_테스트_데이터}
    );

    private final ${도메인명}Identity testIdentity = new ${도메인명}Identity(1L);
    private final ${도메인명}Identity nonExistingIdentity = new ${도메인명}Identity(999L);

    // 필수 테스트 메서드들 (CrudRepository 분석 기반)
    ${필수_테스트_메서드들}
}
```

### 필수 테스트 메서드
```java
// Entity↔Domain 변환 테스트 (필수)
@Test void save_withValidDomain_returnsConvertedDomain()
@Test void save_withNullId_generatesIdAndReturns()
@Test void findById_existingId_returnsConvertedDomain()
@Test void findById_nonExistingId_returnsEmpty()
@Test void findAll_withData_returnsConvertedList()
@Test void findAll_emptyRepository_returnsEmptyList()

// 커스텀 Named Query 테스트 (발견된 메서드만)
@Test void findByName_existingName_returnsConvertedList()
@Test void findByName_nonExistingName_returnsEmptyList()
```

## 테스트 데이터 생성 규칙

### 타입별 자동 생성
- **ID 필드**: null (자동생성 테스트)
- **String**: "test" + 필드명 (name → "testName")
- **String (email)**: "test@example.com"
- **Long/Integer**: 1L, 2L
- **Enum**: 첫 번째 값
- **Instant**: Instant.now()

### 2-depth 도메인 예시
```java
// ProductCategory 테스트 데이터
private final ProductCategory sampleProductCategory = new ProductCategory(
    null,                    // productCategoryId
    1L,                      // productId
    "testCategoryName",      // categoryName
    1,                       // priority
    Instant.now(),           // createdAt
    Instant.now()            // updatedAt
);
```

## 검증 및 완료

### 빌드 검증
- `./gradlew :${루트모듈}:repository-jdbc:compileTestJava` 컴파일 확인
- `./gradlew :${루트모듈}:repository-jdbc:test --tests "${입력받은클래스명}Test"` 테스트 실행

### 완료 메시지
```
✅ ${입력받은클래스명} 테스트 생성 완료!

📁 파일: ${루트모듈}/repository-jdbc/src/test/java/${패키지}/repository/${입력받은클래스명}Test.java
📋 테스트: ${필수_테스트_개수}개 (Entity↔Domain 변환 + 커스텀 쿼리만)
🔧 CrudRepository 기본 메서드: 자동 제외
💡 2-depth 도메인: ${도메인명} 지원

🚀 다음 단계:
   1. 다른 Repository 테스트: "add_repository_jdbc_test.md 실행해줘"
   2. Service 테스트 생성: "add_service_test.md 실행해줘"
```

## 주요 특징
- **클래스명 기반 정확한 타겟팅** - 구체적인 구현체 클래스명 입력
- **필수 테스트만 생성** - Entity↔Domain 변환 + 커스텀 쿼리만
- **2-depth 도메인 지원** - ProductCategory, OrderItem 등 복합 도메인명
- **제한된 범위 탐색** - 3개 모듈에서만 레퍼런스 클래스 검색
- **CrudRepository 분석** - 기본 메서드 자동 제외
- **Model 스펙 기반** - 실제 필드 분석하여 테스트 데이터 생성
- **Spring 통합 테스트** - @DataJdbcTest + H2 InMemory DB