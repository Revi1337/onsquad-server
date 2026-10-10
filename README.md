# OnSquad Server

취미/운동 모임 플랫폼 **OnSquad** 의 백엔드 API 서버입니다.

사용자는 **크루(Crew)** 라는 커뮤니티를 만들고, 그 안에서 **스쿼드(Squad)** 라는 소규모 활동 모임을 열어 참여자를 모집합니다.

- 개발 기간: 2024-04 ~ 현재 (중간에 조금씩 쉬어간 기간이 있습니다)

### 이 프로젝트에서 중점을 둔 것

- **역할 기반 권한**: 크루(`OWNER` / `MANAGER` / `GENERAL`)와 스쿼드(`LEADER` / `GENERAL`)의 권한 규칙을 도메인 Policy 로 모아 일관되게 검증합니다.
- **동시성 정합성**: 가입 승인, 탈퇴, 위임, 추방처럼 경합이 생기는 기능마다 락 전략을 비교하고 테스트로 확정했습니다.
- **이벤트 기반 후처리**: 알림, 이력, 활동 점수, 캐시 정리를 도메인 이벤트로 분리해 핵심 로직과 섞이지 않게 했습니다.
- **장애 내성**: Redis 가 느려지거나 내려가도 캐시, 인증, 토큰, 요청 제한이 폴백으로 계속 동작합니다.
- **검증 가능한 품질**: 계층별 테스트와 Testcontainers 통합 테스트, REST Docs 로 만든 API 문서를 빌드에 연결했습니다.

---

## 목차

