# 🎫 Coupon Management System

> 대규모 동시 요청 환경에서 안전한 쿠폰 발급을 보장하는 멀티모듈 기반 쿠폰 관리 시스템

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg)](https://gradle.org/)

---

## 📌 프로젝트 개요

쿠폰 발급 및 관리를 위한 REST API 시스템입니다. 쿠폰 발행에 대한 동시성을 캐어합니다.  
e.g) 1000명이 동시에 쿠폰을 발급받아도 정확히 설정된 수량만큼만 발급되도록 보장합니다.

### 주요 기능
- ✅ 쿠폰 등록 및 조회
- ✅ 쿠폰 발급 (동시성 제어)
- ✅ 사용자 관리 (CRUD)
- ✅ 발급 내역 조회

---

## 🎯 동시성 캐어 요소 

### 1. 동시성 제어 (Race Condition)

**문제 상황**
```
쿠폰 수량: 1000개
동시 요청: 1000명

❌ 잘못된 구현: 1000개 이상 발급될 수 있음, 1000개 미만이 발행 ( 여유분이 있는데 exception 혹은 중복 발행)
✅ 올바른 구현: 정확히 1000개만 발급
```

**해결 방법**
- **DB 레벨 원자적 연산**: `UPDATE ... WHERE` 조건절로 수량 체크 + 증가를 한 번에 처리
- WHERE 절 조건을 만족하는 경우에만 UPDATE 실행 → affected rows로 성공/실패 판단

**구현 코드 (CouponRepository)**

```java
 @Override
    @Transactional
    public CouponIssuance issueCoupon(Long couponId, Long userId) {
        log.info("Issuing coupon: couponId={}, userId={}", couponId, userId);

        validateUser(userId);

        // 1. 중복 발행 체크
        checkDuplicateIssuance(couponId, userId);

        // 2. 쿠폰 증가 시도 (DB 레벨에서 동시성 제어)
        // 동시성 제어를 db row lock 수준으로만 캐어. 
        var affected = couponRepository.tryIncreaseIssuedCount(couponId);
        if(!affected) {
            // 실패 원인 파악 - 에러메세지 만들기 .
            var coupon = couponReader.findById(couponId);
            if (coupon == null) {
                throw new CouponNotFoundException(couponId);
            }
            if (coupon.getIssuedCount() >= coupon.getTotalQuantity()) {
                throw new CouponSoldOutException(couponId);
            }
            if (Instant.now().isAfter(coupon.getEndDate()) ||
                Instant.now().isBefore(coupon.getStartDate())) {
                throw new CouponExpiredException(couponId);
            }
            throw new CouponIssuanceFailedException(couponId);
        }

        // 3. 발행 기록 생성
        var coupon = couponReader.findById(couponId);
        var couponIssuance = newCouponIssuance(coupon, userId);

        return couponIssuanceRepository.save(couponIssuance);
    }


```
```java
@Query("""
    UPDATE coupons
    SET issued_count = issued_count + 1
    WHERE coupon_id = :couponId
      AND issued_count < total_quantity
      AND :now BETWEEN start_date AND end_date
    """)
boolean tryIncreaseIssuedCount(@Param("couponId") Long couponId,
                                @Param("now") Instant now);
```

### 2. 테스트 검증

**동시성 테스트 (`DefaultCouponIssuerTest`)**
```java
@Test
@Transactional
void race() {
    // Given: 1000개 쿠폰, 1000명 사용자
    var userIds = createUsers(1000);
    var coupon = createCoupon(1000);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    // When: 1000개 쓰레드 동시 실행
    ExecutorService pool = Executors.newFixedThreadPool(poolSize);
    CountDownLatch startGate = new CountDownLatch(1);
    // ... 동시 발급 시도

    // Then: 정확히 1000개만 발급 성공
    assertThat(successCount.get()).isEqualTo(1000);
    assertThat(coupon.getIssuedCount()).isEqualTo(1000);
}
```

