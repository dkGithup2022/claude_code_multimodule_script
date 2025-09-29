# 프로젝트 초기화 가이드

## 권장 작업 순서

### 1단계: 새 멀티모듈 프로젝트 생성
```bash
" run init_multimodule_project.md . 프로젝트명: my-app, 루트모듈: core, 패키지: com.mycompany"
```

**필수 파라미터**:
- `프로젝트명`: kebab-case (예: my-app, user-service)
- `루트모듈`: 소문자 (예: core, corehr, domain)
- `패키지`: 도트 표기법 (예: com.mycompany, com.searchkim)

**생성 결과**:
```
my-app/
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
├── gradlew
└── core/
    └── (준비된 디렉토리)
```

### 2단계: 각 모듈 초기화 (의존성 순서대로)

#### 2-1. Model 모듈 초기화
```bash
"init_model_module.md 실행해줘"
```
- **파라미터**: 없음 (자동 감지)
- **생성 파일**: AuditProps, Example 도메인 클래스들
- **의존성**: 없음

#### 2-2. Exception 모듈 초기화
```bash
"run init_exception_module.md "
```
- **파라미터**: 없음
- **생성 파일**: ExampleNotFoundException
- **의존성**: 없음

#### 2-3. Infrastructure 모듈 초기화
```bash
"run init_infrastructure_module.md"
```
- **파라미터**: 없음
- **생성 파일**: ExampleRepository 인터페이스
- **의존성**: model, exception

#### 2-4. Service 모듈 초기화
```bash
"run init_service_module.md"
```
- **파라미터**: 없음
- **생성 파일**: ExampleLookUpService
- **의존성**: model, exception, infrastructure

#### 2-5. Repository-JDBC 모듈 초기화
```bash
"run init_repository_jdbc_module.md"
```
- **파라미터**: 없음
- **생성 파일**: ExampleJdbcRepository, ExampleEntity, ExampleRowMapper
- **의존성**: infrastructure

#### 2-6. API 모듈 초기화
```bash
"run init_api_module.md"
```
- **파라미터**: 없음
- **생성 파일**: ExampleApiController, ExampleResponse, DTO들
- **의존성**: service

#### 2-7. Schema 모듈 초기화
```bash
"run init_schema_module.md"
```
- **파라미터**: 없음
- **생성 파일**: schema.sql, data.sql
- **의존성**: 없음 (순수 리소스)

#### 2-8. Application-API 모듈 초기화
```bash
"run init_application_api_module.md "
```
- **파라미터**: 없음
- **생성 파일**: Spring Boot 메인 클래스, 설정 파일
- **의존성**: 모든 모듈

### 3단계: 빌드 검증
```bash
./gradlew clean build
```

## 완성된 프로젝트 구조

```
my-app/
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
├── gradlew
└── core/
    ├── model/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/mycompany/
    │       ├── AuditProps.java
    │       └── example/
    │           ├── Example.java
    │           ├── ExampleModel.java
    │           └── ExampleIdentity.java
    ├── exception/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/mycompany/exception/
    │       └── ExampleNotFoundException.java
    ├── infrastructure/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/mycompany/example/repository/
    │       └── ExampleRepository.java
    ├── service/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/mycompany/example/
    │       └── ExampleLookUpService.java
    ├── repository-jdbc/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/mycompany/example/repository/
    │       ├── ExampleJdbcRepository.java
    │       ├── ExampleEntity.java
    │       └── ExampleRowMapper.java
    ├── api/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/mycompany/example/
    │       ├── ExampleApiController.java
    │       └── dto/
    │           ├── ExampleResponse.java
    │           ├── ExampleCreateRequest.java
    │           └── ExampleUpdateRequest.java
    └── application-api/
        ├── build.gradle.kts
        └── src/main/java/com/mycompany/application/
            └── MyAppApplication.java
```

## 주의사항

### 실행 순서 중요
- **반드시 의존성 순서대로** 모듈을 초기화해야 함
- 순서를 바꾸면 빌드 실패 가능

### 자동 감지 기능
- 2단계부터는 settings.gradle.kts와 build.gradle.kts를 자동으로 분석
- 프로젝트명, 루트모듈명, 패키지명이 자동으로 적용됨

### 빌드 검증 필수
- 각 모듈 초기화 후 `./gradlew :core:{module}:build`로 개별 검증 권장
- 전체 초기화 완료 후 `./gradlew clean build`로 최종 검증

## 다음 단계
초기화 완료 후 [how-to-add-code.md](how-to-add-code.md)를 참고하여 실제 도메인 코드를 추가하세요.