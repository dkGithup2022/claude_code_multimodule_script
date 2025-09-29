# 프로젝트 구조 출력 스크립트

## 사용법
```bash
"show_project_structure.md 실행해줘"
```

## 개요

현재 멀티모듈 프로젝트의 구조를 상세하게 분석하고 출력합니다.
프로젝트 생성 후 구조 확인이나 개발 중 구조 파악에 유용합니다.

## 실행 내용

### 1. 전체 프로젝트 구조 출력
```bash
echo "📁 전체 프로젝트 구조:"
echo "현재 위치: $(pwd)"
echo ""

# 전체 디렉토리 구조 (build 폴더 제외)
tree . -I "build|.gradle|*.class|target|node_modules" -a --dirsfirst -L 4
```

### 2. 모듈별 상세 구조
```bash
echo ""
echo "📋 모듈별 상세 구조:"

# 루트 모듈 폴더 찾기
ROOT_MODULE=""
if [ -d "modules" ]; then
    ROOT_MODULE="modules"
elif [ -d "core" ]; then
    ROOT_MODULE="core"
else
    ROOT_MODULE=$(find . -maxdepth 1 -type d -name "*module*" | head -1 | sed 's|./||')
fi

if [ -n "$ROOT_MODULE" ]; then
    echo "루트 모듈: $ROOT_MODULE"
    echo ""

    for module in model exception infrastructure service repository-jdbc api schema application-api; do
        if [ -d "$ROOT_MODULE/$module" ]; then
            echo "🔸 $module 모듈:"
            tree "$ROOT_MODULE/$module" -I "build|.gradle|*.class" -a --dirsfirst
            echo ""
        fi
    done
else
    echo "❌ 루트 모듈을 찾을 수 없습니다."
fi
```

### 3. 프로젝트 통계
```bash
echo "📊 프로젝트 통계:"
if [ -n "$ROOT_MODULE" ]; then
    TOTAL_MODULES=$(find "$ROOT_MODULE" -maxdepth 1 -type d | wc -l | xargs expr -1 +)
    JAVA_FILES=$(find "$ROOT_MODULE" -name "*.java" | wc -l)
    KOTLIN_FILES=$(find . -name "*.gradle.kts" | wc -l)
    RESOURCE_FILES=$(find "$ROOT_MODULE" -path "*/resources/*" -type f | wc -l)
    TOTAL_LINES=$(find "$ROOT_MODULE" -name "*.java" -o -name "*.kts" -o -name "*.yml" -o -name "*.properties" | xargs wc -l 2>/dev/null | tail -1 | awk '{print $1}' || echo "0")

    echo "┌─ 총 모듈 수: $TOTAL_MODULES"
    echo "├─ Java 파일 수: $JAVA_FILES"
    echo "├─ Kotlin 빌드 스크립트: $KOTLIN_FILES"
    echo "├─ 리소스 파일 수: $RESOURCE_FILES"
    echo "└─ 총 라인 수: $TOTAL_LINES"
else
    echo "└─ 통계를 계산할 수 없습니다."
fi
```

### 4. 패키지 구조 분석
```bash
echo ""
echo "📦 패키지 구조 분석:"
if [ -n "$ROOT_MODULE" ]; then
    echo "발견된 패키지들:"
    find "$ROOT_MODULE" -name "*.java" -exec grep -l "^package" {} \; | head -15 | while read file; do
        package=$(grep "^package" "$file" | head -1 | cut -d' ' -f2 | tr -d ';')
        module=$(echo "$file" | cut -d'/' -f2)
        echo "├─ [$module] $package"
    done

    echo ""
    echo "📊 패키지별 파일 수:"
    find "$ROOT_MODULE" -name "*.java" -exec dirname {} \; | sort | uniq -c | sort -nr | head -10 | while read count dir; do
        echo "├─ $count 파일: $dir"
    done
fi
```

### 5. 아키텍처 의존성 시각화
```bash
echo ""
echo "🏗️ 헥사고날 아키텍처 구조:"
echo "┌─ 📦 Domain Layer"
echo "│  ├─ model (도메인 모델)"
echo "│  └─ exception (도메인 예외)"
echo "├─ 🔌 Application Layer"
echo "│  ├─ infrastructure (포트 인터페이스)"
echo "│  └─ service (비즈니스 로직)"
echo "├─ 🗄️ Infrastructure Layer"
echo "│  ├─ repository-jdbc (JDBC 어댑터)"
echo "│  └─ schema (데이터베이스 스키마)"
echo "├─ 🌐 Interface Layer"
echo "│  └─ api (REST API)"
echo "└─ 🚀 Main Layer"
echo "   └─ application-api (Spring Boot 애플리케이션)"

echo ""
echo "📈 의존성 방향:"
echo "Interface → Application → Domain ← Infrastructure"
```

