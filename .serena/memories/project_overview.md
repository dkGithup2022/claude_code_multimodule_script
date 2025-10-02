# 프로젝트 개요

## 프로젝트 목적
쿠폰 관리 시스템 (Coupon Management System)
- 쿠폰 등록 및 관리
- 사용자 관리
- 쿠폰 발급 및 사용 처리

## 기술 스택
- **언어**: Java 21
- **빌드 도구**: Gradle (Gradle Wrapper 사용)
- **프레임워크**: Spring Boot 3.2.0
- **데이터베이스**: H2 (In-memory)
- **ORM**: Spring Data JDBC
- **문서화**: SpringDoc (Swagger UI)
- **테스트**: JUnit 5, Mockito, AssertJ
- **유틸리티**: Lombok

## 프로젝트 구조
Hexagonal Architecture 기반 멀티모듈 프로젝트

### 주요 도메인
1. **User (사용자)**: 이메일, 이름 관리
2. **Coupon (쿠폰)**: 쿠폰 정보, 할인액, 수량, 유효기간 관리
3. **CouponIssuance (쿠폰 발급)**: 사용자별 쿠폰 발급 및 사용 이력 관리
