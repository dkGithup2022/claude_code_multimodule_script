# 코드 스타일 및 컨벤션

## 네이밍 컨벤션
- **패키지**: `io.example.coupon.<module>.<domain>`
- **도메인 클래스**: 
  - `XxxIdentity`: ID Value Object
  - `Xxx`: 도메인 엔티티
  - `XxxModel`: 도메인 모델 인터페이스
- **서비스 레이어**: 
  - `XxxReader`: 조회 서비스 인터페이스
  - `XxxWriter`: 쓰기 서비스 인터페이스
  - `XxxRegister`, `XxxIssuer` 등: 특화된 쓰기 서비스
  - `DefaultXxxReader`, `DefaultXxxWriter`: 구현체 (impl 패키지)
- **리포지토리 레이어**:
  - `XxxRepository`: 인터페이스 (infrastructure 모듈)
  - `XxxJdbcRepository`: JDBC 구현체
  - `XxxEntity`: JDBC 엔티티
  - `XxxEntityRepository`: Spring Data JDBC Repository
- **API 레이어**:
  - `XxxApiController`: REST 컨트롤러
  - `XxxRequest`: 요청 DTO
  - `XxxResponse`: 응답 DTO

## 아키텍처 패턴
- **Hexagonal Architecture**: 포트/어댑터 패턴
- **CQRS**: 명령(Command)/조회(Query) 분리
- **계층 분리**: API → Service → Infrastructure → Model

## 코드 작성 규칙
- Lombok 사용 권장 (`@Data`, `@Builder`, `@AllArgsConstructor` 등)
- Java 21 문법 사용
- UTF-8 인코딩
- Constructor Injection 우선 (필드 주입 지양)
- 인터페이스 기반 설계

## 파일 구조
- 도메인별 패키지 분리: `coupon/`, `user/`, `couponissuance/`
- DTO는 각 컨트롤러 하위 `dto/` 패키지
- 구현체는 `impl/` 패키지
