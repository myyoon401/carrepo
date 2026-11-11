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

## 구동 방법

필요한 것: JDK 21, Node 24+, pnpm, Docker

> macOS 에서 Homebrew 로 JDK 21 을 설치하면 keg-only 라 `java` 명령에 잡히지 않습니다.
> Gradle 은 `gradle.properties` 의 경로로 찾아가므로 `./gradlew` 는 그냥 동작하지만,
> `java -jar` 로 직접 띄우려면 PATH 에 올려야 합니다.
> `export PATH=/opt/homebrew/opt/openjdk@21/bin:$PATH`

### 1. 데이터베이스 (항상 먼저)

```bash
docker compose up -d
```

Docker 가 colima 기반이면 `colima start` 를 먼저 합니다.

> 백엔드와 PostgreSQL 은 **`127.0.0.1` 에만 바인딩**합니다. 같은 네트워크의 다른 기기(폰 등)에서
> 열어야 할 때만 `SERVER_ADDRESS=0.0.0.0 ./gradlew :backend:bootRun` 처럼 명시적으로 풀고,
> 신뢰하는 망에서만 씁니다 — 인증이 없어서 주소를 아는 누구나 전부 읽을 수 있습니다.
PostgreSQL 은 설치하지 않습니다 — 컨테이너로만 띄웁니다.

### 2. 개발 — 터미널 두 개

프런트를 고쳐가며 작업할 때 쓰는 방식입니다. 저장하면 화면이 바로 바뀝니다.

```bash
# 터미널 1 — 백엔드
./gradlew :backend:bootRun                     # → localhost:8080

# 터미널 2 — 프런트
pnpm --dir web install                         # 처음 한 번만
pnpm --dir web dev                             # → localhost:5173
```

접속은 <http://localhost:5173> 입니다. **8080 이 아닙니다** —
SPA 는 배포 산출물(`bootJar`)에만 들어가고 `bootRun` 에는 들어가지 않습니다.
개발 중에 프런트를 매번 다시 빌드하지 않기 위해서입니다.

프런트는 API 주소를 모릅니다. 항상 `/api/...` 상대 경로로 부르고,
dev 에서는 Vite 프록시가 8080 으로 넘깁니다. 그래서 CORS 설정이 없습니다.

> 프런트만 띄워도 화면은 보이지만 가입은 실패합니다 — 넘길 백엔드가 없기 때문입니다.

### 3. 한 프로세스로 띄우기

실제 배포와 같은 형태입니다. 터미널 하나로 끝납니다.

```bash
./gradlew :backend:bootJar                     # SPA 를 jar 안에 넣는다
java -jar backend/build/libs/carrepo.jar       # → localhost:8080 하나로 화면·API 둘 다
```

**산출물은 jar 하나, 프로세스 하나.** 같은 서버가 API 와 화면을 함께 서빙하므로
프록시도 게이트웨이도 없습니다. 대신 프런트를 고칠 때마다 다시 빌드해야 하므로,
개발 중에는 2번 방식이 편합니다.

프런트 없이 빨리 받아야 할 때만 `-PskipWeb` 을 붙입니다.

### 4. 내리기

```bash
# 백엔드 — 실행 중인 터미널에서 Ctrl+C
lsof -ti:8080 | xargs kill      # 포트가 안 풀릴 때

docker compose down             # DB 중지 (데이터는 볼륨에 남는다)
docker compose down -v          # 데이터까지 삭제
colima stop                     # Docker VM 자체를 내림
```

> `bootRun` 중에 Gradle 진행률이 85% 에서 멈춰 있는 것은 정상입니다.
> `bootRun` 은 서버를 내려야 끝나는 태스크라 진행률이 100% 가 되지 않습니다.
> `Tomcat started on port 8080` 이 찍혔으면 기동된 것입니다.
> 진행률 표시가 거슬리면 `--console=plain` 을 붙입니다.

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