**검증 항목**
- ✅ 정확히 1000개만 발급 성공
- ✅ `issued_count`가 정확히 1000
- ✅ DB에 1000개의 발급 기록만 존재
- ✅ 중복 발급 없음

---

## 🏗️ 아키텍처


### 멀티모듈 구조 (8개 모듈)

```
modules/
├── model               # 도메인 모델 (User, Coupon, CouponIssuance)
├── exception           # 공통 예외
├── infrastructure      # 포트 인터페이스
├── service             # 비즈니스 로직 (CQRS)
├── repository-jdbc     # JDBC 어댑터
├── api                 # REST API 컨트롤러
├── schema              # DB 스키마 (DDL, 초기 데이터)
└── application-api     # Spring Boot 실행 애플리케이션
```



---

## 🛠️ 기술 스택

| Category | Technology |
|----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.2.0 |
| Database | H2 (In-memory) |
| ORM | Spring Data JDBC |
| Build Tool | Gradle 8.x |
| Documentation | SpringDoc OpenAPI (Swagger) |
| Testing | JUnit 5, Mockito, AssertJ |
| Architecture | Hexagonal Architecture, CQRS |

---

## 📊 데이터베이스 스키마

### 테이블 구조

**users**
```sql
user_id BIGINT AUTO_INCREMENT PRIMARY KEY
email VARCHAR(255) UNIQUE
name VARCHAR(255)
created_at, updated_at TIMESTAMP(6)
```

**coupons**
```sql
coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY
name VARCHAR(255)
discount_amount INT
total_quantity INT
issued_count INT DEFAULT 0
user_id BIGINT
start_date, end_date TIMESTAMP(6)
created_at, updated_at TIMESTAMP(6)
```

**coupon_issuances**
```sql
issuance_id BIGINT AUTO_INCREMENT PRIMARY KEY
coupon_id BIGINT
user_id BIGINT
issued_at, used_at TIMESTAMP(6)
status VARCHAR(50)
created_at, updated_at TIMESTAMP(6)

UNIQUE (coupon_id, user_id)  -- 중복 발급 방지
```

### 주요 제약조건
- `users.email`: UNIQUE
- `coupon_issuances (coupon_id, user_id)`: UNIQUE (중복 발급 방지)
- Foreign Keys 없음 (애플리케이션 레벨에서 관리)

---

## 🚀 실행 방법

### 1. 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew :modules:application-api:bootRun
```

### 2. API 문서 확인

애플리케이션 실행 후 아래 URL 접속:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:coupondb`
  - Username: `sa`
  - Password: (비어있음)

### 3. API 사용 예시

**쿠폰 등록**
```bash
POST http://localhost:8080/api/coupons
Content-Type: application/json

{
  "name": "Welcome Coupon",
  "discountAmount": 10000,
  "totalQuantity": 1000,
  "issuedCount": 0,
  "userId": 1,
  "startDate": "2025-10-01T00:00:00Z",
  "endDate": "2025-12-31T23:59:59Z"
}
```

**쿠폰 발급**
```bash
POST http://localhost:8080/api/coupons/{couponId}/issue
Content-Type: application/json

{
  "userId": 1
}
```

---

## 🧪 테스트

### 전체 테스트 실행

```bash
./gradlew test
```



---

## 📁 프로젝트 구조

