# Service Module에 도메인 서비스 추가 스크립트 (대화형 + 다중 도메인 지원)

## 사용법
```bash
"add_domain_to_service.md 실행해줘"
```

## 대화형 입력 프로세스

### 1. 프로젝트 정보 자동 감지 및 확인
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출
- "감지된 설정: 루트모듈=${루트모듈}, 패키지=${패키지} 맞나요? (Y/n)"

### 2. 도메인 수집 및 의존성 분석
- "처리할 도메인들을 입력해주세요 (쉼표로 구분): Bar, User, Product"
- 입력값 검증: PascalCase, 영문자만, 첫 글자 대문자

#### 각 도메인별 의존성 자동 감지:
- **Model 모듈** 클래스 탐색
  - `${도메인명}.java` 및 `${도메인명}Identity.java` 확인
- **Infrastructure 모듈** Repository 인터페이스 분석
  - `${도메인명}Repository.java` 존재 확인 및 메서드 추출
- **Exception 모듈** 예외 클래스 확인
  - `${도메인명}NotFoundException.java` 등 관련 예외 탐색

#### 대화형 선택:
```
도메인 'Bar' 분석 결과:
✓ Model: Bar.java, BarIdentity.java 발견
✓ Infrastructure: BarRepository.java 발견 (6개 메서드)
  - findById, findAll, findByCode, findByOwnerId, save, deleteById
✓ Exception: BarNotFoundException.java 발견

이 도메인으로 서비스를 생성하시겠습니까? (Y/n/s)
Y: 생성, n: 스킵, s: 설정 변경
```

### 3. 생성 확인 및 승인
```
처리할 도메인: Bar, User, Product (3개)

생성될 서비스 목록:
✓ Bar: BarReader + BarWriter + 구현체들 (Infrastructure 기반 6개 메서드)
  - DefaultBarReader (조회 3개 메서드)
  - DefaultBarWriter (변경 3개 메서드)
✓ User: UserReader + UserWriter + 구현체들 (Infrastructure 기반 5개 메서드)
  - DefaultUserReader (조회 2개 메서드)
  - DefaultUserWriter (변경 3개 메서드)
✓ Product: ProductReader + ProductWriter + 구현체들 (Infrastructure 기반 8개 메서드)
  - DefaultProductReader (조회 5개 메서드)
  - DefaultProductWriter (변경 3개 메서드)

패키지: ${감지된패키지}.[bar|user|product]
경로: ${루트모듈}/service/src/main/java/${패키지경로}/

위 내용으로 진행하시겠습니까? (Y/n)
```

#### 4-1. ${도메인명}Reader.java (조회 인터페이스)
```java
package ${감지된패키지명}.${도메인명소문자};

import ${감지된패키지명}.${도메인명소문자}.${도메인명};
import ${감지된패키지명}.${도메인명소문자}.${도메인명}Identity;

import java.util.List;

/**
 * ${도메인명} 도메인 조회 서비스 인터페이스
 *
 * CQRS 패턴의 Query 책임을 담당하며,
 * Infrastructure Repository 기반으로 조회 로직을 제공합니다.
 */
public interface ${도메인명}Reader {

    // Infrastructure Repository 조회 메서드들을 기반으로 동적 생성
    ${Infrastructure_Repository_기반_조회_메서드들}
}
```

#### 4-2. ${도메인명}Writer.java (변경 인터페이스)
```java
package ${감지된패키지명}.${도메인명소문자};

import ${감지된패키지명}.${도메인명소문자}.${도메인명};
import ${감지된패키지명}.${도메인명소문자}.${도메인명}Identity;

/**
 * ${도메인명} 도메인 변경 서비스 인터페이스
 *
 * CQRS 패턴의 Command 책임을 담당하며,
 * Infrastructure Repository 기반으로 변경 로직을 제공합니다.
 */
public interface ${도메인명}Writer {

    // Infrastructure Repository 변경 메서드들을 기반으로 동적 생성
    ${Infrastructure_Repository_기반_변경_메서드들}
}
```

