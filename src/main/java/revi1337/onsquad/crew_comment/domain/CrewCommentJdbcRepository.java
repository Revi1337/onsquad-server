package revi1337.onsquad.crew_comment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CrewCommentJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<CrewComment> findLimitedChildCommentsByParentIdIn(List<Long> parentIds, Integer childrenSize) {
        String sql = "SELECT * FROM (" +
                "    SELECT " +
                "        crew_comment.parent_id, " +
                "        crew_comment.id, " +
                "        crew_comment.content, " +
                "        crew_comment.created_at, " +
                "        crew_comment.updated_at, " +
                "        crew_comment.crew_member_id, " +
                "        member.id AS comment_creator_id, " +
                "        member.nickname AS comment_creator_nickname, " +
                "        member.email AS comment_creator_email, " +
                "        ROW_NUMBER() OVER (PARTITION BY crew_comment.parent_id ORDER BY crew_comment.created_at DESC) AS rn " +
                "    FROM crew_comment " +
                "    INNER JOIN crew_member ON crew_comment.crew_member_id = crew_member.id " +
                "    INNER JOIN member ON crew_member.member_id = member.id " +
                "    WHERE crew_comment.parent_id IN (:parentIds) " +
                ") AS subquery " +
                " WHERE subquery.rn <= (:childLimit)" +
                " ORDER BY subquery.rn ASC";

        if (parentIds.isEmpty()) {
            return new ArrayList<>();
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("parentIds", parentIds)
                .addValue("childLimit", childrenSize);

        return jdbcTemplate.query(sql, sqlParameterSource, crewCommentRowMapper());
    }

    private RowMapper<CrewComment> crewCommentRowMapper() {
        return (rs, rowNum) -> CrewComment.builder()
                .parent(CrewComment.builder()
                        .id(rs.getLong("parent_id"))
                        .build())
                .id(rs.getLong("id"))
                .content(rs.getString("content"))
                .crewMember(CrewMember.builder()
                        .id(rs.getLong("crew_member_id"))
                        .member(Member.builder()
                                .id(rs.getLong("comment_creator_id"))
                                .nickname(new Nickname(rs.getString("comment_creator_nickname")))
                                .email(new Email(rs.getString("comment_creator_email")))
                                .build())
                        .build())
                .build();
    }
}
