SELECT
    crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter, ranks
FROM (
         SELECT
             crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter,
             DENSE_RANK() OVER(PARTITION BY crew_id ORDER BY counter, mem_join_time) AS ranks
         FROM (
                  SELECT DISTINCT
                      crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter
                  FROM (
                           SELECT
                               crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time,
                               COUNT(*) OVER (PARTITION BY crew_id, mem_id) AS counter
                           FROM (
                                    SELECT
                                        crew_member.crew_id AS crew_id,
                                        member.id AS mem_id,
                                        member.nickname AS mem_nickname,
                                        member.mbti AS mem_mbti,
                                        crew_member.participate_at AS mem_join_time
                                    FROM squad_comment
                                             INNER JOIN crew_member ON squad_comment.crew_member_id = crew_member.id AND squad_comment.created_at BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP - 1
                                             INNER JOIN member ON crew_member.member_id = member.id

                                    UNION ALL
                                    SELECT
                                        crew_member.crew_id,
                                        member.id,
                                        member.nickname,
                                        member.mbti,
                                        crew_member.participate_at
                                    FROM squad
                                             INNER JOIN crew_member ON squad.crew_member_id = crew_member.id
                                             INNER JOIN member ON crew_member.member_id = member.id
                                ) AS union_table
                       ) AS count_table
              ) AS distinct_table
     ) AS rank_table
WHERE ranks <= 4
ORDER BY crew_id, ranks




SELECT crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter, ranks
FROM (
         SELECT crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter,
                DENSE_RANK() OVER(ORDER BY counter, mem_join_time) AS ranks
         FROM (
                  SELECT crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, COUNT(mem_id) AS counter
                  FROM (
                           SELECT
                               crew_member.crew_id AS crew_id,
                               member.id AS mem_id,
                               member.nickname AS mem_nickname,
                               member.mbti AS mem_mbti,
                               crew_member.participate_at AS mem_join_time
                           FROM squad_comment
                                    INNER JOIN crew_member ON squad_comment.crew_member_id = crew_member.id
                                    INNER JOIN member ON crew_member.member_id = member.id

                           UNION ALL
                           SELECT
                               crew_member.crew_id,
                               member.id,
                               member.nickname,
                               member.mbti,
                               crew_member.participate_at
                           FROM squad
                                    INNER JOIN crew_member ON squad.crew_member_id = crew_member.id
                                    INNER JOIN member ON crew_member.member_id = member.id
                       ) AS union_table
                  GROUP BY crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time
              ) AS count_table
         ORDER BY ranks
     ) AS result
WHERE ranks <= 4;



SELECT
    crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter, ranks
FROM (
         SELECT
             crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter,
             DENSE_RANK() OVER(PARTITION BY crew_id ORDER BY counter, mem_join_time) AS ranks
         FROM (
                  SELECT DISTINCT
                      crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time, counter
                  FROM (
                           SELECT
                               crew_id, mem_id, mem_nickname, mem_mbti, mem_join_time,
                               COUNT(*) OVER (PARTITION BY crew_id, mem_id) AS counter
                           FROM (
                                    SELECT
                                        crew_member.crew_id AS crew_id,
                                        member.id AS mem_id,
                                        member.nickname AS mem_nickname,
                                        member.mbti AS mem_mbti,
                                        crew_member.participate_at AS mem_join_time
                                    FROM squad_comment
                                             INNER JOIN crew_member ON squad_comment.crew_member_id = crew_member.id
                                             INNER JOIN member ON crew_member.member_id = member.id

                                    UNION ALL
                                    SELECT
                                        crew_member.crew_id,
                                        member.id,
                                        member.nickname,
                                        member.mbti,
                                        crew_member.participate_at
                                    FROM squad
                                             INNER JOIN crew_member ON squad.crew_member_id = crew_member.id
                                             INNER JOIN member ON crew_member.member_id = member.id
                                ) AS union_table
                       ) AS count_table
              ) AS distinct_table
     ) AS rank_table
WHERE ranks <= 4
ORDER BY crew_id, ranks


