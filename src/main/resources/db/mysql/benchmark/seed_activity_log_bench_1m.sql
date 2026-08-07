-- =====================================================================
-- 크루 리더보드 SQL 배치 벤치마크용 시드 데이터 (crew_activity_log 전용)
-- 규모: 총 1,000,000건
--
-- leaderboard-workflow.md "4. 비정규화 — 단일 활동 로그 테이블" 단계의 실측용.
--
-- 중요: 2번(GROUP BY 구조 최적화, leaderboard-2-explan-analyze.txt)이 쓴
-- seed_leaderboard_bench_1m.sql과 완전히 동일한 로직(회원/크루 수, 분포 공식,
-- 규모)으로 crew_member/squad/squad_member/squad_comment 4개 테이블을 채운 뒤,
-- (이 테이블들에 이미 있던 base 시드 포함) 전체를 crew_activity_log로 재구성한다
-- (UNION ALL을 INSERT ... SELECT로 치환). CrewRankerJdbcRepository의 쿼리가
-- crew/member ID 범위를 필터링하지 않고 테이블 전체를 긁는 것과 동일하게 맞춰야
-- 1·2번과 결과 후보 수(그룹 수)가 완전히 일치한다 — base 시드를 제외하면 카디널리티가
-- 어긋난다. 반드시 앱 재기동 직후(그래서 base 시드의 날짜 상대값이 신선한 상태)에
-- 실행할 것 (leaderboard-4.md 참고).
--
-- 실행 순서 주의사항 (순서가 꼬이면 FK/유니크 제약 위반으로 실패함):
--   1) member  N명 생성
--   2) crew    N개 생성 (owner = 위에서 만든 member 중 하나)
--   3) crew_member  250,000건 (crew_id, member_id) 유니크 제약
--   4) squad   250,000건 (제약 없음)
--   5) squad_member 250,000건 (squad_id, member_id) 유니크 제약
--   6) squad_comment 250,000건 (제약 없음)
--   7) crew_activity_log를 비우고 crew_member/squad/squad_member/squad_comment
--      전체(base 포함)를 다시 이관 (가중치: 5/10/3/1)
--
-- 이 파일은 db/mysql/*-mysql.sql 글롭에 안 걸리도록 benchmark/ 서브디렉토리에 있음.
-- 실행: mysql -u <user> -p <database> < seed_activity_log_bench_1m.sql
-- 데이터 규모가 커서 실행 시간이 몇 분 걸릴 수 있음.
--
-- 재실행 시 기존 벤치마크 데이터에 누적되므로, 깨끗한 측정을 원하면 실행 전
-- crew_activity_log/crew_member/squad/squad_member/squad_comment/crew/member 중
-- 'bench4_'/'벤치4' 프리픽스가 붙은 행만 별도로 정리하거나, 별도 스키마에서 실행할 것.
-- =====================================================================

SET SESSION cte_max_recursion_depth = 300000;

-- 1) 회원 1,000명 생성
INSERT INTO member (email, address, address_detail, nickname, password, introduce, kakao_link, profile_image, user_type, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 1000
)
SELECT
    CONCAT('bench4_1m_member_', n, '@onsquad.test'),
    CONCAT('벤치주소', n),
    CONCAT('벤치상세주소', n),
    CONCAT('n', LPAD(n, 7, '0')),
    '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.',
    '벤치마크용 계정',
    '',
    'https://d3jao8gvkosd1k.cloudfront.net/onsquad/default/member-default.svg',
    'GENERAL',
    NOW(), NOW()
FROM seq;

SET @member_count = 1000;
SET @member_start_id = (SELECT MAX(id) FROM member) - @member_count + 1;

-- 2) 크루 1,000개 생성
INSERT INTO crew (name, introduce, detail, current_size, kakao_link, member_id, created_at, updated_at, version)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 1000
)
SELECT
    CONCAT('벤치4크루1m_', n),
    CONCAT('벤치크루 소개 ', n),
    CONCAT('벤치크루 디테일 ', n),
    1,
    'https://카카오링크.com',
    @member_start_id + ((n - 1) % @member_count),
    NOW() - INTERVAL (n % 30) DAY,
    NOW() - INTERVAL (n % 30) DAY,
    0
FROM seq;

SET @crew_count = 1000;
SET @crew_start_id = (SELECT MAX(id) FROM crew) - @crew_count + 1;

-- 3) crew_member 250,000건
INSERT INTO crew_member (crew_id, member_id, role, participate_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 250000
)
SELECT
    @crew_start_id + ((n - 1) % @crew_count),
    @member_start_id + (FLOOR((n - 1) / @crew_count) % @member_count),
    'GENERAL',
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND
FROM seq;

-- 4) squad 250,000건 (제약 없음)
INSERT INTO squad (title, content, capacity, current_size, remain, kakao_link, discord_link, created_at, updated_at, member_id, crew_id)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 250000
)
SELECT
    CONCAT('벤치4스쿼드타이틀1m_', n),
    CONCAT('벤치스쿼드본문1m_', n),
    8, 1, 7,
    'https://카카오링크.com', 'https://디스코드.com',
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND,
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND,
    @member_start_id + ((n - 1) % @member_count),
    @crew_start_id + ((n - 1) % @crew_count)
FROM seq;

SET @squad_count = 250000;
SET @squad_start_id = (SELECT MAX(id) FROM squad) - @squad_count + 1;

-- 5) squad_member 250,000건
INSERT INTO squad_member (squad_id, member_id, role, participate_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 250000
)
SELECT
    @squad_start_id + (n - 1),
    @member_start_id + ((n - 1) % @member_count),
    'GENERAL',
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND
FROM seq;

-- 6) squad_comment 250,000건 (제약 없음)
INSERT INTO squad_comment (content, deleted, squad_id, member_id, parent_id, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 250000
)
SELECT
    CONCAT('벤치마크 댓글 ', n),
    FALSE,
    @squad_start_id + ((n - 1) % @squad_count),
    @member_start_id + ((n - 1) % @member_count),
    NULL,
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND,
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND
FROM seq;

-- 7) crew_member/squad/squad_member/squad_comment 전체(base 시드 + 방금 넣은 벤치 데이터)를
--    crew_activity_log로 재구성 (CrewRankerJdbcRepository의 UNION ALL과 동일한 가중치).
--    1·2번 쿼리도 crew/member ID 범위를 필터링하지 않고 테이블 전체를 긁으므로, base 시드까지
--    그대로 포함시켜야 1·2번과 결과 건수(그룹 수)가 일치한다. GROUP BY 특성상 base 행을 몇 번
--    다시 이관해도 고유 (crew_id, member_id) 그룹 수는 늘지 않지만, 직전 규모의 잔존분을
--    없애기 위해 매번 전체를 지우고 다시 채운다.
DELETE FROM crew_activity_log;

INSERT INTO crew_activity_log (crew_id, member_id, activity_type, weight, created_at)
SELECT crew_id, member_id, 'CREW_PARTICIPANT', 5, participate_at
FROM crew_member
UNION ALL
SELECT crew_id, member_id, 'SQUAD_CREATE', 10, created_at
FROM squad
UNION ALL
SELECT s.crew_id, sm.member_id, 'SQUAD_PARTICIPANT', 3, sm.participate_at
FROM squad_member sm INNER JOIN squad s ON s.id = sm.squad_id
UNION ALL
SELECT s.crew_id, sc.member_id, 'SQUAD_COMMENT', 1, sc.created_at
FROM squad_comment sc INNER JOIN squad s ON s.id = sc.squad_id;
