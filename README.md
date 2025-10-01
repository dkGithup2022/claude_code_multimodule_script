# URL Shortener Project


클로드 스크립트 정리 : https://github.com/dkGithup2022/claude_code_multimodule_script

> 멀티모듈 아키텍처 기반의 URL 단축 서비스

---

## 1. 기능

### 1.1 핵심 기능
- **URL 단축**: 긴 URL을 짧은 코드로 변환
- **리다이렉트**: Short URL 접속 시 원본 URL로 자동 이동
- **클릭 추적**: 각 링크의 클릭 수, IP, User-Agent, Referer 기록
- **만료 관리**: 링크별 만료 시간 설정 및 자동 차단
- **사용자 관리**: 사용자별 링크 생성 및 관리

### 1.2 Short Code 생성 전략
- **Snowflake ID**: 분산 환경에서 충돌 없는 고유 ID 생성
- **Base62 인코딩**: 숫자 ID를 짧은 문자열로 변환 (0-9, a-z, A-Z)
- **예시**: ID `123456789` → Short Code `8M0kX`

### 1.3 모니터링 기능
- 전체 링크 목록 조회
- 링크별 클릭 통계 (총 클릭 수 + 상세 클릭 기록)
- IP, User-Agent, Referer 분석 가능

---

## 2. API Specs

### 2.1 Link API

#### POST /api/v1/links
**링크 생성**

Request:
```json
{
  "url": "https://example.com/very-long-url",
  "userId": 1
}
```

Response (201 Created):
```json
{
  "linkId": 1,
  "originalUrl": "https://example.com/very-long-url",
  "shortCode": "abc123",
  "shortUrl": "http://localhost:8080/abc123",
  "userId": 1,
  "expiresAt": null,
  "createdAt": "2025-10-01T00:00:00Z",
  "updatedAt": "2025-10-01T00:00:00Z"
}
```

#### GET /api/v1/links/short/{shortCode}
**Short Code로 링크 조회**

Response (200 OK):
```json
{
  "linkId": 1,
  "originalUrl": "https://example.com/very-long-url",
  "shortCode": "abc123",
  "shortUrl": "http://localhost:8080/abc123",
  "userId": 1,
  "createdAt": "2025-10-01T00:00:00Z"
}
```

#### DELETE /api/v1/links/{linkId}
**링크 삭제**

Response: 204 No Content

#### GET /{shortCode}
**리다이렉트 (핵심 기능)**

Response: 302 Found → Location: {originalUrl}
- 클릭 자동 기록 (IP, User-Agent, Referer)
- 만료된 링크는 RuntimeException 발생
- 프로토콜 없는 URL은 자동으로 `https://` 추가

### 2.2 User API

#### POST /api/v1/users
**사용자 생성**

Request:
```json
{
  "email": "user@example.com",
  "name": "John Doe"
}
```

Response (201 Created):
```json
{
  "userId": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "createdAt": "2025-10-01T00:00:00Z",
  "updatedAt": "2025-10-01T00:00:00Z"
}
```

#### GET /api/v1/users/{userId}
**사용자 조회**

#### GET /api/v1/users/email/{email}
**이메일로 사용자 조회**

#### GET /api/v1/users/name/{name}
**이름으로 사용자 조회**

#### PUT /api/v1/users/{userId}
**사용자 수정**

Request:
```json
{
  "name": "Jane Doe"
}
```

#### DELETE /api/v1/users/{userId}
**사용자 삭제**

Response: 204 No Content

### 2.3 Monitoring API

#### GET /api/v1/monitoring/links
**모든 링크 목록 조회**

Response (200 OK):
```json
[
  {
    "linkId": 1,
    "userId": 1,
    "originalUrl": "https://www.google.com",
    "shortCode": "google",
    "shortUrl": "http://localhost:8080/google",
    "expiresAt": null,
    "createdAt": "2025-10-01T00:00:00Z",
    "updatedAt": "2025-10-01T00:00:00Z"
  }
]
```

#### GET /api/v1/monitoring/links/{linkId}/clicks
**링크 클릭 통계 조회**

Response (200 OK):
```json
{
  "linkId": 1,
  "count": 3,
  "clicks": [
    {
      "clickId": 1,
      "linkId": 1,
      "clickedAt": "2025-10-01T00:00:00Z",
      "ipAddress": "192.168.1.100",
      "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
      "referer": "https://twitter.com"
    }
  ]
}
```

### 2.4 Swagger UI
- **URL**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- 모든 API 엔드포인트 문서화 완료

---

## 3. 테스트 방법과 범위

### 3.1 테스트 구조

