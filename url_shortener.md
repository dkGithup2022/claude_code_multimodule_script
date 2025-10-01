---
URL Shortener 서비스 기획서

1. 프로젝트 개요

1.1 서비스 설명

긴 URL을 짧은 URL로 변환하고, 클릭 통계를 제공하는 서비스

1.2 핵심 기능

- 긴 URL → 짧은 URL 변환
- 짧은 URL → 원본 URL 리다이렉트
- 클릭 통계 조회
- 사용자별 링크 관리 (선택)

1.3 기술 스택

- Backend: Spring Boot 3.2 (멀티모듈)
- Database: H2 (개발), PostgreSQL (운영 대비)
- Cache: Redis (클릭 횟수, 자주 사용되는 URL) -> 이거는 인메모리 대체. 
- Architecture: 헥사고날 + CQRS

---
2. 도메인 모델

2.1 Link (단축 URL)

- linkId: Long (PK)
- originalUrl: String (원본 URL)
- shortCode: String (짧은 코드, 예: "abc123")
- userId: Long (생성자, nullable)
- expiresAt: Instant (만료 시간, nullable)
- createdAt: Instant
- updatedAt: Instant

2.2 LinkClick (클릭 기록)

- clickId: Long (PK)
- linkId: Long 
- clickedAt: Instant
- ipAddress: String
- userAgent: String
- referer: String (nullable)

2.3 User (사용자) - Optional Phase 2

- userId: Long (PK)
- email: String
- name: String
- createdAt: Instant

---
3. API 명세

3.1 단축 URL 생성
```
POST /api/links
Request:
{
"originalUrl": "https://example.com/very/long/url",
"customShortCode": "my-link",  // optional
"expiresAt": "2024-12-31T23:59:59Z"  // optional
}
```

```
Response: 201 Created
{
"linkId": 1,
"originalUrl": "https://example.com/very/long/url",
"shortCode": "abc123",
"shortUrl": "http://short.ly/abc123",
"expiresAt": "2024-12-31T23:59:59Z",
"createdAt": "2024-01-01T00:00:00Z"
}
```
3.2 단축 URL 리다이렉트
```
GET /{shortCode}
Response: 302 Found
Location: https://example.com/very/long/url
```
에러:
- 404: 존재하지 않는 shortCode
- 410: 만료된 링크

3.3 링크 정보 조회
```
GET /api/links/{shortCode}
Response: 200 OK
{
"linkId": 1,
"originalUrl": "https://example.com/very/long/url",
"shortCode": "abc123",
"shortUrl": "http://short.ly/abc123",
"clickCount": 150,
"expiresAt": "2024-12-31T23:59:59Z",
"createdAt": "2024-01-01T00:00:00Z"
}
```
3.4 링크 통계 조회
```
GET /api/links/{shortCode}/analytics
Response: 200 OK
{
"linkId": 1,
"shortCode": "abc123",
"totalClicks": 150,
"last24Hours": 45,
"last7Days": 120,
"clicksByDate": [
{"date": "2024-01-01", "clicks": 10},
{"date": "2024-01-02", "clicks": 15}
],
"topReferers": [
{"referer": "google.com", "clicks": 50},
{"referer": "twitter.com", "clicks": 30}
]
}
```
3.5 내 링크 목록 조회 (Phase 2)
```
GET /api/links
Response: 200 OK
{
"links": [
{
"linkId": 1,
"originalUrl": "https://example.com/url1",
"shortCode": "abc123",
"clickCount": 150,
"createdAt": "2024-01-01T00:00:00Z"
}
],
"totalCount": 10
}
```
---
4. 모듈 구조

4.1 모듈 목록
```
modules/
├── model/              # Link, LinkClick, User
├── exception/          # LinkNotFoundException, ExpiredLinkException
├── infrastructure/     # LinkRepository, LinkClickRepository
├── service/            # LinkReader, LinkWriter, LinkClickWriter, AnalyticsReader
├── repository-jdbc/    # JDBC 구현체
├── api/                # REST Controllers
├── schema/             # DDL/DML
└── application-api/    # Spring Boot App
```

4.2 추가 모듈 (선택)
```
modules/
├── cache/              # Redis 캐싱 레이어
└── shortcode-generator/ # 단축코드 생성 알고리즘
```
---
5. 핵심 로직

5.1 Short Code 생성 전략

Option 1: Base62 Encoding
- linkId를 Base62로 인코딩 (0-9, a-z, A-Z)
- 예: linkId=125 → shortCode="b7"
- 장점: 간단, 충돌 없음
- 단점: linkId 예측 가능

Option 2: Random + 충돌 체크
- 6자리 랜덤 문자열 생성
- DB에서 중복 확인, 중복이면 재생성
- 장점: 예측 불가능
- 단점: 충돌 가능성 (매우 낮음)

추천: Option 2 (보안상 더 안전)

5.2 Redis 캐싱 전략

캐시할 데이터:
1. shortCode → originalUrl (TTL: 1시간)
2. shortCode → clickCount (TTL: 5분)

캐시 플로우:
1. GET /{shortCode} 요청
2. Redis에서 originalUrl 조회
3. Hit: 리다이렉트 + 비동기로 클릭 기록
4. Miss: DB 조회 → Redis 저장 → 리다이렉트

5.3 클릭 추적 전략

비동기 처리:
1. 리다이렉트 응답 즉시 반환 (사용자 경험 향상)
2. @Async로 클릭 기록 저장
3. Redis 클릭 카운터 증가 (실시간 집계)
4. 배치로 1시간마다 DB 동기화

---
6. 개발 단계

Phase 1: 기본 기능 (1-2일)

- 멀티모듈 프로젝트 초기화
- Link 도메인 추가
- 단축 URL 생성 API
- 단축 URL 리다이렉트
- 기본 통계 (총 클릭 수)

Phase 2: 고급 기능 (1-2일)

- LinkClick 도메인 추가
- 상세 통계 API (날짜별, Referer별)
- 링크 만료 기능
- Custom short code 지원

Phase 3: 성능 최적화 (1일)

- Redis 캐싱 추가
- 비동기 클릭 처리
- 부하 테스트

Phase 4: 사용자 관리 (선택)

- User 도메인 추가
- 인증/인가
- 사용자별 링크 관리

---
7. 검증 포인트

7.1 멀티모듈 구조 검증

- ✅ 도메인 추가 스크립트 실행
- ✅ 모듈 간 의존성 관리
- ✅ 레이어 분리 (API → Service → Repository)

7.2 기술 확장성 검증

- ✅ Redis 모듈 추가
- ✅ 새로운 Repository 타입 (Redis)
- ✅ 비동기 처리 (@Async)

7.3 실전 시나리오 검증

- ✅ 동시성 처리 (short code 생성 시 충돌)
- ✅ 캐싱 전략
- ✅ 성능 최적화

---
8. 성공 지표

8.1 기능적 목표

- 단축 URL 생성 < 100ms
- 리다이렉트 < 50ms (캐시 사용 시)
- 초당 1000건 리다이렉트 처리

8.2 학습 목표

- 멀티모듈 프로젝트를 스크립트로 빠르게 구축
- 도메인 추가 스크립트 활용
- Redis 같은 새 기술 통합

---