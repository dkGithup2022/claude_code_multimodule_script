# Complete Multi-Module Project 초기화 스크립트 (Java 21)

## 사용법
```bash
"init_complete_multimodule_project.md 실행해줘"

# 매개변수와 함께 실행
"init_complete_multimodule_project.md 실행해줘 프로젝트명: my-project, 루트모듈: modules, 패키지: com.example.hello"
```

## 개요

이 스크립트는 헥사고날 아키텍처 기반의 완전한 멀티모듈 Spring Boot 프로젝트를 자동으로 생성합니다.
모든 개별 초기화 스크립트를 올바른 순서로 실행하여 종속성 문제 없이 완전한 프로젝트를 구축합니다.

## 실행 단계

### 1. 프로젝트 정보 입력 받기
```
사용자 입력 매개변수:
- 프로젝트명: 기본값 "quick-multimodule"
- 루트모듈: 기본값 "modules"
- 패키지: 기본값 "io.example.hello"

입력 예시:
"프로젝트명: my-awesome-app, 루트모듈: core, 패키지: com.company.app"
```

### 2. 의존성 순서에 따른 스크립트 실행

#### Phase 1: 프로젝트 기본 구조 생성
```bash
# 1. 멀티모듈 프로젝트 초기화
echo "🚀 Phase 1: 프로젝트 기본 구조 생성"
echo "1/9 - 멀티모듈 프로젝트 초기화 중..."
실행: init_multimodule_project.md 프로젝트명=${프로젝트명} 루트모듈=${루트모듈} 패키지=${패키지}
```

#### Phase 2: 기본 모듈들 생성 (의존성 없음)
```bash
echo "📦 Phase 2: 기본 모듈들 생성"

# 2. Model 모듈 (의존성 없음)
echo "2/9 - Model 모듈 생성 중..."
실행: init_model_module.md

# 3. Exception 모듈 (의존성 없음)
echo "3/9 - Exception 모듈 생성 중..."
실행: init_exception_module.md
```

#### Phase 3: 비즈니스 로직 모듈들 생성
```bash
echo "🏗️ Phase 3: 비즈니스 로직 모듈들 생성"

# 4. Infrastructure 모듈 (Model, Exception 의존)
echo "4/9 - Infrastructure 모듈 생성 중..."
실행: init_infrastructure_module.md

# 5. Service 모듈 (Model, Exception, Infrastructure 의존)
echo "5/9 - Service 모듈 생성 중..."
실행: init_service_module.md

# 6. Repository-JDBC 모듈 (Model, Exception, Infrastructure 의존)
echo "6/9 - Repository-JDBC 모듈 생성 중..."
실행: init_repository_jdbc_module.md
```

#### Phase 4: 프레젠테이션 계층 생성
```bash
echo "🌐 Phase 4: 프레젠테이션 계층 생성"

# 7. API 모듈 (Model, Service 의존)
echo "7/9 - API 모듈 생성 중..."
실행: init_api_module.md
```

#### Phase 5: 애플리케이션 및 스키마
```bash
echo "🎯 Phase 5: 애플리케이션 및 스키마 생성"

# 8. Schema 모듈 (Example 도메인)
echo "8/9 - Schema 모듈 생성 중..."
실행: add_schema_module.md 도메인명=example

# 9. Application-API 모듈 (모든 모듈 의존)
echo "9/9 - Application-API 모듈 생성 중..."
실행: init_application_api_module.md
```

### 3. 프로젝트 검증
```bash
echo "✅ Phase 6: 프로젝트 검증"

# Gradle 빌드 테스트
echo "Gradle 빌드 테스트 중..."
실행: ./gradlew clean build

# 애플리케이션 시작 테스트
echo "애플리케이션 시작 테스트 중..."
실행: ./gradlew :${루트모듈}:application-api:bootRun --args='--server.port=8080' (백그라운드)
sleep 10
curl -f http://localhost:8080/actuator/health || echo "애플리케이션 헬스체크 실패"
```

