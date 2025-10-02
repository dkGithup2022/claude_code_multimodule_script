# Schema Module에 도메인 테이블 추가 스크립트 (대화형)

## 사용법
```bash
"add_schema_module.md 실행해줘 도메인명: <도메인명>"
```

## 대화형 입력 프로세스

### 1. 프로젝트 정보 자동 감지 및 확인
- settings.gradle.kts에서 루트 모듈명 추출
- build.gradle.kts에서 패키지명 추출
- "감지된 설정: 루트모듈=${루트모듈}, 패키지=${패키지} 맞나요? (Y/n)"

### 2. 도메인명 입력 받기
- "추가할 도메인명을 입력해주세요 (예: User, Product, Order):"
- 입력값 검증:
  - PascalCase 형식 확인
  - 영문자만 포함 확인
  - 첫 글자 대문자 확인
- 유효하지 않으면 다시 입력 요청

### 3. JDBC 엔티티 탐색 및 컬럼 자동 추출
- `find` 명령어로 repository-jdbc 모듈에서 Entity 클래스 탐색
- 검색 패턴: `find ${루트모듈}/repository-jdbc -name "*Entity*.java" -type f`

#### 3-1. Entity 클래스 발견된 경우 - 자동 매칭
```
Entity 클래스를 탐색 중...

발견된 Entity 파일들:
1. ExampleEntity.java (searchkim/repository-jdbc/.../example/repository/)
2. FooEntity.java (searchkim/repository-jdbc/.../foo/repository/)
3. UserEntity.java (searchkim/repository-jdbc/.../user/repository/)

도메인 '${도메인명}'과 일치하는 Entity: ${도메인명}Entity.java ✓
경로: ${실제경로}

추출된 필드:
- ${필드1}: ${Java타입1} → ${SQL타입1}
- ${필드2}: ${Java타입2} → ${SQL타입2}
...

위 구조를 사용하시겠습니까? (Y/n/c)
Y: 추출된 구조 사용
n: 사용자 정의 구조 입력
c: 기본 구조 사용
```

#### 3-2. Entity 클래스 수동 선택
```
도메인 '${도메인명}'과 정확히 일치하는 Entity를 찾을 수 없습니다.

발견된 Entity 파일들:
1. ExampleEntity.java
2. FooEntity.java
3. UserEntity.java
4. 사용하지 않음 (기본 구조 또는 사용자 정의)

사용할 Entity를 선택하세요 (1-4):
```

#### 3-3. Entity 클래스 미발견된 경우
```
repository-jdbc 모듈에서 Entity 클래스를 찾을 수 없습니다.

테이블 구조를 어떻게 정의하시겠습니까?
1) 기본 구조 사용 (ID, NAME, DESCRIPTION, CREATED_AT, UPDATED_AT)
2) 사용자 정의
선택 (1-2):
```

### 4. Java 타입 → SQL 타입 매핑 규칙
```
칼럼에 어노테이션이 있을 경우, 어노테이션의 규칙을 따름. 없다면 아래의 규칙으로 진행.

Long/long → bigint
Integer/int → integer
String → varchar(255)
String (name 필드) → varchar(255) not null
String (description 필드) → varchar(500)
String (email 필드) → varchar(255) unique
Boolean/boolean → boolean
Instant → timestamp (UTC 저장)
LocalDate → date
BigDecimal → decimal(19,2)
Double/double → double precision
Float/float → real

※ 모든 타입명과 키워드는 소문자 사용
```

### 5. 사용자 정의 컬럼 입력 (n 선택 시)
```
컬럼을 하나씩 입력해주세요 (완료시 빈 입력):
컬럼명: 타입 [제약조건] (예: email: VARCHAR(255) NOT NULL UNIQUE)

예시:
email: VARCHAR(255) NOT NULL UNIQUE
age: INTEGER
phone: VARCHAR(20)
(빈 입력으로 완료)
```

### 6. 초기 데이터 추가 여부 확인
- "초기 테스트 데이터를 추가하시겠습니까? (Y/n)"
- Y 선택 시: 기본 3개 레코드 생성
- n 선택 시: 데이터 삽입 스킵

