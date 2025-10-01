# API Module 테스트 생성 스크립트 (Standalone MockMvc + Mockito)

## 사용법
```bash
"add_api_test.md 실행해줘"
```

## 입력 프로세스

### 1. 프로젝트 정보 감지
- settings.gradle.kts → 루트모듈, build.gradle.kts → 패키지 추출
- "감지된 설정: 루트모듈=${루트모듈}, 패키지=${패키지} 맞나요? (Y/n)"

### 2. 테스트 대상 클래스명 입력 (2-depth 지원)
- "API Controller 클래스명을 입력해주세요:"
- "예: FooApiController, ProductCategoryApiController, OrderItemApiController"
- 도메인명 추출: "ProductCategoryApiController" → "ProductCategory"

### 3. 레퍼런스 클래스 탐색 (제한된 범위)
- **Model**: `**/${도메인명}.java`, `**/${도메인명}Identity.java`
- **Service**: `**/${도메인명}Reader.java`, `**/${도메인명}Writer.java`
- **API**: `**/${입력받은클래스명}.java`
- **DTO**: `**/${도메인명}Response.java`, `**/${도메인명}CreateRequest.java`, `**/${도메인명}UpdateRequest.java`

**탐색 결과 예시:**
```
✓ Foo.java 발견: ${패키지}/foo/Foo.java
✓ FooIdentity.java 발견: ${패키지}/foo/FooIdentity.java
✓ FooReader.java 발견: ${패키지}/foo/FooReader.java
✓ FooWriter.java 발견: ${패키지}/foo/FooWriter.java
✓ FooApiController.java 발견: ${패키지}/foo/FooApiController.java
✓ FooResponse.java 발견: ${패키지}/foo/dto/FooResponse.java
✓ FooCreateRequest.java 발견: ${패키지}/foo/dto/FooCreateRequest.java
✓ FooUpdateRequest.java 발견: ${패키지}/foo/dto/FooUpdateRequest.java
```

### 4. Controller 메서드 분석 (필수 테스트만)
- Controller 클래스 @RequestMapping 분석
- **자동 분류:**
  ```
  🔴 테스트 필수 (모든 HTTP 메서드):
    - GET /api/foos → findAll() 호출 + 200 OK + List<Response> 검증
    - GET /api/foos/{id} → findByIdentity() 호출 + 200 OK/404 NOT_FOUND + Response 검증
    - GET /api/foos/search?name=xxx → findByName() 호출 + 200 OK + List<Response> 검증
    - POST /api/foos → upsert() 호출 + 201 CREATED + Response 검증
    - PUT /api/foos/{id} → findByIdentity() + upsert() 호출 + 200 OK/404 NOT_FOUND + Response 검증
    - DELETE /api/foos/{id} → delete() 호출 + 204 NO_CONTENT/404 NOT_FOUND 검증
  ```

### 5. Model 스펙 수집 (2-depth 지원)
**Model 필드 분석:**
```
Foo.java 스펙:
📋 ID: fooId (Long)
📋 비즈니스: name (String)
📋 시간: createdAt (Instant), updatedAt (Instant)
```

### 6. 필수 테스트 자동 생성
```
생성 예정 테스트: ${API_엔드포인트_개수 * 2}개 (Status Code + Response Spec 검증)
```

## 생성되는 테스트 구조

### API 테스트 클래스 템플릿 (Standalone MockMvc)
```java
@ExtendWith(MockitoExtension.class)
class ${입력받은클래스명}Test {

    private MockMvc mockMvc;

    @Mock
    private ${도메인명}Reader ${도메인명소문자}Reader;

    @Mock
    private ${도메인명}Writer ${도메인명소문자}Writer;

    @InjectMocks
    private ${입력받은클래스명} ${입력받은클래스명소문자};

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(${입력받은클래스명소문자}).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JavaTimeModule 등록
    }

    // 탐색된 Model 스펙 기반 테스트 데이터
    private final ${도메인명} sample${도메인명} = new ${도메인명}(
        1L,                      // ID: 실제 값 (API 테스트용)
        ${Model_필드_기반_테스트_데이터}
    );

    private final ${도메인명}Identity testIdentity = new ${도메인명}Identity(1L);

    // 필수 테스트 메서드들 (Controller 메서드 기반)
    ${필수_테스트_메서드들}
}
```

### 필수 테스트 메서드 (Status Code + Response Spec 동시 검증)
```java
// GET /api/foos - 전체 조회
@Test void getAllFoos_returnsOkWithList() throws Exception
@Test void getAllFoos_emptyList_returnsOkWithEmptyArray() throws Exception

// GET /api/foos/{id} - ID 조회
@Test void getFooById_existingId_returnsOkWithFoo() throws Exception
@Test void getFooById_nonExistingId_throwsException() throws Exception

// GET /api/foos/search?name=xxx - 검색
@Test void getFoosByName_existingName_returnsOkWithList() throws Exception
@Test void getFoosByName_nonExistingName_returnsOkWithEmptyArray() throws Exception

// POST /api/foos - 생성
@Test void createFoo_validRequest_returnsCreatedWithFoo() throws Exception
@Test void createFoo_invalidRequest_returnsBadRequest() throws Exception

// PUT /api/foos/{id} - 수정
@Test void updateFoo_existingId_returnsOkWithUpdatedFoo() throws Exception
@Test void updateFoo_nonExistingId_throwsException() throws Exception

// DELETE /api/foos/{id} - 삭제
@Test void deleteFoo_existingId_returnsNoContent() throws Exception
```

