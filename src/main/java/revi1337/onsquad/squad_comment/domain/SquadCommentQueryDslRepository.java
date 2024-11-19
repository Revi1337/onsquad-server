package revi1337.onsquad.squad_comment.domain;

import static com.querydsl.core.group.GroupBy.groupBy;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.QMember.member;
import static revi1337.onsquad.squad_comment.domain.QSquadComment.squadComment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;
import revi1337.onsquad.squad_comment.domain.dto.QSquadCommentDomainDto;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@RequiredArgsConstructor
@Repository
public class SquadCommentQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 모든 댓글(부모, 자식)들을 모두 가져온다.
     *
     * @return
     */
    public List<SquadCommentDomainDto> findCommentsWithMemberByCrewId(Long squadId) {
        return jpaQueryFactory
                .select(new QSquadCommentDomainDto(
                        squadComment.parent.id,
                        squadComment.id,
                        squadComment.content,
                        squadComment.createdAt,
                        squadComment.updatedAt,
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname,
                                member.mbti
                        )
                ))
                .from(squadComment)
                .innerJoin(squadComment.crewMember, crewMember).on(squadComment.squad.id.eq(squadId))
                .innerJoin(crewMember.member, member)
                .leftJoin(squadComment.parent)
                .orderBy(
                        squadComment.parent.id.asc().nullsFirst(),
                        squadComment.createdAt.desc()
                )
                .fetch();
    }

    /**
     * 페이징처리에 맞게 부모 댓글들을 가져오고, id 별로 묶어서 반환한다.
     *
     * @param pageable
     * @return
     */
    public Map<Long, SquadCommentDomainDto> findLimitedParentCommentsByCrewId(Long squadId, Pageable pageable) {
        return jpaQueryFactory
                .from(squadComment)
                .innerJoin(squadComment.crewMember, crewMember)
                .on(
                        squadComment.squad.id.eq(squadId),
                        squadComment.parent.isNull()
                )
                .innerJoin(crewMember.member, member)
                .orderBy(squadComment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(groupBy(squadComment.id)
                        .as(new QSquadCommentDomainDto(
                                squadComment.id,
                                squadComment.content,
                                squadComment.createdAt,
                                squadComment.updatedAt,
                                new QSimpleMemberInfoDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.mbti
                                )
                        )));
    }

    public List<SquadCommentDomainDto> findChildComments(Long squadId, Long parentId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QSquadCommentDomainDto(
                        squadComment.id,
                        squadComment.content,
                        squadComment.createdAt,
                        squadComment.updatedAt,
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname,
                                member.mbti
                        )
                ))
                .from(squadComment)
                .innerJoin(squadComment.crewMember, crewMember)
                .on(
                        squadComment.squad.id.eq(squadId),
                        squadComment.parent.id.eq(parentId)
                )
                .innerJoin(crewMember.member, member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squadComment.createdAt.desc())
                .fetch();
    }
}