### 7. 생성할 스키마 정보 확인 및 승인
```
도메인명: ${입력받은도메인명}
테이블명: ${도메인명소문자복수형} (예: users, products)

생성될 DDL:
CREATE TABLE IF NOT EXISTS ${테이블명} (
    ${도메인명소문자}_id bigint auto_increment primary key,
    ${사용자정의컬럼들}
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

${초기데이터가_있으면:}
생성될 DML:
INSERT INTO ${테이블명} (컬럼들...) VALUES (값들...);

경로: ${루트모듈}/schema/src/main/resources/

위 내용으로 진행하시겠습니까? (Y/n)

※ 테이블명/컬럼명 규칙: 소문자 + 스네이크케이스 (Spring Data JDBC 네이밍 규칙)
```

### 8. 스키마 파일 업데이트 (사용자 승인 후)

#### 8-1. schema.sql 파일에 DDL 추가
```sql
-- ${도메인명} 테이블 생성
CREATE TABLE IF NOT EXISTS ${테이블명} (
    ${도메인명소문자}_id bigint auto_increment primary key,
    ${정의된컬럼들}
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

-- ※ 외래키 정책: 외래키 제약 조건(FOREIGN KEY) 사용 안함
--   다른 테이블 참조 시 제약 조건 없이 ID 컬럼만 추가 (예: user_id bigint)
--   참조 무결성은 애플리케이션 레벨에서 관리
```

#### 8-2. data.sql 파일에 DML 추가 (선택사항)
```sql
-- ${도메인명} 초기 테스트 데이터
INSERT INTO ${테이블명} (${컬럼목록}) VALUES
${생성된데이터행들};
```

### 9. 검증 및 결과 확인
- 기존 schema.sql 내용 보존 확인
- 새로운 DDL/DML 추가 확인
- `./gradlew :${루트모듈}:schema:build` 실행하여 컴파일 확인
- 업데이트된 파일 내용 미리보기
- "성공적으로 ${도메인명} 테이블이 스키마에 추가되었습니다!"

## 대화형 시나리오 예시

### 시나리오 1: Entity 클래스 발견 및 자동 추출 사용
```
사용자: "add_schema_module.md 실행해줘 도메인명: Foo"

봇: "프로젝트 정보를 감지했습니다.
     루트모듈: searchkim
     패키지: com.searchkim.multimodule
     위 설정이 맞나요? (Y/n)"

사용자: "Y"

봇: "추가할 도메인명을 입력해주세요 (예: User, Product, Order):"

사용자: "Foo"

봇: "Entity 클래스를 탐색 중...

     발견된 Entity 파일들:
     1. ExampleEntity.java (searchkim/repository-jdbc/.../example/repository/)
     2. FooEntity.java (searchkim/repository-jdbc/.../foo/repository/)

     도메인 'Foo'와 일치하는 Entity: FooEntity.java ✓
     경로: searchkim/repository-jdbc/src/main/java/com/searchkim/multimodule/foo/repository/FooEntity.java

     추출된 필드:
     - fooId: Long → BIGINT (PK)
     - name: String → VARCHAR(255) NOT NULL
     - description: String → VARCHAR(500)
     - createdAt: Instant → TIMESTAMP
     - updatedAt: Instant → TIMESTAMP

     위 구조를 사용하시겠습니까? (Y/n/c)
     Y: 추출된 구조 사용
     n: 사용자 정의 구조 입력
     c: 기본 구조 사용"

사용자: "Y"

봇: "초기 테스트 데이터를 추가하시겠습니까? (Y/n)"

사용자: "Y"

봇: "입력하신 정보를 확인해주세요:
     도메인명: Foo
     테이블명: FOOS

     생성될 DDL (Entity 구조 기반):
     CREATE TABLE IF NOT EXISTS foos (
         foo_id bigint auto_increment primary key,
         name varchar(255) not null,
         description varchar(500),
         created_at timestamp default current_timestamp,
         updated_at timestamp default current_timestamp on update current_timestamp
     );

     생성될 DML:
     INSERT INTO foos (name, description, created_at, updated_at) VALUES
     ('Foo One', 'First Foo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Foo Two', 'Second Foo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Foo Three', 'Third Foo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

     위 내용으로 진행하시겠습니까? (Y/n)"

사용자: "Y"

봇: [Entity 필드 분석 완료]
    [스키마 파일 업데이트 및 빌드 수행]
    "성공적으로 Foo 테이블이 스키마에 추가되었습니다!
     ✓ FooEntity.java에서 필드 구조 자동 추출
     ✓ schema.sql에 foos 테이블 DDL 추가 (소문자)
     ✓ data.sql에 초기 데이터 3개 레코드 추가"
```