1. [주요 기능](#주요-기능)
2. [기술 스택](#기술-스택)
3. [API 개요](#api-개요)
4. [아키텍처](#아키텍처)
5. [기술적 하이라이트](#기술적-하이라이트)
6. [인프라와 운영](#인프라와-운영)
7. [테스트](#테스트)
8. [실행 방법](#실행-방법)

---

## 주요 기능

### 서비스 구조

```
Member ──가입/생성──▶ Crew (커뮤니티)
                      ├─ CrewMember   : OWNER / MANAGER / GENERAL
                      ├─ CrewRequest  : 가입 신청
                      ├─ Announce     : 공지 (상단 고정)
                      ├─ Leaderboard  : 활동 점수 랭킹
                      └─ Squad (활동 모임)
                           ├─ SquadMember  : LEADER / GENERAL
                           ├─ SquadRequest : 참가 신청
                           └─ SquadComment : 댓글 / 대댓글
```

- **스쿼드는 크루 안에서만** 만들 수 있고, 스쿼드를 만들거나 댓글을 쓰려면 먼저 그 크루의 멤버여야 합니다.
- 가입 신청/승인/거절, 댓글/대댓글, 스쿼드 생성 같은 주요 활동은 **도메인 이벤트**로 알림, 이력, 활동 점수로 이어집니다.

### 회원/인증

- **회원가입**
    - 이메일 인증코드를 받아 검증한 뒤에만 가입할 수 있습니다. (코드 유효 3분, 인증 성공 후 5분 안에 가입)
    - 인증 메일 발송은 별도 스레드풀에서 비동기로 처리합니다.
    - 이메일, 닉네임(2~8자) 중복을 막고, 비밀번호는 8~20자 영문/숫자/특수문자 조합과 비밀번호 확인 일치를 요구합니다. 저장은 BCrypt 로 합니다.
- **로그인**
    - 이메일/비밀번호(JWT), 카카오/구글 소셜 로그인 (처음 로그인하면 자동으로 가입되며, 닉네임은 랜덤 8자로 부여됩니다)
    - Access / Refresh 토큰 발급과 재발급 — **RTR(Refresh Token Rotation)** 방식으로, 재발급할 때마다 Refresh Token 도 새로 발급해 이전 토큰은 쓸 수 없습니다
- **내 정보**
    - 프로필 수정, 프로필 이미지 변경/삭제 (이전 이미지는 S3 에서 삭제)
    - 비밀번호 변경은 현재 비밀번호 확인 후에만 가능합니다
    - 회원 탈퇴 시 내가 크루장/리더인 크루/스쿼드는 다른 멤버가 있어도 함께 삭제되고, 다른 곳에 남긴 참여 기록도 정리합니다.
        - 내가 쓴 공지는 남고, 작성자는 '탈퇴한 회원'으로 표시됩니다.
        - 내가 쓴 스쿼드 댓글은 그 댓글에 달린 다른 회원의 답글과 함께 삭제됩니다. 다른 회원의 답글까지 사라지지 않도록 마스킹 처리로의 개선을 검토 중입니다.

### 크루 (Crew)

크루원은 역할에 따라 할 수 있는 일이 달라집니다.

| 역할              | 할 수 있는 일                                          |
|-----------------|---------------------------------------------------|
| `OWNER` (크루장)   | 크루 수정/삭제, 크루장 위임, 공지 상단 고정, 모든 멤버 관리              |
| `MANAGER` (매니저) | 가입 신청 승인/거절, 공지 작성, 참여자 목록 조회, 일반 멤버 관리, 관리 화면 조회 |
| `GENERAL` (일반)  | 크루 조회, 스쿼드 생성/참여, 댓글 작성                           |

- **생성/수정/삭제**: 이름/소개/상세 설명/해시태그/대표 이미지를 설정합니다. 만든 사람이 `OWNER` 가 되고, 크루를 삭제하면 하위 스쿼드까지 함께 정리합니다.
- **조회**: 이름 접두사 검색, 단건 조회(로그인하면 참여/신청 여부 포함), 공지/랭커/스쿼드를 모은 메인 화면, 통계를 보여주는 관리 화면을 제공합니다.
- **가입 신청**: 신청 후 `MANAGER` 이상이 승인/거절하고, 신청자는 취소할 수 있습니다. 같은 신청의 연타는 요청 제한으로 막습니다.
- **멤버 관리**: 상위 역할이 하위 역할을 추방할 수 있고, 크루장 위임과 탈퇴를 지원합니다. 마지막 한 명이 탈퇴하면 크루가 함께 삭제됩니다.
- **공지사항**: 작성, 수정, 삭제, 상단 고정을 지원하고 역할에 따라 가능 여부가 다릅니다. 수정/삭제는 작성자 본인과 크루장이 할 수 있고, 작성자가 탈퇴한 공지는 크루장만 할 수 있습니다. 응답에는 현재 사용자의 수정/고정 가능 여부가 포함됩니다.
- **리더보드**: 크루 가입, 스쿼드 생성/참가, 댓글 활동에 점수를 부여해 주간 랭킹을 만듭니다. (점수 체계는 [기술적 하이라이트](#2-redis-기반-주간-리더보드) 참고)

### 스쿼드 (Squad)

- **생성**: 크루 멤버라면 누구나 만들 수 있고, 만든 사람이 `LEADER` 가 됩니다. 카테고리(최대 5개)와 정원(2~1,000명)을 설정합니다.
- **조회**: 카테고리별 목록, 관리 목록, 단건 조회를 제공합니다. 단건 조회는 보는 사람에 따라 참여/신청 여부와 탈퇴/삭제 가능 여부를 함께 내려줍니다.
- **참가 신청**: 신청 후 `LEADER` 가 승인/거절하고, 신청자는 취소할 수 있습니다.
- **멤버 관리**: `LEADER` 가 리더 위임과 추방을 하고, 마지막 한 명이 탈퇴하면 스쿼드가 삭제됩니다. 스쿼드 삭제는 `LEADER` 또는 크루 `OWNER` 가 할 수 있습니다.
- **댓글/대댓글**: 크루 멤버가 작성하고, 대댓글은 1단계만 지원합니다. 삭제된 댓글은 마스킹되며, 대댓글은 별도 API 로 조회합니다.

### 알림/이력

- **실시간 알림 (SSE)**
    - 크루/스쿼드 가입 신청, 승인, 거절, 댓글, 대댓글이 대상자에게 전달됩니다
    - 알림은 DB 에 저장한 뒤 SSE 로 보내므로, 접속하지 않았거나 연결이 끊긴 동안의 알림도 `Last-Event-ID` 로 복구합니다
    - 내가 나에게 보내는 알림은 만들지 않고, 알림 저장과 전송은 별도 스레드풀에서 비동기로 처리합니다
    - 목록 조회, 단건 읽음, 전체 읽음을 지원합니다
- **활동 이력 (History)**
    - 크루 생성/가입 신청/승인/거절/취소, 스쿼드 생성/신청/승인/거절/취소, 댓글/대댓글(총 12종)을 자동으로 기록합니다
    - 기간(`from`~`to`)과 유형으로 내 이력을 조회합니다

---

## 기술 스택

| 영역                   | 내용                                                                                  |
|----------------------|-------------------------------------------------------------------------------------|
| Language / Framework | Java 17 (`--enable-preview`), Spring Boot 3.3.1, Gradle                             |
| Persistence          | Spring Data JPA (Hibernate), QueryDSL 5.1.0                                         |
| Database             | MySQL (운영), H2 (로컬), SQLite (파일 휴지통), Redis                                         |
| Auth                 | Spring Security (Stateless), JWT (jjwt) + RTR, OAuth2 (Kakao, Google)               |
| Storage              | AWS S3 + CloudFront                                                                 |
| Resilience           | Resilience4j (Circuit Breaker), Spring Retry, Redis 락 (`SET NX` 직접 구현), Caffeine 폴백 |
| Observability        | Spring Actuator, Micrometer + Prometheus, Grafana, Discord 웹훅 알림                    |
| Docs                 | Spring REST Docs + Asciidoctor                                                      |
| Test                 | JUnit 5, Mockito, Testcontainers (MySQL, Redis, LocalStack)                         |
| CI/CD                | GitHub Actions                                                                      |

---

## API 개요

모든 API 는 `/api` 하위에 있으며, OAuth2 인가 URL/콜백과 SSE 를 제외한 응답은 아래 형태로 통일되어 있습니다.

```json
{
  "status": 200,
  "success": true,
  "data": {}
}
```

실패 시에는 `success: false` 와 함께 `data` 대신 `error` 에 자체 정의한 `ProblemDetail`(`code`, `message`, 검증 실패 시 `parameters`)이 담깁니다.

```json
{
  "status": 400,
  "success": false,
  "error": {
    "code": "C001",
    "message": "유효성 검증 실패",
    "parameters": [
      "nickname"
    ]
  }
}
```

- **HTTP 상태 코드는 바디의 `status` 와 같습니다.** 성공은 200 (본문이 없는 성공도 200 이며 `data` 는 빈 문자열), 생성은 201, 오류는 해당 상태(400, 401, 403, 404, 409, 413, 415, 500 등)로 내려갑니다.
- OAuth2 인가 URL 조회는 `Location` 헤더만 담은 200, 콜백은 302 리다이렉트이고, SSE 는 `text/event-stream` 입니다.
- 인증이 필요한 API 는 `Authorization: Bearer {accessToken}` 헤더로 식별합니다.
- 페이징은 `page`(1부터 시작), `size`(기본 10개, 최대 100개) 파라미터로 지정합니다.
- 상세 명세는 Spring REST Docs 로 생성되며, 빌드 시 `static/` 에 포함됩니다. (`src/docs/asciidoc`)

총 **72개**의 REST API 를 제공합니다.

### Auth

| Method | Path                              | 설명                                   |
|--------|-----------------------------------|--------------------------------------|
| POST   | `/api/auth/login`                 | 이메일/비밀번호 로그인                         |
| POST   | `/api/auth/reissue`               | 토큰 재발급                               |
| POST   | `/api/auth/send`                  | 이메일 인증코드 발송                          |
| POST   | `/api/auth/verify`                | 이메일 인증코드 검증                          |
| GET    | `/api/login/oauth2/{vendor}`      | 소셜 로그인 인가 URL 조회 (`kakao`, `google`) |
| GET    | `/api/login/oauth2/code/{vendor}` | 소셜 로그인 콜백                            |

### Member

| Method | Path                                 | 설명                             |
|--------|--------------------------------------|--------------------------------|
| GET    | `/api/members/check-nickname`        | 닉네임 중복 확인                      |
| GET    | `/api/members/check-email`           | 이메일 중복 확인                      |
| POST   | `/api/members`                       | 회원가입                           |
| GET    | `/api/members/me`                    | 내 정보 조회                        |
| PUT    | `/api/members/me`                    | 내 정보 수정                        |
| DELETE | `/api/members/me`                    | 회원 탈퇴                          |
| PATCH  | `/api/members/me/password`           | 비밀번호 변경                        |
| PATCH  | `/api/members/me/image`              | 프로필 이미지 변경                     |
| DELETE | `/api/members/me/image`              | 프로필 이미지 삭제                     |
| GET    | `/api/members/me/crew-participants`  | 내가 참여 중인 크루                    |
| GET    | `/api/members/me/crew-requests`      | 내 크루 가입 신청 내역                  |
| GET    | `/api/members/me/squad-participants` | 내가 참여 중인 스쿼드                   |
| GET    | `/api/members/me/squad-requests`     | 내 스쿼드 참가 신청 내역                 |
| GET    | `/api/members/me/histories`          | 내 활동 이력 (`from`, `to`, `type`) |

### Crew

| Method | Path                              | 설명                |
|--------|-----------------------------------|-------------------|
| GET    | `/api/crews/check-name`           | 크루 이름 중복 확인       |
| POST   | `/api/crews`                      | 크루 생성 (multipart) |
| GET    | `/api/crews`                      | 크루 이름 접두사 검색      |
| GET    | `/api/crews/{crewId}`             | 크루 단건 조회          |
| PUT    | `/api/crews/{crewId}`             | 크루 수정             |
| DELETE | `/api/crews/{crewId}`             | 크루 삭제             |
| PATCH  | `/api/crews/{crewId}/image`       | 크루 이미지 변경         |
| DELETE | `/api/crews/{crewId}/image`       | 크루 이미지 삭제         |
| GET    | `/api/crews/{crewId}/main`        | 크루 메인             |
| GET    | `/api/crews/{crewId}/manage`      | 크루 관리 요약          |
| GET    | `/api/crews/{crewId}/leaderboard` | 크루 리더보드           |

### Crew Member / Request

| Method | Path                                                 | 설명         |
|--------|------------------------------------------------------|------------|
| GET    | `/api/crews/{crewId}/members`                        | 크루 참여자 목록  |
| PATCH  | `/api/crews/{crewId}/members/{targetMemberId}/owner` | 크루장 위임     |
| DELETE | `/api/crews/{crewId}/members/me`                     | 크루 탈퇴      |
| DELETE | `/api/crews/{crewId}/members/{targetMemberId}`       | 크루원 추방     |
| POST   | `/api/crews/{crewId}/requests`                       | 가입 신청      |
| GET    | `/api/crews/{crewId}/requests`                       | 가입 신청 목록   |
| PATCH  | `/api/crews/{crewId}/requests/{requestId}`           | 가입 승인      |
| DELETE | `/api/crews/{crewId}/requests/{requestId}`           | 가입 거절      |
| DELETE | `/api/crews/{crewId}/requests/me`                    | 내 가입 신청 취소 |

### Announce

| Method | Path                                             | 설명                 |
|--------|--------------------------------------------------|--------------------|
| POST   | `/api/crews/{crewId}/announces`                  | 공지 작성              |
| GET    | `/api/crews/{crewId}/announces`                  | 공지 목록              |
| GET    | `/api/crews/{crewId}/announces/{announceId}`     | 공지 단건              |
| PUT    | `/api/crews/{crewId}/announces/{announceId}`     | 공지 수정              |
| PATCH  | `/api/crews/{crewId}/announces/{announceId}/pin` | 상단 고정 변경 (`state`) |
| DELETE | `/api/crews/{crewId}/announces/{announceId}`     | 공지 삭제              |

### Squad

| Method | Path                                | 설명                         |
|--------|-------------------------------------|----------------------------|
| POST   | `/api/crews/{crewId}/squads`        | 스쿼드 생성                     |
| GET    | `/api/crews/{crewId}/squads`        | 크루의 스쿼드 목록 (`category` 필터) |
| GET    | `/api/crews/{crewId}/squads/manage` | 스쿼드 관리 목록                  |
| GET    | `/api/squads/{squadId}`             | 스쿼드 단건                     |
| DELETE | `/api/squads/{squadId}`             | 스쿼드 삭제                     |

### Squad Member / Request

| Method | Path                                                    | 설명         |
|--------|---------------------------------------------------------|------------|
| GET    | `/api/squads/{squadId}/members`                         | 스쿼드 참여자 목록 |
| PATCH  | `/api/squads/{squadId}/members/{targetMemberId}/leader` | 리더 위임      |
| DELETE | `/api/squads/{squadId}/members/me`                      | 스쿼드 탈퇴     |
| DELETE | `/api/squads/{squadId}/members/{targetMemberId}`        | 스쿼드원 추방    |
| POST   | `/api/squads/{squadId}/requests`                        | 참가 신청      |
| GET    | `/api/squads/{squadId}/requests`                        | 참가 신청 목록   |
| PATCH  | `/api/squads/{squadId}/requests/{requestId}`            | 참가 승인      |
| DELETE | `/api/squads/{squadId}/requests/{requestId}`            | 참가 거절      |
| DELETE | `/api/squads/{squadId}/requests/me`                     | 내 참가 신청 취소 |

### Squad Comment

| Method | Path                                                | 설명      |
|--------|-----------------------------------------------------|---------|
| POST   | `/api/squads/{squadId}/comments`                    | 댓글 작성   |
| POST   | `/api/squads/{squadId}/comments/{parentId}/replies` | 대댓글 작성  |
| GET    | `/api/squads/{squadId}/comments`                    | 댓글 목록   |
| GET    | `/api/squads/{squadId}/comments/{parentId}/replies` | 대댓글 더보기 |
| PATCH  | `/api/squads/{squadId}/comments/{commentId}`        | 댓글 수정   |
| DELETE | `/api/squads/{squadId}/comments/{commentId}`        | 댓글 삭제   |

### Notification / Metadata

| Method | Path                                       | 설명                                               |
|--------|--------------------------------------------|--------------------------------------------------|
| GET    | `/api/notifications/sse`                   | SSE 알림 구독 (`accessToken` 쿼리, `Last-Event-ID` 헤더) |
| GET    | `/api/notifications`                       | 내 알림 목록                                          |
| PATCH  | `/api/notifications/{notificationId}/read` | 알림 읽음                                            |
| PATCH  | `/api/notifications/read-all`              | 알림 전체 읽음                                         |
| GET    | `/api/categories`                          | 카테고리 목록                                          |
| GET    | `/api/hashtags`                            | 해시태그 목록                                          |

---

## 아키텍처

DDD 에서 영감을 받은 **레이어드 아키텍처** 이며, 계층 간 의존은 단방향입니다.

```
presentation → application → domain ← infrastructure
```

### 도메인

`member` `auth` `crew` `crew_member` `crew_request` `crew_hashtag`
`squad` `squad_member` `squad_request` `squad_category` `squad_comment`
`announce` `notification` `history` `hashtag` `category`

+ `common` `infrastructure`

각 도메인은 `presentation / application / domain / infrastructure` 로 나뉩니다.

### 설계 규칙

- **Value Object**: 불변 객체가 스스로 길이/형식을 검증합니다.
- **Policy**: 권한과 규칙 판단을 한곳에 모읍니다.
- **AOP**: 요청 제한(`@Throttling`)을 AOP 로 분리합니다. 활동 이력은 크루/스쿼드 생성과 신청 취소를 AOP 로, 신청/승인/거절과 댓글을 도메인 이벤트로 기록합니다.

---

## 기술적 하이라이트

### 1. 동시성 전략을 시나리오별로 비교하고 확정

크루 도메인의 경합 상황마다 결함을 먼저 **재현**하고, 낙관적 락 / 비관적 락 / Atomic Update 를 각각 구현/테스트한 뒤 전략을 확정했습니다.

| 시나리오   | 확정 전략                    |
|--------|--------------------------|
| 가입 승인  | 비관적 락                    |
| 크루 탈퇴  | 비관적 락                    |
| 크루장 위임 | 낙관적 락                    |
| 크루원 추방 | Atomic Update (+ 수동 버저닝) |

인증코드 검증도 원자적 업데이트가 없을 때의 중복 성공 문제를 테스트로 입증한 뒤 해결했습니다. 관련 테스트는 `src/test/java/.../concurrency` 에 있습니다.

### 2. Redis 기반 주간 리더보드

- 활동별 가중치(크루 가입 5, 스쿼드 생성 10, 스쿼드 참여 3, 댓글/대댓글 1)를 **Lua 스크립트**로 원자적으로 반영합니다.
- 점수와 반영 시각을 하나의 **복합 점수**로 합쳐 동점을 처리합니다.
- 매주 월요일 0시, 스냅샷(`RENAME`)을 뜬 뒤 Redis 점수 기준 상위 50명을 후보로 가져옵니다. RDB 에서 현재 크루원인지 확인하고 프로필(닉네임/MBTI)을 보강한 뒤, 탈퇴자를 제외하고 메모리에서 순위를 다시 매겨 섀도 테이블에 적재하고 교체(swap)합니다.
  스케줄러는 직접 구현한 Redis 락(`SET NX` + TTL)으로 여러 인스턴스에서의 중복 실행을 막고, 락을 잡지 못한 인스턴스는 대기하지 않고 실행을 건너뜁니다.
- 점수 반영은 실패하면 최대 3회까지 시도하며(최초 시도 포함), 최종 실패 시 크루/회원/활동 정보와 함께 로그를 남기고 Discord 로 알립니다.

### 3. Redis 장애 내성

Redis 가 느려지거나 내려가도 서비스가 계속 동작하도록, **Redis 사용 용도별(캐시 / 인증 / 요청 제한)로 Circuit Breaker 를 분리해** 적용했습니다. 한 용도의 장애가 다른 용도의 서킷을 열지 않습니다.

| 대상                  | 장애 시 동작                                         |
|---------------------|-------------------------------------------------|
| 캐시 (`@Cacheable` 등) | 캐시 miss 로 처리하고 MySQL 에서 조회                      |
| 스쿼드 카테고리 캐시         | MySQL 조회로 폴백                                    |
| 이메일 인증코드            | Redis 실패 시 RDB 로 폴백                             |
| 리프레시 토큰             | Redis 와 RDB 에 **이중 저장**, 조회는 Redis → RDB 순      |
| 요청 제한 (Throttling)  | 인스턴스 로컬 메모리(Caffeine)로 폴백 (장애 중에는 인스턴스별로 따로 제한) |

- Redis 명령 타임아웃은 1초로 설정해 장애를 빠르게 감지합니다.
- 폴백 동작은 Testcontainers 와 접속 불가능한 Redis 를 사용한 테스트로 검증합니다.

### 4. 파일 업로드 설계

- S3 업로드와 DB 트랜잭션을 Facade 에서 분리해, 트랜잭션 안에서 외부 I/O 로 커넥션을 점유하지 않습니다.
- DB 처리가 실패하면 `FileDeleteEvent` 로 이미 올라간 S3 객체를 정리합니다.
- 업로드 가능한 파일 형식은 서버 코드가 아니라 AWS S3 버킷 정책(Rule)으로 제한합니다.
- 삭제 대상 파일은 곧바로 S3 에서 지우지 않고 SQLite 휴지통(`file_recycle_bin`)에 먼저 기록합니다. 매일 자정 배치가 기록을 읽어 S3 에서 삭제하며, 실패한 파일은 다음 배치에서 다시 시도합니다. 그래서 S3 에서 실제로 지워지는 시점은 최대 하루 정도
  늦어집니다.
- 이미지 변경 시 CloudFront 캐시를 무효화합니다.

### 5. SSE 알림

- SSE 와 Redis Pub/Sub 을 조합해 알림을 전달합니다.
- 구독을 먼저 수행한 뒤 emitter 를 저장소에 등록해, 구독 실패 시 emitter 가 남지 않도록 연결 순서를 보장합니다.
- 알림은 먼저 DB 에 저장한 뒤 SSE 로 전송합니다. 재연결 시 클라이언트가 보낸 `Last-Event-ID` 이후의 알림을 DB 에서 조회해 순서대로 다시 보내므로, 연결이 끊긴 동안의 알림도 놓치지 않습니다.
- 45초마다 heartbeat 를 보내 연결을 유지합니다.

### 6. 토큰 재발급 (Refresh Token Rotation)

- `POST /api/auth/reissue` 는 서명 검증에 더해, 서버에 저장된 회원의 Refresh Token 과 **값이 일치하는지** 확인한 뒤에만 새 토큰 쌍을 발급합니다.
- 재발급할 때마다 Access / Refresh Token 을 모두 새로 만들고 저장된 Refresh Token 을 교체하므로, 한 번 사용한 Refresh Token 은 다시 쓸 수 없습니다. (회원당 최신 Refresh Token 1개만 유효)
- Refresh Token 은 Redis 와 RDB 에 함께 저장해 Redis 장애 중에도 재발급이 동작합니다. (위 Redis 장애 내성 참고)
- 알려진 한계: 같은 Refresh Token 으로 동시에 두 번 요청하면 둘 다 통과할 수 있고(조회와 교체가 원자적이지 않음), Redis 장애 중 재발급했다면 복구 직후 Redis 에 남은 이전 토큰이 한 번 통과할 수 있습니다.

### 7. 요청 제한

`@Throttling(name, key, during)` 애노테이션과 SpEL 키로 중복 요청을 제한합니다. (예: 같은 크루에 대한 가입 신청 연타, 알림 전체 읽음 반복)

- 평소에는 Redis(`SET NX` + TTL)로 판단하므로 여러 인스턴스가 같은 기준을 공유합니다. 요청 제한 전용 Redis 클라이언트는 타임아웃을 200ms 로 짧게 두고 Circuit Breaker 를 겁니다.
- Redis 를 쓸 수 없으면 인스턴스 로컬 메모리(Caffeine, 최대 10,000건)로 폴백합니다. 폴백 중에는 인스턴스마다 따로 제한하므로 인스턴스 수만큼 요청이 통과할 수 있습니다. 중복 요청 방지가 목적이라 장애 중 일시적으로 이 정도 오차는 감수하고, 요청을 아예 막지
  않는 쪽을 택했습니다.

---

## 인프라와 운영

### 배포

- GitHub Actions 로 빌드/테스트 후 JAR 를 서버로 전달하고 `systemd` 서비스를 재시작합니다.
- 배포 완료 시 Discord 로 알립니다.
- EC2 운영 당시 OOM 발생 시 힙덤프를 자동 생성해 S3 로 업로드하도록 서버를 구성했습니다. (서버의 systemd 유닛 설정이라 저장소에는 포함되어 있지 않습니다.)

### 모니터링

- Spring Actuator 를 별도 관리 포트(`9097`)로 노출합니다. 경로는 `/metadata` 입니다.
    - `health`, `info`, `metrics`, `prometheus`, `http-exchanges`
- Prometheus 로 수집하고 Grafana 로 시각화합니다.
- AWS 삭제 실패, 리더보드 점수 반영 실패, 리더보드 스케줄러 실패는 Discord 웹훅으로 알립니다.

### 스케줄러

| 작업           | 주기                |
|--------------|-------------------|
| 리프레시 토큰 정리   | 매시 정각             |
| 인증코드 정리      | 매시 정각             |
| S3 삭제 배치     | 매일 0시 (설정 가능)     |
| 크루 리더보드 집계   | 매주 월요일 0시 (설정 가능) |

### 인프라 구성 파일

`infrastructure/` 에 로컬 개발/테스트용 Docker Compose 가 있습니다.

| 디렉터리      | 내용                  |
|-----------|---------------------|
| `mysql`   | MySQL               |
| `aws`     | LocalStack (S3)     |
| `monitor` | Prometheus, Grafana |

---

## 테스트

- 레이어별 테스트 기반 클래스를 제공합니다.
    - `PersistenceLayerTestSupport` : Repository
    - `ApplicationLayerTestSupport` : Service
    - `PresentationLayerTestSupport` : Controller (+ REST Docs)
- MySQL, Redis, LocalStack 은 **Testcontainers** 로 실제 컨테이너를 사용합니다. Docker 가 필요합니다.
- 동시성 테스트는 스레드 간 격리 문제로 CI 에서 제외되어 있으며(`@Disabled`), 필요할 때 단독 실행합니다.

```bash
./gradlew test
```

---

## 실행 방법

### 요구 사항

- JDK 17
- Docker (테스트와 로컬 인프라용)

> Java 17 의 `--enable-preview` 옵션이 필요합니다. 없으면 런타임 오류가 발생합니다.

### 설정

기본 `application.yml` 에는 더미 값만 들어 있습니다. 실제 값은 프로필별 설정 파일에 작성합니다.
이 파일들은 `.gitignore` 대상이며, CI 에서는 GitHub Secrets 로 생성합니다.

```
src/main/resources/
├── application.yml          # 공통 (더미 값)
├── application-local.yml    # 로컬
├── application-dev.yml      # 개발
└── application-prod.yml     # 운영
```

설정이 필요한 외부 요소는 다음과 같습니다.

- MySQL / Redis 접속 정보
- AWS S3 / CloudFront
- 메일(SMTP) 계정
- 카카오 / 구글 OAuth 클라이언트
- JWT 시크릿 키
- Discord 웹훅 URL

### 빌드와 실행

```bash
./gradlew clean build
java --enable-preview -Dspring.profiles.active=local -jar build/libs/onsquad.jar
```
