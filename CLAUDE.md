# 커리포 (Carrepo)

공고·이력서·지원 문구·전형 결과를 쌓아 **다음 지원에 재사용하는** 개인 커리어 저장소.
공고를 저장하는 서비스가 아니라, 과거 지원 데이터를 다음 지원에 재사용하는 서비스다.

설계 배경과 데이터 모델은 [docs/프로젝트-구조-설계.md](docs/프로젝트-구조-설계.md),
실행 방법은 [README.md](README.md)에 있다. 이 문서에는 **작업 규칙만** 둔다.

## 기능 소유권

기능 단위로 패키지를 나눈다. 레이어 단위가 아니다.

```
backend/com/carrepo/{user, document, posting, jobapplication, episode, common}
web/src/features/{user, document, posting, jobapplication, episode}
```

- 한 기능 안에 Controller · Service · Repository · 엔티티 · DTO 를 **함께** 둔다.
- **다른 기능의 API 를 자기 Controller 에 얹지 않는다.** 이력서 API 는 `document` 가 갖는다.
  주소가 `/api/users/{id}/resume` 라고 해서 `user` 의 기능이 되는 것은 아니다.
- 프런트도 같은 경계를 쓴다. `routes/` 는 화면 조립만, `components/ui/` 는 범용 UI,
  `lib/` 는 API 클라이언트 같은 기술 공통.

## 기능 사이에는 엔티티를 넘기지 않는다

ID 나 필요한 입력 값만 작은 데이터 객체로 건넨다.

```kotlin
// 하지 않는다
fun render(user: User): String

// 한다
fun render(header: ResumeHeaderData): String
```

`document` 는 `user` 를 import 하지 않는다. 의존은 한 방향으로만 흐른다.
거대한 계층이나 포트 인터페이스는 필요 없다. 기능 간 결합만 끊으면 된다.

변환에 딸린 책임도 넘기는 쪽이 갖는다 — 전화번호 표기는 `user` 가 만들어서 건넨다.

## 공통화는 두 번째 중복부터

- 공통 `BaseEntity` 를 미리 만들지 않는다.
- 범용 CRUD 서비스·컨트롤러를 만들지 않는다.
- 모든 응답을 감싸는 공통 응답 객체를 만들지 않는다.
- 미래 기능용 패키지·테이블·enum 값을 미리 만들지 않는다.
- 비슷한 코드가 실제로 두세 번 나온 뒤에 공통화한다.

`common` 에는 설정, 예외 처리, 정말 범용적인 기술 코드만 둔다.
**도메인 규칙이 `common` 으로 옮겨가기 시작하면 경계가 무너진 신호다.**

## 저장소 규칙

- 스키마의 정본은 Flyway 다. `ddl-auto` 는 `validate`. 스키마 변경은 새 마이그레이션으로.
- 버전 테이블(`document_version` 등)은 **불변**이다. 수정은 새 `seq` 를 insert 하고
  head 포인터만 옮긴다. 기존 행을 `UPDATE` 하지 않는다.
- 쓰기·단건 조회는 JPA, 집계와 `jsonb` 질의는 `JdbcClient` 네이티브 SQL.
- 테스트에 H2 를 쓰지 않는다. `jsonb` 와 마이그레이션이 검증되지 않고 조용히 갈라진다.

## 프런트 규칙

- 캐시 키와 fetcher 는 같은 파일(`features/<도메인>/queries.ts`)에 둔다. 늘 같이 바뀐다.
- 서버 데이터를 클라이언트 상태로 복사하지 않는다. TanStack Query 캐시가 단일 출처다.
- 브라우저 코드는 API 주소를 모른다. 항상 `/api/...` 상대 경로.
- 파일명은 kebab-case. 타입에 `I`/`T` 접두사를 붙이지 않는다. Props 는 `{Component}Props`.
- **모바일과 데스크탑의 기능은 동일하다.** 폭별로 컴포넌트를 나누지 않고 같은 컴포넌트가
  레이아웃만 바꾼다. 표는 좁은 폭에서 카드로, 입력 많은 화면은 단을 접어 세로로 쌓는다.

## 주석

"무엇"이 아니라 **"왜"** 를 적는다. 코드를 읽으면 아는 것은 적지 않는다.
특히 다르게 할 수도 있었는데 이렇게 한 이유, 나중에 되돌리고 싶어질 결정에 남긴다.

## 커밋

```
[영어작업유형] 커밋내용 - 간단설명
```

- **제목 한 줄만.** 본문을 붙이지 않는다.
- 대괄호 안은 영어 소문자: `init` `add` `update` `refactor` `docs` `chore` `fix`
- 대괄호 뒤는 한국어.
- `Co-Authored-By` 같은 트레일러를 넣지 않는다.

예: `[add] 홈 화면 - 있으면 조회, 없으면 가입`

## 완료 조건

```bash
./gradlew :backend:test
pnpm --dir web build          # tsc -b 를 포함한다
```

둘 다 통과해야 끝난 것이다.

## 알아둘 것

- **인증이 없다.** 홈의 `POST /api/sessions` 는 이름·휴대폰·이메일로 사람을 찾을 뿐이다.
  셋 다 이력서에 적혀 있는 값이라 보안 기능이 아니다. V1 이 로컬 전용이라 성립한다.
  외부에 공개한다면 그 전에 실제 인증을 붙여야 한다.
- 개발은 백엔드(`:8080`)와 프런트(`:5173`)를 따로 띄우고 **5173 으로 접속**한다.
  SPA 는 `bootJar` 에만 들어간다. 배포는 jar 하나로 둘 다 서빙한다.
- 사용자의 로컬 DB 에는 실제 데이터가 들어 있다. **테스트로 그 데이터를 수정하지 않는다.**