### 테스트 메서드 예시 (Status + Spec 동시 검증)
```java
// GET 성공 케이스
@Test
void getFooById_existingId_returnsOkWithFoo() throws Exception {
    // given
    when(fooReader.findById(testIdentity))
        .thenReturn(Optional.of(sampleFoo));

    // when & then - Status Code 검증
    MvcResult result = mockMvc.perform(get("/api/v1/foos/{id}", 1L))
        .andExpect(status().isOk())
        .andReturn();

    // then - Response Spec 검증
    String responseJson = result.getResponse().getContentAsString();
    FooResponse response = objectMapper.readValue(responseJson, FooResponse.class);

    assertThat(response.fooId()).isEqualTo(1L);
    assertThat(response.name()).isEqualTo("testName");
    assertThat(response.createdAt()).isNotNull();
    assertThat(response.updatedAt()).isNotNull();

    verify(fooReader).findById(testIdentity);
}

// GET 실패 케이스 (RuntimeException 처리)
@Test
void getFooById_nonExistingId_throwsException() throws Exception {
    // given
    when(fooReader.findById(new FooIdentity(999L)))
        .thenReturn(Optional.empty());

    // when & then
    try {
        mockMvc.perform(get("/api/v1/foos/{id}", 999L))
            .andExpect(status().is5xxServerError());
    } catch (Exception e) {
        // Controller에서 RuntimeException 발생 예상
        assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
    }

    verify(fooReader).findById(new FooIdentity(999L));
}

// POST 성공 케이스
@Test
void createFoo_validRequest_returnsCreatedWithFoo() throws Exception {
    // given
    CreateFooRequest request = new CreateFooRequest("test@example.com", "testName");

    when(fooWriter.upsert(any(Foo.class))).thenReturn(sampleFoo);

    // when & then - Status Code 검증
    MvcResult result = mockMvc.perform(post("/api/v1/foos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn();

    // then - Response Spec 검증
    String responseJson = result.getResponse().getContentAsString();
    FooResponse response = objectMapper.readValue(responseJson, FooResponse.class);

    assertThat(response.fooId()).isEqualTo(1L);
    assertThat(response.name()).isEqualTo("testName");

    verify(fooWriter).upsert(argThat(foo ->
        foo.getFooId() == null &&
        foo.getName().equals("testName")
    ));
}

// POST 실패 케이스 (Validation)
@Test
void createFoo_invalidRequest_returnsBadRequest() throws Exception {
    // given - 빈 이름
    CreateFooRequest request = new CreateFooRequest("", "testName");

    // when & then
    mockMvc.perform(post("/api/v1/foos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(fooWriter, never()).upsert(any());
}
```

## 테스트 데이터 생성 규칙

### 타입별 자동 생성 (API 테스트용)
- **ID 필드**: 1L, 2L (실제 값, API에서 사용)
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

## MockMvc 테스트 패턴

### HTTP Status Code 검증
```java
// 성공 케이스
.andExpect(status().isOk())          // 200 OK
.andExpect(status().isCreated())     // 201 CREATED
.andExpect(status().isNoContent())   // 204 NO_CONTENT

// 실패 케이스
.andExpect(status().isNotFound())    // 404 NOT_FOUND
.andExpect(status().isBadRequest())  // 400 BAD_REQUEST
```

### Response Spec 검증
```java
// JSON 응답을 객체로 변환 후 필드별 검증
String responseJson = result.getResponse().getContentAsString();
FooResponse response = objectMapper.readValue(responseJson, FooResponse.class);

assertThat(response.getFooId()).isEqualTo(1L);
assertThat(response.getName()).isEqualTo("testName");
assertThat(response.getCreatedAt()).isNotNull();
```

### Reader/Writer Mock 검증
```java
// Service 호출 검증
verify(fooReader).findByIdentity(testIdentity);
verify(fooWriter).upsert(argThat(foo ->
    foo.getName().equals("expectedName")
));
```

## 검증 및 완료

### 빌드 검증
- `./gradlew :${루트모듈}:api:compileTestJava` 컴파일 확인
- `./gradlew :${루트모듈}:api:test --tests "${입력받은클래스명}Test"` 테스트 실행

### 완료 메시지
```
✅ ${입력받은클래스명} 테스트 생성 완료!

📁 파일: ${루트모듈}/api/src/test/java/${패키지}/${입력받은클래스명}Test.java
📋 테스트: ${API_테스트_개수}개 (Status Code + Response Spec 동시 검증)
🔧 MockMvc 테스트: Standalone Setup + @Mock Reader/Writer
💡 현대적 스타일: Spring Context 없는 빠른 단위 테스트

🚀 다음 단계:
   1. 다른 API Controller 테스트: "add_api_test.md 실행해줘"
   2. 통합 테스트 생성: "add_integration_test.md 실행해줘"
```

## 주요 특징
- **Standalone MockMvc** - Spring Context 없이 빠른 단위 테스트
- **Mockito 기반** - @Mock, @InjectMocks로 의존성 주입
- **JavaTimeModule 자동 등록** - Instant 등 Java Time API 직렬화 지원
- **RuntimeException 처리** - try-catch로 Controller 예외 검증
- **Status Code + Response Spec 동시 검증** - 하나의 테스트에서 두 가지 모두 확인
- **2-depth 도메인 지원** - ProductCategory, OrderItem 등 복합 도메인명
- **DTO 기반 검증** - Request/Response DTO 자동 탐색 및 활용
- **현대적인 테스트 스타일** - 빠르고 격리된 단위 테스트