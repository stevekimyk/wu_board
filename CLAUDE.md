# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# 빌드
./gradlew build

# 실행 (포트 8087)
./gradlew bootRun

# 테스트 전체 실행
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests "com.wu.board.WuBoardApplicationTests"

# 클린 빌드
./gradlew clean build
```

Windows 환경에서는 `./gradlew` 대신 `gradlew.bat` 사용.

## 환경 설정

`application.properties`에 Oracle DB 접속 정보가 직접 기입되어 있다. `.gitignore`에 포함되어 있는지 확인 필요. `application.properties.example`을 참고해 로컬 설정을 잡는다.

- 서버 포트: **8087**
- DB: Oracle (`jdbc:oracle:thin:@203.246.100.150:1521:WUOPRDB`)
- MyBatis underscore-to-camelCase 자동 변환 활성화

## 아키텍처

Spring Boot 2.7.7 + MyBatis + Thymeleaf + Oracle 구성의 사내 커뮤니티 게시판.

### 요청 흐름

```
브라우저 → Controller → Mapper Interface → XML SQL → Oracle DB
                ↓
           Thymeleaf 템플릿 렌더링
```

### 레이어 구조

| 레이어 | 위치 | 역할 |
|--------|------|------|
| Controller | `controller/` | 라우팅, 모델 구성 |
| DTO | `dto/` | `Post`, `PsnlPhoto` |
| Mapper | `mapper/` + `resources/mapper/*.xml` | MyBatis SQL |
| View | `resources/templates/` | Thymeleaf HTML |

컨트롤러는 두 개다:
- `BasicController`: `/` 루트만 담당, `index.html` 반환.
- `BoardController`: `/board/**` 전체 담당.

### 핵심 도메인

- **`PsnlPhoto`**: 사원 사진 관리. Oracle `WU_ADM.TA_PSNL_PHOTO_TEST` 테이블에서 BLOB 사진을 조회하고, `EMP_MSTR` 테이블 JOIN으로 사원명·부서 정보를 가져온다. Oracle 패키지 함수 두 개를 사용한다: `wu_cmm.codenm.emp(empno)` → 사원명, `wu_cmm.codenm.dept(dept_no)` → 부서명(한글). SQL의 `sclpst_sttus_cd IN ('3050100','3050200')` 조건은 재직자만 필터링하는 고정 코드값이다.
- **`Post`**: 게시글 DTO. `free` 게시판은 실제로 `Post`가 아닌 `PsnlPhoto`(사원 디렉토리) 데이터를 보여준다. `Post`는 notice/qna/gallery 더미 데이터와 detail 페이지에서만 사용 중이다.
- **`PsnlPhotoMapper.selectAll()`**: Mapper 인터페이스와 XML에 정의되어 있으나 현재 어느 컨트롤러에서도 호출하지 않는 미사용 메서드다.

### 주요 URL

| URL | 설명 |
|-----|------|
| `GET /` | 메인 페이지 |
| `GET /board/list?type={type}` | 게시판 목록 (type: notice/free/qna/gallery) |
| `GET /board/list?id={empno}` | 사원 상세 정보 |
| `GET /board/detail/{id}` | 게시글 상세 |
| `GET /board/write` | 게시글 작성 폼 |
| `POST /board/write` | 게시글 저장 (TODO: DB 미연동) |
| `GET /board/photo/{empno}` | 사원 사진 BLOB → JPEG 응답 |

`/board/list?id=` 와 `/board/list?type=` 은 같은 경로(`/board/list`)를 공유한다. Spring MVC의 `@GetMapping(value = "/list", params = "id")` 로 `id` 파라미터 유무에 따라 핸들러를 분기한다.

### 페이징

`free` 게시판은 `PAGE_SIZE = 10` 기준으로 Oracle `ROW_NUMBER()` 방식 페이징. `selectPage`에 `startRow`/`endRow` Map 파라미터로 전달.

### 검색

`searchType` 파라미터로 `empno`(사원번호) 또는 `empnoNm`(이름) 검색 지원. MyBatis XML의 `<if>` 동적 쿼리로 처리.

### Thymeleaf 템플릿

`templates/fragments/layout.html`에 navbar, footer, CSS, JS를 fragment로 정의해두고 각 페이지에서 `th:replace`로 재사용한다. Bootstrap 5.3 + Bootstrap Icons CDN 사용.

## 코딩 컨벤션

### Java

- **DTO 표준**: `@Data + @NoArgsConstructor + @AllArgsConstructor` 세트를 항상 함께 사용한다.
- **DI**: `@Autowired` 필드 주입 방식 사용 (생성자 주입 미사용).
- **Controller**: `@Controller` + `@RequestMapping` 사용. 바이너리(BLOB) 응답만 `@ResponseBody + ResponseEntity<byte[]>` 조합 사용.
- **옵셔널 파라미터**: `@RequestParam(required = false, defaultValue = "...")` 패턴으로 처리.
- **인코딩**: 소스 파일 UTF-8, 한글 주석 허용.
- **Java 버전**: 1.8 (람다, 스트림 사용 가능하나 record/sealed class 등 9+ 문법 사용 불가).

### MyBatis

- SQL은 **XML 파일**에서만 작성한다 (`resources/mapper/*.xml`). 어노테이션 SQL(`@Select` 등) 사용 금지.
- 다중 파라미터는 `Map<String, Object>`로 전달한다.
- DB 컬럼명은 snake_case, DTO 필드는 camelCase — MyBatis `map-underscore-to-camel-case: true` 설정으로 자동 변환된다. `resultMap` 별도 정의 불필요.
- `resultType`은 패키지 전체 경로 대신 클래스명 단축명(예: `PsnlPhoto`)을 사용한다 (`mybatis.type-aliases-package` 설정 적용).

### Thymeleaf

- 모든 페이지는 `fragments/layout.html`의 fragment를 `th:replace`로 삽입한다.
- 페이지별 추가 CSS가 필요할 때만 `<style>` 블록을 해당 파일에 직접 작성한다.
- UI 아이콘은 **Bootstrap Icons** (`bi bi-*`) 클래스로 통일한다.

---

## 아키텍처 원칙

### MVC 2 레이어 구조 (필수)

```
브라우저
  ↓ HTTP 요청
Controller   — 요청/응답 처리, 파라미터 바인딩, 뷰 반환만 담당
  ↓
Service      — 비즈니스 로직, 트랜잭션 단위 (@Service)
  ↓
Mapper       — SQL 실행만 담당 (MyBatis @Mapper)
  ↓
Oracle DB
```

**레이어 책임 원칙**
- **Controller**: 요청 파라미터 수집 → Service 호출 → Model에 결과 적재 → 뷰 이름 반환. 비즈니스 로직 작성 금지.
- **Service**: 비즈니스 규칙, 페이징 계산, 조건 분기 등 핵심 로직 담당. `@Transactional` 경계.
- **Mapper**: SELECT / INSERT / UPDATE / DELETE SQL 실행만. 로직 없음.

> **현재 코드 상태**: `BoardController`가 `PsnlPhotoMapper`를 직접 호출하고 있으며, 페이징 계산·분기 로직이 Controller에 혼재되어 있다. 기능 추가·수정 시 반드시 Service 레이어를 만들어 이 원칙을 지킨다. 기존 Controller의 직접 호출 코드도 점진적으로 Service로 이동시킨다.

### 기타 원칙

- **게시판 타입 분기**: `type` 파라미터(`notice` / `free` / `qna` / `gallery`)로 동작을 분기한다. `free`만 DB 연동, 나머지는 더미 데이터. 신규 게시판 추가 시 `label()` switch, Service/Mapper 쿼리, 뷰 분기를 함께 추가한다.
- **Oracle 전용 SQL**: 페이징은 `ROW_NUMBER() OVER (ORDER BY ...)` 방식만 사용한다. LIMIT/OFFSET 문법 사용 불가.
- **BLOB 이미지 서빙**: DB에서 `byte[]`로 읽어 `ResponseEntity<byte[]>` + `MediaType.IMAGE_JPEG`로 직접 응답한다. 파일 시스템 저장 방식 미사용.

---

## 자주 쓰는 패턴

### 페이징 쿼리 (Oracle ROW_NUMBER)

Controller에서 Map에 `startRow` / `endRow` 계산 후 Mapper 전달:
```java
param.put("startRow", (page - 1) * PAGE_SIZE + 1);
param.put("endRow",   page * PAGE_SIZE);
```
XML에서 서브쿼리로 감싸 `rn BETWEEN #{startRow} AND #{endRow}` 조건 적용.

### 동적 검색 쿼리 (MyBatis `<if>`)

```xml
<if test="searchType == 'empno' and keyword != null and keyword != ''">
    AND a.empno LIKE '%' || #{keyword} || '%'
</if>
```
`searchType` + `keyword`를 Map으로 전달하고 XML에서 분기.

### Thymeleaf 페이지 골격

```html
<th:block th:replace="~{fragments/layout :: css}"></th:block>
...
<nav th:replace="~{fragments/layout :: navbar(${activeMenu})}"></nav>
<div class="page-header">...</div>
<div class="container my-4">...</div>
<footer th:replace="~{fragments/layout :: footer}"></footer>
<th:block th:replace="~{fragments/layout :: js}"></th:block>
```
`activeMenu`는 navbar의 현재 탭 강조에 사용된다 (`home` / `notice` / `free` / `qna` / `gallery`).

### POST 후 리다이렉트

```java
return "redirect:/board/list?type=" + post.getBoardType();
```

### 사원 사진 이미지 태그 (Thymeleaf)

```html
<img th:src="@{/board/photo/{empno}(empno=${p.empno})}" ...>
```

---

## 미완성 기능 (TODO)

- `POST /board/write`: MyBatis insert 미연동, 현재 목록으로 리다이렉트만 함
- notice/qna/gallery 게시판: 더미 데이터, DB 연동 필요
- 로그인/회원가입 (`/login`, `/signup`): 미구현