#### 4-3. Default${도메인명}Reader.java (조회 구현체)
```java
package ${감지된패키지명}.${도메인명소문자}.impl;

import ${감지된패키지명}.${도메인명소문자}.${도메인명};
import ${감지된패키지명}.${도메인명소문자}.${도메인명}Identity;
import ${감지된패키지명}.${도메인명소문자}.${도메인명}Reader;
import ${감지된패키지명}.${도메인명소문자}.repository.${도메인명}Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ${도메인명} 도메인 조회 서비스 구현체
 *
 * CQRS 패턴의 Query 책임을 구현하며,
 * Infrastructure Repository를 활용한 조회 로직을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Default${도메인명}Reader implements ${도메인명}Reader {

    private final ${도메인명}Repository ${도메인명소문자}Repository;

    // Infrastructure Repository 조회 메서드들을 로깅과 함께 구현
    ${Infrastructure_Repository_기반_조회_구현_메서드들}
}
```

#### 4-4. Default${도메인명}Writer.java (변경 구현체)
```java
package ${감지된패키지명}.${도메인명소문자}.impl;

import ${감지된패키지명}.${도메인명소문자}.${도메인명};
import ${감지된패키지명}.${도메인명소문자}.${도메인명}Identity;
import ${감지된패키지명}.${도메인명소문자}.${도메인명}Writer;
import ${감지된패키지명}.${도메인명소문자}.repository.${도메인명}Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ${도메인명} 도메인 변경 서비스 구현체
 *
 * CQRS 패턴의 Command 책임을 구현하며,
 * Infrastructure Repository를 활용한 변경 로직을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Default${도메인명}Writer implements ${도메인명}Writer {

    private final ${도메인명}Repository ${도메인명소문자}Repository;

    // Infrastructure Repository 변경 메서드들을 로깅과 함께 구현
    ${Infrastructure_Repository_기반_변경_구현_메서드들}
}
```

### 5. 검증 및 결과 확인
- `./gradlew :${감지된루트모듈}:service:build` 실행하여 컴파일 확인
- 생성된 파일 구조 출력
- "성공적으로 ${처리된도메인개수}개 도메인의 서비스가 생성되었습니다!"

## 대화형 시나리오 예시

### 다중 도메인 처리 시나리오
```
사용자: "add_domain_to_service.md 실행해줘"

봇: "감지된 설정: 루트모듈=searchkim, 패키지=com.searchkim.multimodule 맞나요? (Y/n)"

사용자: "Y"

봇: "처리할 도메인들을 입력해주세요 (쉼표로 구분): "

사용자: "Bar, User"

봇: "도메인 'Bar' 분석 결과:
     ✓ Model: Bar.java, BarIdentity.java 발견
     ✓ Infrastructure: BarRepository.java 발견 (6개 메서드)
     ✓ Exception: BarNotFoundException.java 발견

     이 도메인으로 서비스를 생성하시겠습니까? (Y/n/s)"

사용자: "Y"

봇: "도메인 'User' 분석 결과: [유사하게 진행...]"

봇: "성공적으로 2개 도메인의 서비스가 생성되었습니다!
     ✓ Bar: BarReader + BarWriter + 구현체들 (6개 메서드)
       - DefaultBarReader (조회 3개), DefaultBarWriter (변경 3개)
     ✓ User: UserReader + UserWriter + 구현체들 (5개 메서드)
       - DefaultUserReader (조회 2개), DefaultUserWriter (변경 3개)"
```

## 주요 특징
- **CQRS 패턴 적용** - Reader/Writer 책임 분리
- **대화형 다중 도메인** 처리
- **Infrastructure Repository 기반** 동적 메서드 분리 생성
- **의존성 자동 감지** 및 검증
- **인터페이스-구현체 분리** 패턴
- **@Service + @RequiredArgsConstructor + @Slf4j** 적용
- **로깅 포함** - 모든 작업 로그 기록
- **upsert 패턴** - save를 upsert로 매핑