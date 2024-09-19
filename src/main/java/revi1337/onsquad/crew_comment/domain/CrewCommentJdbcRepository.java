package revi1337.onsquad.crew_comment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_comment.domain.dto.CrewCommentDomainDto;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.dto.SimpleMemberInfoDomainDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CrewCommentJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<CrewCommentDomainDto> findLimitedChildCommentsByParentIdIn(Collection<Long> parentIds, Integer childrenSize) {
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

    private RowMapper<CrewCommentDomainDto> crewCommentRowMapper() {
        return (rs, rowNum) -> new CrewCommentDomainDto(
                rs.getLong("parent_id"),
                rs.getLong("id"),
                rs.getString("content"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class),
                new SimpleMemberInfoDomainDto(
                        rs.getLong("comment_creator_id"),
                        new Nickname(rs.getString("comment_creator_nickname"))
                )
        );
    }
}
