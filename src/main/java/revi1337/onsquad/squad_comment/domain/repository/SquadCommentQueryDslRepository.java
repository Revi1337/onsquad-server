package revi1337.onsquad.squad_comment.domain.repository;

import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad_comment.domain.entity.QSquadComment.squadComment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;
import revi1337.onsquad.squad_comment.domain.dto.QSquadCommentDomainDto;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@RequiredArgsConstructor
@Repository
public class SquadCommentQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 페이징처리에 맞게 부모 댓글들을 가져오고, id 별로 묶어서 반환한다.
     */
    public List<SquadCommentDomainDto> fetchAllParentsBySquadId(Long squadId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QSquadCommentDomainDto(
                        squadComment.id,
                        squadComment.content,
                        squadComment.deleted,
                        squadComment.createdAt,
                        squadComment.updatedAt,
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
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
                .fetch();
    }

    public List<SquadCommentDomainDto> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QSquadCommentDomainDto(
                        squadComment.id,
                        squadComment.content,
                        squadComment.deleted,
                        squadComment.createdAt,
                        squadComment.updatedAt,
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
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

    /**
     * 모든 댓글(부모, 자식)들을 모두 가져온다.
     */
    @Deprecated
    public List<SquadCommentDomainDto> findAllWithMemberBySquadId(Long squadId) {
        return jpaQueryFactory
                .select(new QSquadCommentDomainDto(
                        squadComment.parent.id,
                        squadComment.id,
                        squadComment.content,
                        squadComment.deleted,
                        squadComment.createdAt,
                        squadComment.updatedAt,
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
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
}
