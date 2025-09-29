# Service Module 테스트 생성 스크립트 (Reader/Writer 패턴 + Mock 테스트만)

## 사용법
```bash
"add_service_test.md 실행해줘"
```

## 입력 프로세스

### 1. 프로젝트 정보 감지
- settings.gradle.kts → 루트모듈, build.gradle.kts → 패키지 추출
- "감지된 설정: 루트모듈=${루트모듈}, 패키지=${패키지} 맞나요? (Y/n)"

### 2. 테스트 대상 클래스명 입력 (2-depth 지원)
- "Service 구현체 클래스명을 입력해주세요:"
- "예: DefaultUserReader, DefaultProductCategoryWriter, DefaultOrderItemReader"
- 도메인명 추출: "DefaultProductCategoryReader" → "ProductCategory"
- 서비스 타입 추출: "DefaultUserReader" → "Reader"

### 3. 레퍼런스 클래스 탐색 (제한된 범위)
- **Model**: `**/${도메인명}.java`, `**/${도메인명}Identity.java`
- **Infrastructure**: `**/${도메인명}Repository.java`
- **Service**: `**/${도메인명}Reader.java`, `**/${도메인명}Writer.java`

**탐색 결과 예시:**
```
✓ User.java 발견: ${패키지}/user/User.java
✓ UserIdentity.java 발견: ${패키지}/user/UserIdentity.java
✓ UserRepository.java 발견: ${패키지}/user/repository/UserRepository.java
✓ UserReader.java 발견: ${패키지}/user/UserReader.java
✓ DefaultUserReader.java 발견: ${패키지}/user/impl/DefaultUserReader.java
```

### 4. 서비스 인터페이스 분석 (필수 테스트만)
- Service 인터페이스 메서드 추출 및 분석
- **자동 분류:**
  ```
  🔴 테스트 필수 (비즈니스 로직):
    - findByIdentity() → Repository 호출 + 예외 처리
    - findAll() → Repository 호출 + 리스트 변환
    - findByName() → Repository 호출 + 필터링 로직
    - upsert() → Repository 호출 + 생성/수정 로직
    - delete() → Repository 호출 + 삭제 로직

  🟢 무시 (단순 위임):
    - (Reader/Writer 패턴에서는 대부분 테스트 필수)
  ```

### 5. Model 스펙 수집 (2-depth 지원)
**Model 필드 분석:**
```
User.java 스펙:
📋 ID: userId (Long)
📋 비즈니스: name (String), email (String), status (UserStatus)
📋 시간: createdAt (Instant), updatedAt (Instant)

UserIdentity.java 스펙:
📋 생성자: UserIdentity(Long userId)
```

### 6. 필수 테스트 자동 생성
```
생성 예정 테스트: ${필수_테스트_개수}개 (Repository Mock + 비즈니스 로직 검증)
```

## 생성되는 테스트 구조

### Reader 테스트 클래스 템플릿
```java
@ExtendWith(MockitoExtension.class)
class Default${도메인명}ReaderTest {

    @Mock
    private ${도메인명}Repository ${도메인명소문자}Repository;

    @InjectMocks
    private Default${도메인명}Reader ${도메인명소문자}Reader;

    // 탐색된 Model 스펙 기반 테스트 데이터
    private final ${도메인명} sample${도메인명} = new ${도메인명}(
        1L,                      // ID: 실제 값 (Mock 테스트용)
        ${Model_필드_기반_테스트_데이터}
    );

    private final ${도메인명}Identity testIdentity = new ${도메인명}Identity(1L);

    // 필수 테스트 메서드들 (Service 인터페이스 기반)
    ${필수_테스트_메서드들}
}
```

### Writer 테스트 클래스 템플릿
```java
@ExtendWith(MockitoExtension.class)
class Default${도메인명}WriterTest {

    @Mock
    private ${도메인명}Repository ${도메인명소문자}Repository;

    @InjectMocks
    private Default${도메인명}Writer ${도메인명소문자}Writer;

    // Writer용 테스트 데이터
    private final ${도메인명} sample${도메인명} = new ${도메인명}(/*...*/);

    // Writer 필수 테스트 메서드들
    ${Writer_필수_테스트_메서드들}
}
```