### 4. 프로젝트 구조 상세 출력
```bash
echo "📁 생성된 프로젝트 구조 상세 분석:"
echo ""

# 전체 프로젝트 구조 출력 (build 폴더 제외)
echo "📊 전체 프로젝트 디렉토리 구조:"
tree . -I "build|.gradle|*.class|target" -a --dirsfirst

echo ""
echo "📋 모듈별 세부 구조:"

# 각 모듈별 상세 구조 출력
for module in model exception infrastructure service repository-jdbc api schema application-api; do
    if [ -d "${루트모듈}/$module" ]; then
        echo ""
        echo "🔸 $module 모듈:"
        tree "${루트모듈}/$module" -I "build|.gradle|*.class" -a --dirsfirst
    fi
done

echo ""
echo "📄 주요 설정 파일들:"
echo "┌─ 루트 설정:"
ls -la *.gradle.kts *.md 2>/dev/null | sed 's/^/│  /'
echo "├─ Gradle 래퍼:"
ls -la gradle* 2>/dev/null | sed 's/^/│  /'
echo "└─ 스크립트:"
ls -la .claude/tasks/multi_module/init/*.md 2>/dev/null | wc -l | xargs echo "│   총" | sed 's/$/개 초기화 스크립트/'

echo ""
echo "📊 프로젝트 통계:"
echo "┌─ 총 모듈 수: $(find ${루트모듈} -maxdepth 1 -type d | wc -l | xargs expr -1 +)"
echo "├─ Java 파일 수: $(find ${루트모듈} -name "*.java" | wc -l)"
echo "├─ Kotlin 스크립트 수: $(find . -name "*.gradle.kts" | wc -l)"
echo "├─ 리소스 파일 수: $(find ${루트모듈} -path "*/resources/*" -type f | wc -l)"
echo "└─ 총 라인 수: $(find ${루트모듈} -name "*.java" -o -name "*.kts" -o -name "*.md" | xargs wc -l 2>/dev/null | tail -1 | awk '{print $1}')"

echo ""
echo "🏗️ 아키텍처 의존성 관계:"
echo "┌─ 📦 model (기본 도메인)"
echo "├─ 🚨 exception (도메인 예외)"
echo "├─ 🔌 infrastructure ← model, exception"
echo "├─ ⚙️  service ← model, exception, infrastructure"
echo "├─ 🗄️  repository-jdbc ← model, exception, infrastructure"
echo "├─ 🌐 api ← model, service"
echo "├─ 🗃️  schema (독립적)"
echo "└─ 🚀 application-api ← 모든 모듈"

echo ""
echo "📦 패키지 구조 분석:"
echo "루트 패키지: ${패키지}"
find ${루트모듈} -name "*.java" -exec grep -l "^package" {} \; | head -10 | while read file; do
    package=$(grep "^package" "$file" | cut -d' ' -f2 | tr -d ';')
    echo "├─ $package"
done

echo ""
echo "🔍 컴포넌트 스캔 정책:"
if [ -f "${루트모듈}/application-api/src/main/java"/*/*/application/*Application.java ]; then
    app_file=$(find ${루트모듈}/application-api -name "*Application.java")
    echo "┌─ 스캔 설정 위치: $app_file"
    grep -A 10 "@ComponentScan" "$app_file" | sed 's/^/│  /'
    echo "└─ 중앙 집중식 컴포넌트 스캔 적용됨 ✅"
else
    echo "└─ Application 클래스를 찾을 수 없음 ❌"
fi
```

### 5. 완료 메시지 및 다음 단계 안내
```bash
echo ""
echo "🎉 멀티모듈 프로젝트 생성 완료!"
echo ""
echo "🚀 다음 단계:"
echo "1. 애플리케이션 실행: ./gradlew :${루트모듈}:application-api:bootRun"
echo "2. API 문서 확인: http://localhost:8080/swagger-ui.html"
echo "3. H2 콘솔 접근: http://localhost:8080/h2-console"
echo "4. 샘플 API 테스트: curl http://localhost:8080/api/examples"
echo ""
echo "📚 추가 도메인 생성:"
echo "- 새 도메인 추가: add_domain_to_model.md 도메인명=새도메인명"
echo "- 완전한 도메인 생성: add_schema_module.md 도메인명=새도메인명"
echo ""
echo "🔧 프로젝트 설정:"
echo "- 패키지 구조: ${패키지}.{model|service|api|jdbc|application}"
echo "- 컴포넌트 스캔: Application 모듈에서 중앙 관리"
echo "- 아키텍처: 헥사고날 아키텍처 (Ports & Adapters)"
echo ""
echo "📄 구조 재출력: tree . -I 'build|.gradle' -a --dirsfirst"
```

## 생성되는 최종 프로젝트 구조

```
${프로젝트명}/
├── build.gradle.kts
├── settings.gradle.kts
└── ${루트모듈}/
    ├── model/                     # 도메인 모델
    ├── exception/                 # 도메인 예외
    ├── infrastructure/            # 인프라 포트 인터페이스
    ├── service/                   # 비즈니스 로직 (CQRS)
    ├── repository-jdbc/           # JDBC 구현체
    ├── api/                       # REST API
    ├── schema/                    # 데이터베이스 스키마
    └── application-api/           # Spring Boot 애플리케이션
```

## 포함되는 기능

### 📦 **모듈별 기능**
- **Model**: Example 도메인 (AuditProps, Example, ExampleIdentity, ExampleModel)
- **Exception**: ExampleNotFoundException
- **Infrastructure**: ExampleRepository 인터페이스
- **Service**: CQRS (ExampleReader/Writer + DefaultImplementations)
- **Repository-JDBC**: Spring Data JDBC 구현체
- **API**: REST API (ExampleApiController, ExampleResponse, GlobalExceptionHandler)
- **Schema**: H2 데이터베이스 스키마 + 테스트 데이터
- **Application**: 통합 Spring Boot 애플리케이션

### 🏗️ **아키텍처 특징**
- **헥사고날 아키텍처**: Ports & Adapters 패턴
- **CQRS**: Command Query Responsibility Segregation
- **의존성 역전**: Infrastructure → Application 방향 의존성
- **중앙 집중 스캔**: Application 모듈에서 컴포넌트 스캔 관리
- **Auto Configuration**: Spring Boot 자동 설정 활용

### 🔧 **기술 스택**
- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Data JDBC**
- **H2 Database**
- **Swagger/OpenAPI 3**
- **Lombok**
- **Gradle Kotlin DSL**

## 주의사항

1. **실행 순서**: 스크립트는 반드시 정해진 순서대로 실행되어야 합니다.
2. **의존성 확인**: 각 단계 완료 후 다음 단계로 진행합니다.
3. **에러 처리**: 중간에 실패하면 해당 단계부터 재실행 가능합니다.
4. **포트 충돌**: 8080 포트가 사용 중이면 다른 포트로 변경하여 테스트합니다.

## 예상 실행 시간
- **전체 프로젝트 생성**: 약 2-3분
- **빌드 및 테스트**: 약 1-2분
- **총 소요 시간**: 약 3-5분