```
총 84개 테스트 (100% 통과)
├── Repository Tests (34개)
│   ├── UserJdbcRepositoryTest (9개)
│   ├── LinkJdbcRepositoryTest (11개)
│   ├── LinkClickJdbcRepositoryTest (13개)
│   └── SnowflakeIdGeneratorTest (1개)
├── Service Tests (34개)
│   ├── DefaultUserReaderTest (9개)
│   ├── DefaultUserWriterTest (2개)
│   ├── DefaultLinkReaderTest (2개)
│   ├── DefaultLinkWriterTest (8개)
│   ├── DefaultLinkClickReaderTest (3개)
│   ├── DefaultLinkClickWriterTest (1개)
│   └── SnowflakeIdGeneratorTest (9개)
└── API Tests (16개)
    ├── UserControllerTest (9개)
    └── LinkControllerTest (7개)
```

### 3.2 Repository Layer 테스트
**전략**: Spring Data JDBC + H2 In-Memory DB

**UserJdbcRepositoryTest** (9개)
- CRUD 기본 동작
- Email/Name 검색
- 존재 여부 확인

**LinkJdbcRepositoryTest** (11개)
- CRUD 기본 동작
- ShortCode/OriginalUrl 검색
- UserId별 조회
- 중복 ShortCode 체크

**LinkClickJdbcRepositoryTest** (13개)
- 클릭 기록 저장
- LinkId별 조회/카운트
- 시간순 정렬

**SnowflakeIdGeneratorTest** (1개)
- 동시성 테스트 (1000개 ID 생성 시 중복 없음)

### 3.3 Service Layer 테스트
**전략**: Mockito + 단위 테스트

**Reader 테스트**
- Optional 반환 처리
- 예외 상황 (NotFound)
- 다양한 조회 조건

**Writer 테스트**
- 생성/수정/삭제
- ShortCode 생성 로직
- Snowflake ID 생성

### 3.4 API Layer 테스트
**전략**: Standalone MockMvc + Mockito

**특징**:
- Spring Context 없는 빠른 단위 테스트
- Status Code + Response Spec 동시 검증
- JSON 직렬화/역직렬화 테스트
- Validation 테스트 (@NotBlank, @NotNull)

**UserControllerTest** (9개)
- POST: 생성 성공/실패 (Validation)
- GET: ID/Email/Name 조회 성공/실패
- PUT: 수정 성공/실패
- DELETE: 삭제 성공

**LinkControllerTest** (7개)
- POST: 링크 생성 성공/실패
- GET: ShortCode 조회
- DELETE: 삭제
- GET /{shortCode}: 리다이렉트 성공/만료/무제한

### 3.5 테스트 실행 방법

```bash
# 전체 테스트
./gradlew test

# 모듈별 테스트
./gradlew :modules:repository-jdbc:test
./gradlew :modules:service:test
./gradlew :modules:api:test

# 특정 클래스 테스트
./gradlew :modules:api:test --tests "LinkControllerTest"
```

### 3.6 테스트 커버리지
- **Repository**: CRUD + 검색 + 동시성 완벽 커버
- **Service**: 비즈니스 로직 + 예외 처리 완벽 커버
- **API**: HTTP 계층 + Validation + 직렬화 완벽 커버

---

## 4. 차별점

### 4.1 아키텍처
**헥사고날 아키텍처 + CQRS 패턴**
- 명확한 계층 분리: Model → Infrastructure → Service → API
- Reader/Writer 분리로 읽기/쓰기 최적화
- 포트/어댑터 패턴으로 기술 스택 교체 용이

### 4.2 멀티모듈 구조
**8개 모듈로 책임 분리**
```
modules/
├── model              # 순수 도메인 모델
├── exception          # 도메인 예외
├── infrastructure     # 포트 인터페이스
├── service            # CQRS 비즈니스 로직
├── repository-jdbc    # JDBC 어댑터
├── api                # REST API + DTO
├── schema             # DB 스키마
└── application-api    # Spring Boot 실행
```

**장점**:
- 도메인 로직이 기술 스택에 독립적
- 모듈별 독립 배포 가능
- 테스트 격리 용이

### 4.3 확장 가능한 ID 생성 전략
**Snowflake Algorithm**
- 분산 환경에서 충돌 없는 ID 생성
- 64비트 구조: Timestamp(41) + MachineId(10) + Sequence(12)
- 초당 409만개 ID 생성 가능
- Base62 인코딩으로 짧고 읽기 쉬운 코드 변환

**동시성 보장**:
- `AtomicLong` 기반 Sequence 관리
- 테스트로 검증된 동시성 안전성

