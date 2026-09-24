-- =====================================================================
-- 크루 리더보드 SQL 배치 벤치마크용 시드 데이터
-- 규모: 총 10,000건 (crew_member/squad/squad_member/squad_comment 각 2,500건)
--
-- leaderboard-workflow.md "1. 초기 (Baseline 재현)" 단계의 실측 기준선용.
--
-- 실행 순서 주의사항 (순서가 꼬이면 FK/유니크 제약 위반으로 실패함):
--   1) member  N명 생성
--   2) crew    N개 생성 (owner = 위에서 만든 member 중 하나)
--   3) crew_member  (crew_id, member_id) 유니크 제약 -> crew x member 조합으로 유니크 보장
--   4) squad   (제약 없음, crew/member 아무거나 참조)
--   5) squad_member (squad_id, member_id) 유니크 제약 -> squad_id 자체가 매 행 다르므로 자동 유니크
--   6) squad_comment (제약 없음)
-- 이 순서를 바꾸면 FK 위반 또는 유니크 제약 위반이 발생함.
--
-- 이 파일은 db/mysql/*-mysql.sql 글롭에 안 걸리도록 benchmark/ 서브디렉토리에 있음
-- (스프링부트 data-init이 앱 기동마다 자동 실행하지 않도록 의도적으로 분리).
-- 실행: mysql -u <user> -p <database> < seed_leaderboard_bench_10k.sql
--
-- 재실행 시 기존 벤치마크 데이터에 누적되므로, 깨끗한 측정을 원하면
-- 실행 전 crew_member/squad/squad_member/squad_comment/crew/member 중
-- 'bench_'/'벤치' 프리픽스가 붙은 행만 별도로 정리하거나, 별도 스키마에서 실행할 것.
-- =====================================================================

SET SESSION cte_max_recursion_depth = 3000;

-- 1) 회원 100명 생성
INSERT INTO member (email, address, address_detail, nickname, password, introduce, kakao_link, profile_image, user_type, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 100
)
SELECT
    CONCAT('bench10k_member_', n, '@onsquad.test'),
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

SET @member_count = 100;
SET @member_start_id = (SELECT MAX(id) FROM member) - @member_count + 1;

-- 2) 크루 100개 생성
INSERT INTO crew (name, introduce, detail, current_size, kakao_link, member_id, created_at, updated_at, version)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 100
)
SELECT
    CONCAT('벤치크루10k_', n),
    CONCAT('벤치크루 소개 ', n),
    CONCAT('벤치크루 디테일 ', n),
    1,
    'https://카카오링크.com',
    @member_start_id + ((n - 1) % @member_count),
    NOW() - INTERVAL (n % 30) DAY,
    NOW() - INTERVAL (n % 30) DAY,
    0
FROM seq;

SET @crew_count = 100;
SET @crew_start_id = (SELECT MAX(id) FROM crew) - @crew_count + 1;

-- 3) crew_member 2,500건 (crew_id, member_id) 유니크 제약 -> 100 x 100 = 10,000 조합 중 2,500개만 사용
INSERT INTO crew_member (crew_id, member_id, role, participate_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 2500
)
SELECT
    @crew_start_id + ((n - 1) % @crew_count),
    @member_start_id + (FLOOR((n - 1) / @crew_count) % @member_count),
    'GENERAL',
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND
FROM seq;

-- 4) squad 2,500건 (제약 없음)
INSERT INTO squad (title, content, capacity, current_size, remain, kakao_link, discord_link, created_at, updated_at, member_id, crew_id)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 2500
)
SELECT
    CONCAT('벤치스쿼드타이틀10k_', n),
    CONCAT('벤치스쿼드본문10k_', n),
    8, 1, 7,
    'https://카카오링크.com', 'https://디스코드.com',
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND,
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND,
    @member_start_id + ((n - 1) % @member_count),
    @crew_start_id + ((n - 1) % @crew_count)
FROM seq;

SET @squad_count = 2500;
SET @squad_start_id = (SELECT MAX(id) FROM squad) - @squad_count + 1;

-- 5) squad_member 2,500건 (squad_id, member_id) 유니크 제약 -> squad_id가 매 행 다르므로 자동으로 유니크
INSERT INTO squad_member (squad_id, member_id, role, participate_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 2500
)
SELECT
    @squad_start_id + (n - 1),
    @member_start_id + ((n - 1) % @member_count),
    'GENERAL',
    NOW() - INTERVAL (n % 30) DAY - INTERVAL (n % 86400) SECOND
FROM seq;

-- 6) squad_comment 2,500건 (제약 없음)
INSERT INTO squad_comment (content, deleted, squad_id, member_id, parent_id, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 2500
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