### 시나리오 2: Entity 클래스 수동 선택
```
사용자: "add_schema_module.md 실행해줘 도메인명: Product"

사용자: "Product"

봇: "Entity 클래스를 탐색 중...

     발견된 Entity 파일들:
     1. ExampleEntity.java
     2. FooEntity.java
     3. UserEntity.java

     도메인 'Product'와 정확히 일치하는 Entity를 찾을 수 없습니다.

     발견된 Entity 파일들:
     1. ExampleEntity.java
     2. FooEntity.java
     3. UserEntity.java
     4. 사용하지 않음 (기본 구조 또는 사용자 정의)

     사용할 Entity를 선택하세요 (1-4):"

사용자: "2"

봇: "FooEntity.java를 선택했습니다.

     추출된 필드:
     - fooId: Long → BIGINT → PRODUCT_ID (PK)
     - name: String → VARCHAR(255) NOT NULL
     - description: String → VARCHAR(500)
     - createdAt: Instant → TIMESTAMP
     - updatedAt: Instant → TIMESTAMP

     ※ PK 컬럼명이 도메인에 맞게 PRODUCT_ID로 자동 변경됩니다.

     위 구조를 사용하시겠습니까? (Y/n)"

사용자: "Y"

봇: "성공적으로 Product 테이블이 스키마에 추가되었습니다!
     ✓ FooEntity.java 구조를 Product 테이블로 변환
     ✓ schema.sql에 PRODUCTS 테이블 DDL 추가"
```

## 예상 실행 결과 예시

### 업데이트된 schema.sql
```sql
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

-- Examples 테이블 생성
CREATE TABLE IF NOT EXISTS examples (
    example_id bigint auto_increment primary key,
    name varchar(255) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

-- User 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    user_id bigint auto_increment primary key,
    name varchar(255) not null,
    description varchar(500),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);
```

### 업데이트된 data.sql
```sql
/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

-- 초기 테스트 데이터
INSERT INTO examples (name, created_at, updated_at) VALUES
('First Example', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Second Example', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Third Example', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User 초기 테스트 데이터
INSERT INTO users (name, description, created_at, updated_at) VALUES
('User One', 'First User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('User Two', 'Second User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('User Three', 'Third User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

## 주요 특징
- **JDBC Entity 자동 탐색** - repository-jdbc 모듈에서 Entity 클래스 자동 발견
- **필드 구조 자동 추출** - Java 타입을 SQL 타입으로 자동 매핑
- **대화형 입력** 방식으로 사용자 친화적
- **기존 스키마 보존** - 기존 테이블 DDL/DML 유지
- **유연한 컬럼 정의** - Entity 추출/기본 구조/사용자 정의 선택
- **H2 호환** DDL 생성
- **대문자 테이블명** 규약 준수
- **초기 데이터 선택적** 추가
- **자동 타임스탬프** 컬럼 (CREATED_AT, UPDATED_AT)
- **Auto Increment PK** 자동 설정
- **빌드 검증** 포함

## 테이블 명명 규칙
- 도메인명 → 테이블명 변환 예시:
  - User → users
  - Product → products
  - OrderItem → order_items
- PK 컬럼: `${도메인명소문자}_id`
- 모든 테이블명/컬럼명은 소문자 + 스네이크케이스 사용 (Spring Data JDBC 네이밍 규칙)

## 지원하는 H2 데이터 타입
- `VARCHAR(size)` - 문자열
- `INTEGER` - 정수
- `BIGINT` - 큰 정수
- `DECIMAL(precision,scale)` - 십진수
- `BOOLEAN` - 불린값
- `TIMESTAMP` - 타임스탬프
- `DATE` - 날짜
- `TEXT` - 긴 텍스트