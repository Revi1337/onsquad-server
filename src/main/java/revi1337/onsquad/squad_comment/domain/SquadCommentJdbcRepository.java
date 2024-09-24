package revi1337.onsquad.squad_comment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class SquadCommentJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<SquadCommentDomainDto> findLimitedChildCommentsByParentIdIn(Collection<Long> parentIds, Integer childrenSize) {
        String sql = "SELECT * FROM (" +
                "    SELECT " +
                "        squad_comment.parent_id, " +
                "        squad_comment.id, " +
                "        squad_comment.content, " +
                "        squad_comment.created_at, " +
                "        squad_comment.updated_at, " +
                "        squad_comment.crew_member_id, " +
                "        member.id AS comment_creator_id, " +
                "        member.nickname AS comment_creator_nickname, " +
                "        member.mbti AS comment_creator_mbti, " +
                "        ROW_NUMBER() OVER (PARTITION BY squad_comment.parent_id ORDER BY squad_comment.created_at DESC) AS rn " +
                "    FROM squad_comment " +
                "    INNER JOIN crew_member ON squad_comment.crew_member_id = crew_member.id " +
                "    INNER JOIN member ON crew_member.member_id = member.id " +
                "    WHERE squad_comment.parent_id IN (:parentIds) " +
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

    private RowMapper<SquadCommentDomainDto> crewCommentRowMapper() {
        return (rs, rowNum) -> new SquadCommentDomainDto(
                rs.getLong("parent_id"),
                rs.getLong("id"),
                rs.getString("content"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class),
                new SimpleMemberInfoDomainDto(
                        rs.getLong("comment_creator_id"),
                        new Nickname(rs.getString("comment_creator_nickname")),
                        rs.getObject("comment_creator_mbti", Mbti.class)
                )
        );
    }
}