```
multimodule_quick_setup/
├── build.gradle.kts                    # 루트 빌드 스크립트
├── settings.gradle.kts                 # 모듈 설정
├── modules/
│   ├── model/                          # 도메인 모델
│   │   └── src/main/java/.../model/
│   │       ├── user/
│   │       │   ├── User.java
│   │       │   ├── UserIdentity.java
│   │       │   └── UserModel.java
│   │       ├── coupon/
│   │       │   ├── Coupon.java
│   │       │   ├── CouponIdentity.java
│   │       │   └── CouponModel.java
│   │       └── couponissuance/
│   │           ├── CouponIssuance.java
│   │           ├── CouponIssuanceIdentity.java
│   │           ├── CouponIssuanceModel.java
│   │           └── CouponIssuanceStatus.java
│   │
│   ├── exception/                      # 예외 클래스
│   │   └── src/main/java/.../exception/coupon/
│   │       ├── CouponNotFoundException.java
│   │       ├── CouponExpiredException.java
│   │       ├── CouponSoldOutException.java
│   │       └── DuplicateCouponIssuanceException.java
│   │
│   ├── infrastructure/                 # 포트 인터페이스
│   │   └── src/main/java/.../infrastructure/
│   │       ├── coupon/repository/
│   │       │   └── CouponRepository.java
│   │       ├── user/repository/
│   │       │   └── UserRepository.java
│   │       └── couponissuance/repository/
│   │           └── CouponIssuanceRepository.java
│   │
│   ├── service/                        # 비즈니스 로직
│   │   ├── src/main/java/.../service/
│   │   │   ├── coupon/
│   │   │   │   ├── CouponReader.java
│   │   │   │   ├── CouponRegister.java
│   │   │   │   └── impl/
│   │   │   │       ├── DefaultCouponReader.java
│   │   │   │       └── DefaultCouponRegister.java
│   │   │   ├── coupon/issuance/
│   │   │   │   ├── CouponIssuer.java         # 🔥 동시성 제어 핵심
│   │   │   │   └── impl/
│   │   │   │       └── DefaultCouponIssuer.java
│   │   │   └── user/
│   │   │       ├── UserReader.java
│   │   │       └── UserWriter.java
│   │   └── src/test/java/                    # 🧪 테스트
│   │       └── .../DefaultCouponIssuerTest.java
│   │
│   ├── repository-jdbc/                # JDBC 어댑터
│   │   └── src/main/java/.../jdbc/
│   │       ├── coupon/repository/
│   │       │   ├── CouponEntity.java
│   │       │   ├── CouponEntityRepository.java
│   │       │   └── CouponJdbcRepository.java
│   │       ├── user/repository/
│   │       └── couponissuance/repository/
│   │
│   ├── api/                            # REST API
│   │   └── src/main/java/.../api/
│   │       ├── coupon/
│   │       │   ├── CouponApiController.java
│   │       │   └── dto/
│   │       │       ├── CouponRegisterRequest.java
│   │       │       └── CouponResponse.java
│   │       ├── user/
│   │       │   ├── UserApiController.java
│   │       │   └── dto/
│   │       └── issuance/
│   │           ├── CouponIssuanceApiController.java
│   │           └── dto/
│   │
│   ├── schema/                         # DB 스키마
│   │   └── src/main/resources/
│   │       ├── schema.sql              # DDL
│   │       └── data.sql                # 초기 데이터
│   │
│   └── application-api/                # Spring Boot 앱
│       ├── src/main/java/.../application/
│       │   └── MyProjectApplication.java
│       └── src/main/resources/
│           └── application.yml
└── README.md
```

---

## 🎨 주요 API 명세

### 쿠폰 관리

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/coupons` | 쿠폰 등록 |
| GET | `/api/coupons/{couponId}` | 쿠폰 조회 |
| GET | `/api/coupons?ownerId={ownerId}` | 소유자별 쿠폰 목록 조회 |

### 쿠폰 발급

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/coupons/{couponId}/issue` | 쿠폰 발급 ⭐ |
| GET | `/api/coupons/issuances/{issuanceId}` | 발급 내역 조회 |
| GET | `/api/coupons/issuances` | 전체 발급 내역 |
| GET | `/api/coupons/{couponId}/issuances` | 쿠폰별 발급 내역 |
| GET | `/api/users/{userId}/coupons` | 사용자별 발급 내역 |

### 사용자 관리

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | 사용자 생성 |
| GET | `/api/users/{userId}` | 사용자 조회 |
| GET | `/api/users` | 전체 사용자 조회 |
| PUT | `/api/users/{userId}` | 사용자 수정 |
| DELETE | `/api/users/{userId}` | 사용자 삭제 |

---