### 필수 테스트 메서드 (Reader)
```java
// Repository 호출 + 예외 처리 테스트
@Test void findByIdentity_existingId_returnsFound()
@Test void findByIdentity_nonExistingId_throwsException()

// Repository 호출 + 리스트 처리 테스트
@Test void findAll_withData_returnsList()
@Test void findAll_emptyData_returnsEmptyList()

// 커스텀 로직 테스트 (발견된 메서드만)
@Test void findByName_existingName_returnsList()
@Test void findByName_nonExistingName_returnsEmptyList()
```

### 필수 테스트 메서드 (Writer)
```java
// Repository 호출 + 생성/수정 로직 테스트
@Test void upsert_newEntity_callsRepositorySave()
@Test void upsert_existingEntity_callsRepositorySave()

// Repository 호출 + 삭제 로직 테스트
@Test void delete_existingId_callsRepositoryDelete()
@Test void delete_nonExistingId_handlesGracefully()
```

## 테스트 데이터 생성 규칙

### 타입별 자동 생성 (Mock 테스트용)
- **ID 필드**: 1L, 2L (실제 값, Mock에서 사용)
- **String**: "testName", "testEmail"
- **Long/Integer**: 1L, 2L
- **Enum**: 첫 번째 값
- **Instant**: Instant.now()

### 2-depth 도메인 예시
```java
// ProductCategory 테스트 데이터
private final ProductCategory sampleProductCategory = new ProductCategory(
    1L,                      // productCategoryId
    1L,                      // productId
    "testCategoryName",      // categoryName
    1,                       // priority
    Instant.now(),           // createdAt
    Instant.now()            // updatedAt
);
```

## Mock 테스트 패턴

### Repository Mock 설정
```java
// Reader 테스트 - Repository Mock 설정 예시
@Test
void findByIdentity_existingId_returnsFound() {
    // given
    when(userRepository.findById(testIdentity))
        .thenReturn(Optional.of(sampleUser));

    // when
    User result = userReader.findByIdentity(testIdentity);

    // then
    assertNotNull(result);
    assertEquals(sampleUser.getName(), result.getName());
    verify(userRepository).findById(testIdentity);
}
```

### 예외 처리 테스트
```java
@Test
void findByIdentity_nonExistingId_throwsException() {
    // given
    when(userRepository.findById(testIdentity))
        .thenReturn(Optional.empty());

    // when & then
    assertThrows(RuntimeException.class, () ->
        userReader.findByIdentity(testIdentity)
    );
    verify(userRepository).findById(testIdentity);
}
```

## 검증 및 완료

### 빌드 검증
- `./gradlew :${루트모듈}:service:compileTestJava` 컴파일 확인
- `./gradlew :${루트모듈}:service:test --tests "Default${도메인명}${타입}Test"` 테스트 실행

### 완료 메시지
```
✅ Default${도메인명}${타입} 테스트 생성 완료!

📁 파일: ${루트모듈}/service/src/test/java/${패키지}/impl/Default${도메인명}${타입}Test.java
📋 테스트: ${필수_테스트_개수}개 (Repository Mock + 비즈니스 로직 검증)
🔧 Mock 테스트: @InjectMocks + @Mock Repository
💡 2-depth 도메인: ${도메인명} 지원

🚀 다음 단계:
   1. 상대방 서비스 테스트: "${반대_타입} 테스트 생성"
   2. API 테스트 생성: "add_api_test.md 실행해줘"
```

## 주요 특징
- **Reader/Writer 분리** - CQRS 패턴 기반 개별 테스트
- **Mock 기반 단위 테스트** - Repository 의존성 Mock 처리
- **비즈니스 로직 검증** - 예외 처리, 변환 로직, 필터링 로직
- **2-depth 도메인 지원** - ProductCategory, OrderItem 등 복합 도메인명
- **제한된 범위 탐색** - 3개 모듈에서만 레퍼런스 클래스 검색
- **Service 인터페이스 기반** - 실제 메서드 분석하여 테스트 생성
- **Model 스펙 기반** - 실제 필드 분석하여 테스트 데이터 생성