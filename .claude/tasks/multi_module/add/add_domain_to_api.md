# API Module에 REST API 컨트롤러 추가 스크립트 (대화형 + Reader/Writer 패턴)

## 사용법
```bash
"add_domain_to_api.md 실행해줘"
```

## 대화형 입력 프로세스

### 1. 프로젝트 정보 자동 감지 및 확인
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출
- "감지된 설정: 루트모듈=${루트모듈}, 패키지=${패키지} 맞나요? (Y/n)"

### 2. 도메인 수집 및 분석
- "API를 생성할 도메인들을 입력해주세요 (쉼표로 구분): Bar, User, Product"
- 입력값 검증: PascalCase, 영문자만, 첫 글자 대문자

#### 각 도메인별 의존성 및 스펙 자동 감지:
- **Model 모듈** 클래스 탐색 및 필드 분석
  - `${도메인명}.java` 확인 후 필드 추출
  - ID 필드, 비즈니스 필드, 시간 필드 분류
- **Service 모듈** Reader/Writer 클래스 확인
  - `${도메인명}Reader.java` 및 `${도메인명}Writer.java` 존재 확인
  - 메서드 목록 추출
- **Exception 모듈** 예외 클래스 확인

#### 대화형 선택:
```
도메인 'Bar' 분석 결과:
✓ Model: Bar.java 발견
  필드: barId(Long), ownerId(Long), code(String), createdAt(Instant), updatedAt(Instant)
✓ Service: BarReader.java, BarWriter.java 발견
  Reader 메서드: findByIdentity, findAll, findByOwner (3개)
  Writer 메서드: upsert, delete (2개)
✓ Exception: 관련 예외 클래스 확인

이 도메인으로 API를 생성하시겠습니까? (Y/n/s)
Y: 생성, n: 스킵, s: 설정 변경
```

### 3. 생성 확인 및 승인
```
처리할 도메인: Bar, User, Product (3개)

생성될 API 클래스 목록:
✓ Bar: BarApiController + DTO들 (Model 필드 기반)
  - BarApiController (Reader/Writer 패턴 적용, 5개 엔드포인트)
  - BarResponse (barId, ownerId, code, createdAt, updatedAt)
  - BarCreateRequest (ownerId, code)
  - BarUpdateRequest (ownerId, code)

✓ User: UserApiController + DTO들 (Model 필드 기반)
  - UserApiController (Reader/Writer 패턴 적용, 4개 엔드포인트)
  - UserResponse (userId, name, email, createdAt, updatedAt)
  - UserCreateRequest (name, email)
  - UserUpdateRequest (name, email)

✓ Product: ProductApiController + DTO들 (Model 필드 기반)
  - ProductApiController (Reader/Writer 패턴 적용, 6개 엔드포인트)
  - ProductResponse (productId, name, price, category, createdAt, updatedAt)
  - ProductCreateRequest (name, price, category)
  - ProductUpdateRequest (name, price, category)

패키지: ${감지된패키지}.[bar|user|product]
경로: ${루트모듈}/api/src/main/java/${패키지경로}/

위 내용으로 진행하시겠습니까? (Y/n)
```

### 4. 파일 생성 (각 도메인별로 반복)

#### 4-1. API Controller 템플릿 (${도메인명}ApiController.java)
```java
package ${패키지명}.${도메인명소문자};

import ${패키지명}.${도메인명소문자}.${도메인명}Reader;
import ${패키지명}.${도메인명소문자}.${도메인명}Writer;
import ${패키지명}.${도메인명소문자}.${도메인명};
import ${패키지명}.${도메인명소문자}.${도메인명}Identity;
import ${패키지명}.${도메인명소문자}.dto.${도메인명}Response;
import ${패키지명}.${도메인명소문자}.dto.${도메인명}CreateRequest;
import ${패키지명}.${도메인명소문자}.dto.${도메인명}UpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ${도메인명} REST API 컨트롤러
 *
 * Reader/Writer 패턴을 활용하여 CQRS 기반 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/${도메인명소문자_복수형}")
@RequiredArgsConstructor
@Slf4j
public class ${도메인명}ApiController {

    private final ${도메인명}Reader ${도메인명소문자}Reader;
    private final ${도메인명}Writer ${도메인명소문자}Writer;

    // Reader 메서드들 (Service Reader 메서드 기반 동적 생성)
    ${Service_Reader_기반_API_엔드포인트들}

    // Writer 메서드들 (Service Writer 메서드 기반 동적 생성)
    ${Service_Writer_기반_API_엔드포인트들}
}
```

#### 4-2. Response DTO 템플릿 (${도메인명}Response.java)
```java
package ${패키지명}.${도메인명소문자}.dto;

import ${패키지명}.${도메인명소문자}.${도메인명};
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.Instant;

/**
 * ${도메인명} 응답 DTO
 *
 * API 응답에서 사용되는 ${도메인명} 데이터 전송 객체
 * Model 클래스 필드 기반으로 자동 생성됩니다.
 */
@Getter
public class ${도메인명}Response {

    // Model 클래스 필드들을 기반으로 동적 생성
    ${Model_필드들을_Response_DTO로_변환}

    public ${도메인명}Response(${Model_필드들을_생성자_파라미터로_변환}) {
        ${Model_필드들을_this_할당으로_변환}
    }

    /**
     * 도메인 객체에서 Response DTO 생성
     */
    public static ${도메인명}Response from(${도메인명} ${도메인명소문자}) {
        return new ${도메인명}Response(
                ${Model_필드들을_getter_호출로_변환}
        );
    }
}
```

