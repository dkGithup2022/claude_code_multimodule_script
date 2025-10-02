# 주요 명령어

## 빌드 및 실행
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew :modules:application-api:bootRun

# 특정 모듈 빌드
./gradlew :modules:api:build
```

## 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :modules:service:test
./gradlew :modules:repository-jdbc:test
```

## 클린 및 재빌드
```bash
# 클린 빌드
./gradlew clean build

# 빌드 캐시 삭제
./gradlew clean
```

## 애플리케이션 접속
- **애플리케이션**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:coupondb`
  - Username: `sa`
  - Password: (비어있음)

## Git 명령어 (Darwin/macOS)
```bash
git status
git add .
git commit -m "message"
git push
```

## macOS 유틸 명령어
```bash
ls -la          # 파일 목록 (숨김 파일 포함)
cd <directory>  # 디렉터리 이동
grep -r "pattern" .  # 재귀 검색
find . -name "*.java"  # 파일 찾기
```
