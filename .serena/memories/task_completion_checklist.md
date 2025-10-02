# 작업 완료 시 체크리스트

## 코드 작성 후 필수 확인 사항

### 1. 빌드 확인
```bash
./gradlew build
```
- 컴파일 에러 없음
- 모든 모듈 빌드 성공

### 2. 테스트 실행
```bash
./gradlew test
```
- 모든 테스트 통과
- 새로운 기능에 대한 테스트 작성 완료

### 3. 코드 스타일 확인
- Lombok 어노테이션 적절히 사용
- 네이밍 컨벤션 준수 (Reader/Writer, Identity, Model 등)
- 패키지 구조 준수 (`io.example.coupon.<module>.<domain>`)

### 4. 아키텍처 준수
- 계층 의존성 규칙 준수 (API → Service → Infrastructure → Model)
- 포트/어댑터 패턴 준수
- CQRS 패턴 준수 (Read/Write 분리)

### 5. 문서화
- Swagger 주석 추가 (API 컨트롤러)
- 복잡한 비즈니스 로직에 주석 추가

## 새 도메인 추가 시 체크리스트
1. Model 모듈: Identity, Entity, Model 클래스
2. Exception 모듈: 도메인 예외 클래스
3. Infrastructure 모듈: Repository 인터페이스
4. Service 모듈: Reader/Writer 인터페이스 및 구현체
5. Repository-JDBC 모듈: Entity, EntityRepository, JdbcRepository
6. API 모듈: Controller, Request/Response DTO
7. Schema 모듈: schema.sql, data.sql 업데이트

## Claude Code 스크립트 활용
- `.claude/tasks/multi_module/` 하위 스크립트 활용
- 초기화: `init_complete_multimodule_project.md`
- 도메인 추가: `add/add_domain_to_*.md`
- 테스트 추가: `test/add_*_test.md`
