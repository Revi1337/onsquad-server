package revi1337.onsquad.squad_comment.domain.repository;

import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad_comment.domain.entity.QSquadComment.squadComment;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

@RequiredArgsConstructor
@Repository
public class SquadCommentQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<SquadComment> fetchAllParentsBySquadId(Long squadId, Pageable pageable) {
        List<SquadComment> comments = jpaQueryFactory
                .selectFrom(squadComment)
                .innerJoin(squadComment.member, member).fetchJoin()
                .where(squadComment.squad.id.eq(squadId), squadComment.parent.isNull())
                .orderBy(squadComment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squadComment.id.count())
                .from(squadComment)
                .where(squadComment.squad.id.eq(squadId), squadComment.parent.isNull());

        return PageableExecutionUtils.getPage(comments, pageable, countQuery::fetchOne);
    }

    public Page<SquadComment> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable) {
        List<SquadComment> replies = jpaQueryFactory
                .selectFrom(squadComment)
                .innerJoin(squadComment.member, member).fetchJoin()
                .where(squadComment.squad.id.eq(squadId), squadComment.parent.id.eq(parentId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squadComment.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squadComment.id.count())
                .from(squadComment)
                .where(squadComment.squad.id.eq(squadId), squadComment.parent.id.eq(parentId));

        return PageableExecutionUtils.getPage(replies, pageable, countQuery::fetchOne);
    }
}
