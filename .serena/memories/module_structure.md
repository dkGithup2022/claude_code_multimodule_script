# 모듈 구조

## 8개 모듈 구성 (Hexagonal Architecture)

### 1. model
- **역할**: 도메인 모델, Value Objects, 비즈니스 규칙
- **패키지**: `io.example.coupon.model`
- **주요 클래스**:
  - `AuditProps.java`: 공통 감사 속성 (createdAt, updatedAt)
  - `coupon/`: Coupon, CouponIdentity, CouponModel
  - `user/`: User, UserIdentity, UserModel
  - `couponissuance/`: CouponIssuance, CouponIssuanceIdentity, CouponIssuanceModel, CouponIssuanceStatus

### 2. exception
- **역할**: 공통 예외 클래스
- **패키지**: `io.example.coupon.exception`

### 3. infrastructure
- **역할**: 포트(Port) 인터페이스 정의
- **패키지**: `io.example.coupon.infrastructure`
- **주요 인터페이스**:
  - `coupon/repository/CouponRepository`
  - `user/repository/UserRepository`
  - `couponissuance/repository/CouponIssuanceRepository`

### 4. service
- **역할**: CQRS 패턴 비즈니스 로직
- **패키지**: `io.example.coupon.service`
- **구조**: Reader/Writer 분리
  - `coupon/`: CouponReader, CouponRegister, CouponIssuer
  - `user/`: UserReader, UserWriter
  - `coupon/issuance/`: CouponIssuanceReader
  - `impl/`: 구현체 (DefaultXxxReader, DefaultXxxWriter)

### 5. repository-jdbc
- **역할**: Spring Data JDBC 어댑터 (Infrastructure 구현체)
- **패키지**: `io.example.coupon.jdbc`
- **구조**: Entity + EntityRepository + JdbcRepository 패턴

### 6. api
- **역할**: REST API 컨트롤러, DTO
- **패키지**: `io.example.coupon.api`
- **구조**:
  - `coupon/`: CouponApiController, CouponRegisterRequest, CouponResponse
  - `user/`: UserApiController, UserCreateRequest, UserUpdateRequest, UserResponse
  - `issuance/`: CouponIssuanceApiController, CouponIssueRequest, CouponIssuanceResponse
  - `ApiAutoConfiguration.java`: 자동 설정

### 7. schema
- **역할**: 데이터베이스 스키마 및 초기 데이터
- **파일**:
  - `schema.sql`: users, coupons, coupon_issuances 테이블 정의
  - `data.sql`: 초기 테스트 데이터

### 8. application-api
- **역할**: Spring Boot 실행 애플리케이션
- **패키지**: `io.example.coupon.application`
- **설정**: application.yml (H2 DB, Swagger UI)

## 의존성 관계
```
application-api → api, schema
api → service, model
service → infrastructure, model
repository-jdbc → infrastructure, model
infrastructure → model
exception (모든 모듈에서 참조 가능)
```
