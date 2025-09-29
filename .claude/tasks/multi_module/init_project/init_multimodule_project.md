# Multi-Module Project 초기화 스크립트

## 사용법
```bash
"init_multimodule_project.md 실행해줘.
프로젝트명: flex-sample,
루트모듈: corehr,
패키지: com.searchkim"
```

## 필수 입력 파라미터
- **프로젝트명**: flex-sample (kebab-case)
- **루트모듈**: corehr (소문자)
- **패키지**: com.searchkim

## 실행 단계

### 1. 프로젝트 루트 파일 생성

#### 1-1. settings.gradle.kts
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

rootProject.name = "${프로젝트명}"

// 향후 모듈들이 여기에 추가됩니다
// include(":${루트모듈}:model")
// include(":${루트모듈}:exception")
// include(":${루트모듈}:service")
// include(":${루트모듈}:infrastructure")
// include(":${루트모듈}:repository-jdbc")
// include(":${루트모듈}:api")
// include(":${루트모듈}:application-api")
// include(":${루트모듈}:schema")

pluginManagement {
    buildscript {
        repositories {
            gradlePluginPortal()
        }
    }

    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

#### 1-2. build.gradle.kts (루트)
```kotlin
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
}

group = "${패키지}"
version = "1.0-SNAPSHOT"

allprojects {
    group = "${패키지}"
    version = "1.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        // Lombok 전역 설정
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
        testCompileOnly("org.projectlombok:lombok:1.18.30")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.mockito:mockito-junit-jupiter")
        testImplementation("org.assertj:assertj-core")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
```

#### 1-3. .gitignore
```gitignore
# Gradle
.gradle
**/build/
!src/**/build/

# Gradle GUI config
gradle-app.setting

# Avoid ignoring Gradle wrapper jar file (.jar files are usually ignored)
!gradle-wrapper.jar

# Cache of project
.gradletasknamecache

# IntelliJ IDEA
.idea/
*.iws
*.iml
*.ipr
out/

# Eclipse
.classpath
.project
.settings/
bin/

# VS Code
.vscode/

# Operating System Files
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db

# Logs
*.log

# Temporary files
*.tmp
*.temp

# Java
*.class
*.jar
*.war
*.ear
hs_err_pid*
```

#### 1-4. gradlew 및 wrapper 확인
```bash
# gradlew가 없으면 생성
gradle wrapper --gradle-version 8.12
```

### 2. 초기 디렉토리 구조 생성

```
${프로젝트명}/
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── ${루트모듈}/
    └── (향후 모듈들이 생성될 디렉토리)
```

### 3. 검증
- `./gradlew clean` 실행하여 Gradle 설정 확인
- 생성된 파일 구조 출력
- "Multi-module 프로젝트가 성공적으로 초기화되었습니다" 메시지

### 4. 다음 단계 안내
```
프로젝트 초기화 완료!

이제 다음 명령어들로 모듈을 생성할 수 있습니다:
- init_model_module.md 실행
- init_exception_module.md 실행
- init_service_module.md 실행
등등...
```

## 예상 실행 결과
```
flex-sample/
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
├── gradlew
└── corehr/
    └── (준비됨)
```

## 주요 특징
- **Java 21** 기반
- **Spring Boot 3.2.0** 지원
- **멀티모듈** 구조 준비
- **Lombok 1.18.30** 전역 설정 (boilerplate 코드 자동 생성)
- **표준 테스트** 도구 포함 (JUnit 5, Mockito, AssertJ)