### 4.4 현대적인 테스트 전략
**Standalone MockMvc**
- Spring Context 로딩 불필요 → 빠른 실행
- `@ExtendWith(MockitoExtension.class)` + `@Mock` + `@InjectMocks`
- 순수 단위 테스트로 격리성 보장

**테스트 자동화 스크립트**
- `.claude/tasks/multi_module/test/` 하위 스크립트
- LLM 기반 테스트 코드 자동 생성
- 레퍼런스 클래스 추론 및 패턴 적용

### 4.5 모니터링 & 관찰성
**실시간 통계 API**
- 링크별 클릭 수, 클릭 기록 추적
- IP, User-Agent, Referer 분석 가능
- 확장 가능한 구조 (향후 대시보드 연동 가능)

**Swagger 통합**
- 모든 API 자동 문서화
- 즉시 테스트 가능한 UI 제공
- OpenAPI 3.0 스펙 준수

### 4.6 프로덕션 준비
**만료 시간 관리**
- 링크별 만료 시간 설정
- 리다이렉트 시 자동 체크

**프로토콜 자동 보정**
- `google.com` → `https://google.com` 자동 변환
- 사용자 실수 방지

**에러 핸들링**
- GlobalExceptionHandler로 중앙 집중 처리
- 의미있는 에러 응답

**데이터베이스**
- H2 In-Memory (개발/테스트)
- 프로덕션 DB 교체 용이 (interface 기반 설계)

### 4.7 LLM 기반 개발 자동화
**Claude Code 스크립트**
- 도메인 추가 자동화 (`add_domain_to_*.md`)
- 테스트 코드 자동 생성 (`add_*_test.md`)
- 자연어로 커스텀 요청 가능
- 반복 작업 제거 → 개발 속도 향상

---

## 부록: 프로젝트 구조

```
multimodule_quick_setup/
├── modules/
│   ├── model/                # 도메인 모델
│   │   └── src/main/java/io/multi/hello/model/
│   │       ├── user/         # User, UserIdentity, UserModel
│   │       ├── link/         # Link, LinkIdentity, LinkModel
│   │       └── linkclick/    # LinkClick, LinkClickIdentity
│   ├── exception/            # 도메인 예외
│   │   └── src/main/java/io/multi/hello/exception/
│   │       ├── user/         # UserNotFoundException
│   │       └── link/         # LinkNotFoundException
│   ├── infrastructure/       # 포트 인터페이스
│   │   └── src/main/java/io/multi/hello/infrastructure/
│   │       ├── user/repository/        # UserRepository
│   │       ├── link/repository/        # LinkRepository
│   │       └── linkclick/repository/   # LinkClickRepository
│   ├── service/              # CQRS 비즈니스 로직
│   │   └── src/main/java/io/multi/hello/service/
│   │       ├── user/         # UserReader, UserWriter + Impl
│   │       ├── link/         # LinkReader, LinkWriter + Impl
│   │       │   └── url/      # SnowflakeIdGenerator, Base62Encoder
│   │       └── linkclick/    # LinkClickReader, LinkClickWriter + Impl
│   ├── repository-jdbc/      # Spring Data JDBC 어댑터
│   │   └── src/main/java/io/multi/hello/jdbc/
│   │       ├── user/         # UserJdbcRepository, UserEntity
│   │       ├── link/         # LinkJdbcRepository, LinkEntity
│   │       └── linkclick/    # LinkClickJdbcRepository, LinkClickEntity
│   ├── api/                  # REST API
│   │   └── src/main/java/io/multi/hello/api/
│   │       ├── user/         # UserController + DTOs
│   │       ├── link/         # LinkController + DTOs
│   │       ├── monitoring/   # MonitoringController + DTOs
│   │       └── config/       # GlobalExceptionHandler
│   ├── schema/               # DB 스키마
│   │   └── src/main/resources/
│   │       ├── schema.sql    # DDL
│   │       └── data.sql      # 샘플 데이터
│   └── application-api/      # Spring Boot 실행
│       ├── src/main/java/io/multi/hello/application/
│       │   └── MyProjecApplication.java
│       └── src/main/resources/
│           └── application.yml
├── .claude/                  # LLM 자동화 스크립트
│   └── tasks/multi_module/
│       ├── init/             # 모듈 초기화
│       ├── add/              # 도메인 추가
│       └── test/             # 테스트 생성
└── README.md                 # 프로젝트 가이드
```

---

## 실행 방법

```bash
# 애플리케이션 실행
./gradlew :modules:application-api:bootRun

# Swagger UI 접속
open http://localhost:8080/swagger-ui.html

# Short URL 테스트
curl http://localhost:8080/google
# → https://www.google.com 으로 리다이렉트
```