#### 4-3. Create Request DTO 템플릿 (${도메인명}CreateRequest.java)
```java
package ${패키지명}.${도메인명소문자}.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * ${도메인명} 생성 요청 DTO
 *
 * API에서 ${도메인명} 생성 시 사용되는 요청 데이터 전송 객체
 * Model 클래스의 비즈니스 필드 기반으로 자동 생성됩니다 (ID, 시간 필드 제외).
 */
@Getter
@Setter
public class ${도메인명}CreateRequest {

    // Model 클래스의 비즈니스 필드들만 포함 (ID, createdAt, updatedAt 제외)
    ${Model_비즈니스_필드들을_CreateRequest로_변환}
}
```

#### 4-4. Update Request DTO 템플릿 (${도메인명}UpdateRequest.java)
```java
package ${패키지명}.${도메인명소문자}.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * ${도메인명} 수정 요청 DTO
 *
 * API에서 ${도메인명} 수정 시 사용되는 요청 데이터 전송 객체
 * Model 클래스의 비즈니스 필드 기반으로 자동 생성됩니다 (ID, 시간 필드 제외).
 */
@Getter
@Setter
public class ${도메인명}UpdateRequest {

    // Model 클래스의 비즈니스 필드들만 포함 (ID, createdAt, updatedAt 제외)
    ${Model_비즈니스_필드들을_UpdateRequest로_변환}
}
```

### 5. 검증 및 결과 확인
- `./gradlew :${루트모듈}:api:build` 실행하여 컴파일 확인
- 생성된 파일 구조 출력
- "성공적으로 ${처리된도메인개수}개 도메인의 REST API가 생성되었습니다!"

## 대화형 시나리오 예시

### Reader/Writer 패턴 기반 다중 도메인 API 생성
```
사용자: "add_domain_to_api.md 실행해줘"

봇: "감지된 설정: 루트모듈=searchkim, 패키지=com.searchkim.multimodule 맞나요? (Y/n)"

사용자: "Y"

봇: "API를 생성할 도메인들을 입력해주세요 (쉼표로 구분): "

사용자: "Bar, User"

봇: "도메인 'Bar' 분석 결과:
     ✓ Model: Bar.java 발견
       필드: barId(Long), ownerId(Long), code(String), createdAt(Instant), updatedAt(Instant)
     ✓ Service: BarReader.java, BarWriter.java 발견
       Reader 메서드: findByIdentity, findAll, findByOwner (3개)
       Writer 메서드: upsert, delete (2개)
     ✓ Exception: 관련 예외 클래스 확인

     이 도메인으로 API를 생성하시겠습니까? (Y/n/s)"

사용자: "Y"

봇: "도메인 'User' 분석 결과: [유사하게 진행...]"

봇: "처리할 도메인: Bar, User (2개)

     생성될 API 클래스 목록:
     ✓ Bar: BarApiController + DTO들 (Model 필드 기반)
       - BarApiController (Reader/Writer 패턴, 5개 엔드포인트)
       - BarResponse (barId, ownerId, code, createdAt, updatedAt)
       - BarCreateRequest (ownerId, code)
       - BarUpdateRequest (ownerId, code)

     ✓ User: UserApiController + DTO들 (Model 필드 기반)
       - UserApiController (Reader/Writer 패턴, 4개 엔드포인트)
       - UserResponse (userId, name, email, createdAt, updatedAt)
       - UserCreateRequest (name, email)
       - UserUpdateRequest (name, email)

     위 내용으로 진행하시겠습니까? (Y/n)"

사용자: "Y"

봇: "성공적으로 2개 도메인의 REST API가 생성되었습니다!
     ✓ Bar: BarApiController + 4개 DTO (Model 기반 필드 매핑)
     ✓ User: UserApiController + 4개 DTO (Model 기반 필드 매핑)"
```

## 주요 특징
- **CQRS 패턴 적용** - Reader/Writer 서비스 분리 주입
- **대화형 다중 도메인** 처리
- **Model 필드 자동 감지** - 실제 Model 클래스 필드 기반 DTO 생성
- **Service 메서드 기반** 동적 API 엔드포인트 생성
- **Reader/Writer 분리 주입** - 단일 Service 대신 Reader + Writer
- **로깅 표준화** - @Slf4j 적용, 모든 API 요청/응답 로그
- **Model 기반 DTO** - ID/시간 필드 자동 분류 및 처리
- **Jakarta Validation** 자동 적용
- **@Lombok 활용** - @Getter, @Setter, @RequiredArgsConstructor
- **null 처리** - Reader에서 null 반환 시 적절한 HTTP 상태 코드
- **upsert 패턴** - Writer의 upsert() 메서드 활용

## 생성되는 API 엔드포인트 (Service 메서드 기반)
### Reader 기반 조회 엔드포인트
- `GET /api/{domain}s` - findAll() 기반
- `GET /api/{domain}s/{id}` - findByIdentity() 기반
- `GET /api/{domain}s/owner/{ownerId}` - findByOwner() 기반 (해당 메서드 존재시)

### Writer 기반 변경 엔드포인트
- `POST /api/{domain}s` - upsert() 기반 생성
- `PUT /api/{domain}s/{id}` - upsert() 기반 수정
- `DELETE /api/{domain}s/{id}` - delete() 기반 삭제

## DTO 자동 생성 규칙
- **Response DTO**: Model의 모든 필드 포함
- **Create/Update Request DTO**: ID, createdAt, updatedAt 필드 제외
- **Instant 필드**: @JsonFormat 자동 적용
- **Validation**: 필드 타입별 적절한 제약조건 자동 적용