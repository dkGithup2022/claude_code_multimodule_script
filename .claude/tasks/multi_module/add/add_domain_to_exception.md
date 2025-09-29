# Exception Module에 도메인 예외 추가 스크립트 (대화형)

## 사용법
```bash
"add_domain_to_exception.md 실행해줘"
```

## 대화형 입력 프로세스

### 1. 프로젝트 정보 자동 감지 및 확인
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출
- "감지된 설정: 루트모듈=${루트모듈}, 패키지=${패키지} 맞나요? (Y/n)"

### 2. 도메인명 입력 받기
- "추가할 도메인명을 입력해주세요 (예: Foo, User, Product):"
- 입력값 검증:
  - PascalCase 형식 확인
  - 영문자만 포함 확인
  - 첫 글자 대문자 확인
- 유효하지 않으면 다시 입력 요청

### 3. 예외명 입력 받기
- "예외 이름을 입력해주세요 (예: NotFound, AlreadyExists, Invalid, AccessDenied):"
- 입력값 검증:
  - PascalCase 형식 확인
  - 영문자만 포함 확인
  - 첫 글자 대문자 확인
  - Exception 단어 제외 (자동으로 추가됨)
- 유효하지 않으면 다시 입력 요청

### 4. 생성할 예외 클래스 확인
```
도메인명: ${입력받은도메인명}
예외명: ${입력받은예외명}
생성될 클래스: ${도메인명}${예외명}Exception
패키지: ${감지된패키지}.exception
경로: ${루트모듈}/exception/src/main/java/${패키지경로}/exception/${도메인명}${예외명}Exception.java

위 내용으로 진행하시겠습니까? (Y/n)
```

### 5. 디렉토리 구조 생성 (사용자 승인 후)
```
${루트모듈}/exception/src/main/java/${패키지경로}/
└── exception/
    └── ${도메인명}${예외명}Exception.java
```

### 6. 파일 생성

#### 6-1. ${도메인명}${예외명}Exception.java
```java
package ${패키지명}.exception;

public class ${도메인명}${예외명}Exception extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "${도메인명} ${예외명소문자}";
    private static final String MESSAGE_WITH_ID = "${도메인명} ${예외명소문자} with id: %s";

    public ${도메인명}${예외명}Exception() {
        super(DEFAULT_MESSAGE);
    }

    public ${도메인명}${예외명}Exception(String message) {
        super(message);
    }

    public ${도메인명}${예외명}Exception(Long ${도메인명소문자}Id) {
        super(String.format(MESSAGE_WITH_ID, ${도메인명소문자}Id));
    }

    public ${도메인명}${예외명}Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public ${도메인명}${예외명}Exception(Long ${도메인명소문자}Id, Throwable cause) {
        super(String.format(MESSAGE_WITH_ID, ${도메인명소문자}Id), cause);
    }
}
```

### 7. 검증 및 결과 확인
- `./gradlew :${루트모듈}:exception:build` 실행하여 컴파일 확인
- 생성된 파일 구조 출력
- "성공적으로 ${도메인명}${예외명}Exception이 생성되었습니다!"

## 대화형 시나리오 예시

```
사용자: "add_domain_to_exception.md 실행해줘"

봇: "프로젝트 정보를 감지했습니다.
     루트모듈: corehr
     패키지: com.searchkim
     위 설정이 맞나요? (Y/n)"

사용자: "Y"

봇: "추가할 도메인명을 입력해주세요 (예: Foo, User, Product):"

사용자: "Foo"

봇: "예외 이름을 입력해주세요 (예: NotFound, AlreadyExists, Invalid, AccessDenied):"

사용자: "AlreadyExists"

봇: "입력하신 정보를 확인해주세요:
     도메인명: Foo
     예외명: AlreadyExists
     생성될 클래스: FooAlreadyExistsException
     패키지: com.searchkim.exception
     경로: corehr/exception/src/main/java/com/searchkim/exception/FooAlreadyExistsException.java

     위 내용으로 진행하시겠습니까? (Y/n)"

사용자: "Y"

봇: [파일 생성 및 빌드 수행]
    "성공적으로 FooAlreadyExistsException이 생성되었습니다!"
```

## 입력 검증 규칙

### 도메인명 검증
- **필수**: 첫 글자는 대문자
- **필수**: 영문자만 포함 (숫자, 특수문자 불가)
- **권장**: PascalCase 형식 (예: ProductCategory)
- **금지**: 예약어 (Class, Object, Exception 등)

### 에러 처리
```
잘못된 입력 예시와 안내 메시지:
- "foo" → "도메인명은 첫 글자가 대문자여야 합니다. (예: Foo)"
- "Foo123" → "도메인명은 영문자만 사용해주세요."
- "Exception" → "Exception은 예약어입니다. 다른 이름을 사용해주세요."
```

## 예상 실행 결과 (도메인명: Foo, 예외명: AlreadyExists)
```
corehr/exception/src/main/java/com/searchkim/
└── exception/
    ├── ExampleNotFoundException.java (기존)
    └── FooAlreadyExistsException.java (신규)
```

## 생성 가능한 예외 예시
```
도메인: User, 예외: NotFound → UserNotFoundException
도메인: Product, 예외: AlreadyExists → ProductAlreadyExistsException
도메인: Order, 예외: Invalid → OrderInvalidException
도메인: Account, 예외: AccessDenied → AccountAccessDeniedException
도메인: Payment, 예외: ProcessFailed → PaymentProcessFailedException
```

## 주요 특징
- **대화형 입력** 방식 (도메인명 + 예외명 분리 입력)
- **실시간 검증** 및 재입력 요청
- **사용자 확인** 단계 포함
- **유연한 예외 이름** 지원 (NotFound, AlreadyExists, Invalid 등)
- **표준 예외 패턴** 적용
- **다양한 생성자** 제공 (기본, 메시지, ID, Cause)