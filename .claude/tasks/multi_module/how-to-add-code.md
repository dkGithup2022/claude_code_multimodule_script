# 도메인 코드 추가 가이드

## Foo 데이터에 대한 CRUD 추가 예시

기존 초기화된 프로젝트에 `Foo` 도메인의 완전한 CRUD API를 추가하는 과정입니다.

### 권장 작업 순서 (의존성 방향 준수)

#### 1단계: Model 모듈에 Foo 도메인 추가
```bash
"add_domain_to_model.md 실행해줘"
```

**대화형 입력**:
```
도메인명: Foo
```

**생성 파일**:
- `core/model/src/main/java/com/mycompany/foo/FooIdentity.java`
- `core/model/src/main/java/com/mycompany/foo/FooModel.java`
- `core/model/src/main/java/com/mycompany/foo/Foo.java`

#### 2단계: Exception 모듈에 Foo 예외 추가
```bash
"run add_domain_to_exception.md "
```

**대화형 입력**:
```
도메인명: Foo
예외명: NotFound
```

**생성 파일**:
- `core/exception/src/main/java/com/mycompany/exception/FooNotFoundException.java`

#### 3단계: Infrastructure 모듈에 Repository 인터페이스 추가
```bash
"run add_domain_to_infrastructure.md"
```

**대화형 입력**:
```
도메인명: Foo
하위 엔티티: (빈 입력)
```

**생성 파일**:
- `core/infrastructure/src/main/java/com/mycompany/foo/repository/FooRepository.java`

#### 4단계: Service 모듈에 비즈니스 로직 추가
```bash
"add_domain_to_service.md 실행해줘"
```

**대화형 입력**:
```
도메인명: Foo
하위 엔티티: (빈 입력)
```

**생성 파일**:
- `core/service/src/main/java/com/mycompany/foo/FooLookUpService.java`

#### 5단계: Repository-JDBC 모듈에 JDBC 구현체 추가
```bash
"add_domain_to_repository_jdbc.md 실행해줘"
```

**대화형 입력**:
```
도메인명: Foo
하위 엔티티: (빈 입력)
```

**생성 파일**:
- `core/repository-jdbc/src/main/java/com/mycompany/foo/repository/FooJdbcRepository.java`
- `core/repository-jdbc/src/main/java/com/mycompany/foo/repository/FooEntity.java`
- `core/repository-jdbc/src/main/java/com/mycompany/foo/repository/FooRowMapper.java`

#### 6단계: API 모듈에 REST API 추가
```bash
"add_domain_to_api.md 실행해줘"
```

**대화형 입력**:
```
도메인명: Foo
하위 엔티티: (빈 입력)
```

**생성 파일**:
- `core/api/src/main/java/com/mycompany/foo/FooApiController.java`
- `core/api/src/main/java/com/mycompany/foo/dto/FooResponse.java`
- `core/api/src/main/java/com/mycompany/foo/dto/FooCreateRequest.java`
- `core/api/src/main/java/com/mycompany/foo/dto/FooUpdateRequest.java`

## 2-depth 지원 예시: User 도메인 + 하위 엔티티

User 도메인에 Profile과 Setting 하위 엔티티를 추가하는 경우:

### 3단계: Infrastructure (2-depth 활용)
```bash
"add_domain_to_infrastructure.md 실행해줘"
```

**대화형 입력**:
```
도메인명: User
하위 엔티티: Profile, Setting
```

**생성 파일**:
- `UserProfileRepository.java`
- `UserSettingRepository.java`

### 4단계: Service (2-depth 활용)
```bash
"add_domain_to_service.md 실행해줘"
```

**대화형 입력**:
```
도메인명: User
하위 엔티티: Profile, Setting
```

**생성 파일**:
- `UserProfileLookUpService.java`
- `UserSettingLookUpService.java`

### 5단계: Repository-JDBC (2-depth 활용)
```bash
"add_domain_to_repository_jdbc.md 실행해줘"
```

**대화형 입력**:
```
도메인명: User
하위 엔티티: Profile, Setting
```

**생성 파일** (6개):
- `UserProfileJdbcRepository.java` + `UserProfileEntity.java` + `UserProfileRowMapper.java`
- `UserSettingJdbcRepository.java` + `UserSettingEntity.java` + `UserSettingRowMapper.java`

### 6단계: API (2-depth 활용)
```bash
"add_domain_to_api.md 실행해줘"
```

**대화형 입력**:
```
도메인명: User
하위 엔티티: Profile, Setting
```

**생성 파일** (8개):
- `UserProfileApiController.java` + 3개 DTO
- `UserSettingApiController.java` + 3개 DTO

## 대화형 지원 스크립트 특징

### ✅ 대화형 지원 스크립트
| 스크립트 | 입력 항목 | 2-depth 지원 |
|---------|----------|-------------|
| `add_domain_to_model.md` | 도메인명 | ❌ |
| `add_domain_to_exception.md` | 도메인명, 예외명 | ❌ |
| `add_domain_to_infrastructure.md` | 도메인명, 하위엔티티 | ✅ |
| `add_domain_to_service.md` | 도메인명, 하위엔티티 | ✅ |
| `add_domain_to_repository_jdbc.md` | 도메인명, 하위엔티티 | ✅ |
| `add_domain_to_api.md` | 도메인명, 하위엔티티 | ✅ |

### 하위 엔티티 입력 방법 (2-depth 지원 스크립트)
- **빈 입력**: 도메인 자체만 처리 (예: `UserRepository`)
- **단일 입력**: 하나의 하위 엔티티 (예: `Profile` → `UserProfileRepository`)
- **다중 입력**: 여러 하위 엔티티 (예: `Profile, Setting` → `UserProfileRepository, UserSettingRepository`)

### 의존성 자동 검증
각 스크립트는 실행 전에 필요한 의존성을 자동으로 검증합니다:
- **Service 추가 시**: Repository 인터페이스와 Exception 클래스 존재 확인
- **Repository-JDBC 추가 시**: Infrastructure Repository 인터페이스 존재 확인
- **API 추가 시**: Service 클래스 존재 확인

## 최종 검증

모든 단계 완료 후:
```bash
./gradlew clean build
```

## 생성된 REST API 엔드포인트 (Foo 예시)

```
GET    /api/foos              - 전체 조회
GET    /api/foos/{id}         - 단일 조회
GET    /api/foos/search?name= - 이름 검색
POST   /api/foos              - 생성
PUT    /api/foos/{id}         - 수정
DELETE /api/foos/{id}         - 삭제
GET    /api/foos/{id}/exists  - 존재 여부 확인
GET    /api/foos/count        - 총 개수
```

## 추가 도메인 확장

동일한 과정을 반복하여 여러 도메인을 추가할 수 있습니다:
- User (Profile, Setting, Address)
- Product (Category, Review, Inventory)
- Order (OrderItem, Payment, Delivery)

각 도메인은 독립적으로 관리되며, 필요에 따라 도메인 간 연관관계도 구현 가능합니다.