### 6. 컴포넌트 스캔 정책 확인
```bash
echo ""
echo "🔍 컴포넌트 스캔 정책:"
if [ -n "$ROOT_MODULE" ]; then
    APP_FILE=$(find "$ROOT_MODULE/application-api" -name "*Application.java" 2>/dev/null | head -1)
    if [ -f "$APP_FILE" ]; then
        echo "┌─ Application 클래스: $APP_FILE"
        echo "├─ 스캔 설정:"
        grep -A 10 "@ComponentScan" "$APP_FILE" | sed 's/^/│  /' || echo "│  기본 스캔 정책 사용"
        echo "├─ JDBC Repository 설정:"
        grep -A 5 "@EnableJdbcRepositories" "$APP_FILE" | sed 's/^/│  /' || echo "│  JDBC Repository 설정 없음"
        echo "└─ 스캔 정책: 중앙 집중식 ✅"
    else
        echo "└─ Application 클래스를 찾을 수 없음 ❌"
    fi
fi
```

### 7. 설정 파일들 확인
```bash
echo ""
echo "📄 주요 설정 파일들:"
echo "┌─ 루트 설정 파일들:"
ls -la *.gradle.kts *.md README* 2>/dev/null | sed 's/^/│  /' || echo "│  설정 파일 없음"

echo "├─ Gradle 래퍼:"
ls -la gradle* 2>/dev/null | sed 's/^/│  /' || echo "│  Gradle 래퍼 없음"

echo "├─ 애플리케이션 설정:"
if [ -n "$ROOT_MODULE" ]; then
    find "$ROOT_MODULE" -name "application.yml" -o -name "application.properties" | sed 's/^/│  /' || echo "│  애플리케이션 설정 없음"
fi

echo "└─ 스크립트:"
SCRIPT_COUNT=$(find .claude -name "*.md" 2>/dev/null | wc -l)
echo "│   총 $SCRIPT_COUNT개 스크립트 파일"
```

### 8. Git 상태 (있는 경우)
```bash
echo ""
if [ -d ".git" ]; then
    echo "🔄 Git 상태:"
    echo "├─ 브랜치: $(git branch --show-current 2>/dev/null || echo 'unknown')"
    echo "├─ 커밋 수: $(git rev-list --count HEAD 2>/dev/null || echo '0')"
    echo "├─ 변경된 파일:"
    git status --porcelain 2>/dev/null | head -10 | sed 's/^/│  /' || echo "│  변경사항 없음"
    echo "└─ 마지막 커밋: $(git log -1 --format='%h %s' 2>/dev/null || echo 'none')"
else
    echo "📝 Git 저장소가 아닙니다."
fi
```

### 9. 실행 가능한 태스크들
```bash
echo ""
echo "🎯 실행 가능한 Gradle 태스크들:"
if [ -f "gradlew" ]; then
    echo "┌─ 빌드 관련:"
    echo "│  ├─ ./gradlew clean"
    echo "│  ├─ ./gradlew build"
    echo "│  └─ ./gradlew test"
    echo "├─ 실행 관련:"
    if [ -n "$ROOT_MODULE" ]; then
        echo "│  └─ ./gradlew :$ROOT_MODULE:application-api:bootRun"
    fi
    echo "└─ 기타:"
    echo "   ├─ ./gradlew dependencies"
    echo "   └─ ./gradlew tasks"
else
    echo "└─ Gradle 래퍼가 없습니다."
fi
```

### 10. 요약
```bash
echo ""
echo "📋 프로젝트 요약:"
if [ -n "$ROOT_MODULE" ]; then
    echo "✅ 멀티모듈 Spring Boot 프로젝트"
    echo "✅ 헥사고날 아키텍처 구조"
    echo "✅ $TOTAL_MODULES개 모듈, $JAVA_FILES개 Java 파일"

    # 프로젝트 크기 분류
    if [ "$JAVA_FILES" -lt 20 ]; then
        echo "📏 프로젝트 크기: 소형 (스타터 프로젝트)"
    elif [ "$JAVA_FILES" -lt 100 ]; then
        echo "📏 프로젝트 크기: 중형 (일반적인 비즈니스 애플리케이션)"
    else
        echo "📏 프로젝트 크기: 대형 (엔터프라이즈 애플리케이션)"
    fi
else
    echo "❓ 프로젝트 구조를 인식할 수 없습니다."
fi

echo ""
echo "💡 구조 재출력: 'show_project_structure.md 실행해줘'"
```