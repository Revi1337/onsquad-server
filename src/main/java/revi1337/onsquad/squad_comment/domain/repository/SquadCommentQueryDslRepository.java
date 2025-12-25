package revi1337.onsquad.squad_comment.domain.repository;

import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad_comment.domain.entity.QSquadComment.squadComment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;
import revi1337.onsquad.squad_comment.domain.result.QSquadCommentResult;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

@RequiredArgsConstructor
@Repository
public class SquadCommentQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<SquadCommentResult> fetchAllParentsBySquadId(Long squadId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QSquadCommentResult(
                        squadComment.id,
                        squadComment.content,
                        squadComment.deleted,
                        squadComment.createdAt,
                        squadComment.updatedAt,
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(squadComment)
                .innerJoin(squadComment.member, member).on(squadComment.squad.id.eq(squadId), squadComment.parent.isNull())
                .orderBy(squadComment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<SquadCommentResult> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QSquadCommentResult(
                        squadComment.id,
                        squadComment.content,
                        squadComment.deleted,
                        squadComment.createdAt,
                        squadComment.updatedAt,
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(squadComment)
                .innerJoin(squadComment.member, member).on(squadComment.squad.id.eq(squadId), squadComment.parent.id.eq(parentId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squadComment.createdAt.desc())
                .fetch();
    }
}
