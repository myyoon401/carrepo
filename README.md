# 커리포 Carrepo

> 공고부터 면접까지 쌓일수록 나를 더 잘 아는 커리어 저장소

## 개요

지원 공고를 모아두고, 이력서와 지원 문구를 만들어 저장하고, 전형 결과까지 쌓아
**다음 지원에 재활용하는 개인 커리어 저장소**입니다.

공고 수집 자체는 외부 채용 사이트에 맡깁니다. 커리포가 맡는 것은 **붙여넣은 이후**입니다.

### 무엇을 해결하는가

이미 마크다운 디렉터리로 하고 있던 일을 서비스로 옮깁니다. 손으로 할 때 깨지던 지점이 출발점입니다.

| 손으로 할 때 | 커리포에서 |
| --- | --- |
| 지원 이력 표를 직접 갱신하다 어느 시점부터 멈춤 | 지원 건 조회로 항상 최신 |
| 이력서 버전이 파일명(`resume_v16`, `_최종`)에만 존재 | 버전 행 + 제출본 FK 로 계보가 남음 |
| 초안과 제출본이 다른 파일로 흩어짐 | 제출 시점의 내용을 못 박아 보관 |
| "이 소재를 저번에 썼던가"를 기억에 의존 | 소재 ↔ 지원 건 연결로 추적 |

마지막 줄이 커리포의 존재 이유입니다. 공고를 저장하는 서비스가 아니라,
**과거 지원 데이터를 다음 지원에 재사용하는 서비스**입니다.

### 지금 되는 것

첫 조각만 올라가 있습니다.

- 가입 — 이름 · 휴대폰번호 · 이메일 · 이력서 타이틀
- 가입과 동시에 이력서 머리말 HTML 자동 생성
- 내 정보 수정 시 **이력서 새 버전 생성** (이전 버전은 보존)
- 미리보기 · 인쇄(PDF 저장) · HTML 내려받기

## 빠른 시작

필요한 것: JDK 21, Node 24+, pnpm, Docker

> macOS 에서 Homebrew 로 JDK 21 을 설치하면 keg-only 라 `java` 명령에 잡히지 않습니다.
> Gradle 은 `gradle.properties` 의 경로로 찾아가므로 `./gradlew` 는 그냥 동작하지만,
> `java -jar` 로 직접 띄우려면 PATH 에 올려야 합니다.
> `export PATH=/opt/homebrew/opt/openjdk@21/bin:$PATH`

```bash
docker compose up -d                 # PostgreSQL
./gradlew :backend:bootRun           # 백엔드 → localhost:8080
pnpm --dir web install && pnpm --dir web dev   # 프런트 → localhost:5173
```

브라우저에서 <http://localhost:5173> 을 엽니다.
프런트는 API 주소를 모릅니다 — 항상 `/api/...` 상대 경로로 부르고, dev 에서는 Vite 프록시가 백엔드로 넘깁니다.

### 배포

```bash
./gradlew :backend:bootJar           # SPA 를 jar 안에 넣는다
java -jar backend/build/libs/carrepo.jar
```

**산출물은 jar 하나, 프로세스 하나.** 배포에서는 같은 서버가 API 와 화면을 함께 서빙하므로
프록시도 CORS 설정도 없습니다. 프런트 없이 빨리 받아야 할 때만 `-PskipWeb`.

## 구조

```
carrepo/
├── backend/        Kotlin + Spring Boot + JPA   (:8080)
│   └── com.carrepo/{user, document, common}
├── web/            React + Vite + TypeScript    (:5173, dev 전용)
│   └── src/{routes, features, components, lib}
├── docs/           설계 문서
└── docker-compose.yml
```

패키지는 레이어가 아니라 **기능** 기준으로 나눕니다. 도메인이 여럿이라 레이어 우선으로 두면
기능 하나를 고치는 데 여러 패키지를 왕복하게 됩니다.

## 스택

| 영역 | 선택 | 이유 |
| --- | --- | --- |
| 저장소 | PostgreSQL + `jsonb` | 하려는 질의가 조인·집계다. 회사마다 다른 폼은 `jsonb` 가 흡수 |
| 마이그레이션 | Flyway | 버전 테이블은 제약조건이 핵심이라 `ddl-auto` 로는 부족 |
| 백엔드 | Kotlin · Spring Boot · JPA | 쓰기는 JPA, 집계는 네이티브 SQL 로 경로를 나눈다 |
| 프런트 | React · Vite · TanStack Query · Tailwind | 서버 상태 캐싱과 스타일 부담을 라이브러리가 가져간다 |

## 다음 단계

| 단계 | 내용 |
| --- | --- |
| V1 | 공고 · 지원 건 · 전형 이력 · 면접 자료 · 내보내기 |
| **V2** | 소재 라이브러리 · 문항 초안과 버전 · **소재 사용 이력** ← 여기부터 본전 |
| V3 | 이력서 본문 편집 · 제출본 고정 · 첨부 |
| V4 | 전적 집계 |

## 문서

- [프로젝트 구조 설계](docs/프로젝트-구조-설계.md) — 데이터 모델, 패키지 구조, 결정과 근거
