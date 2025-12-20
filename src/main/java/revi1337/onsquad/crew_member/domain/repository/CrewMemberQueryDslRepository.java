package revi1337.onsquad.crew_member.domain.repository;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.result.CrewMemberResult;
import revi1337.onsquad.crew_member.domain.result.JoinedCrewResult;
import revi1337.onsquad.crew_member.domain.result.QCrewMemberResult;
import revi1337.onsquad.crew_member.domain.result.QJoinedCrewResult;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<CrewMemberResult> fetchParticipantsByCrewId(Long crewId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QCrewMemberResult(
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        ),
                        crewMember.requestAt
                ))
                .from(crewMember)
                .innerJoin(crewMember.member, member)
                .where(crewMember.crew.id.eq(crewId))
                .orderBy(crewMember.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<JoinedCrewResult> fetchJoinedCrewsByMemberId(Long memberId) {
        return jpaQueryFactory
                .select(new QJoinedCrewResult(
                        crew.id,
                        crew.name,
                        crew.imageUrl,
                        new CaseBuilder()
                                .when(member.id.eq(memberId))
                                .then(true)
                                .otherwise(false),
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(crewMember)
                .innerJoin(crewMember.crew, crew)
                .innerJoin(crew.member, member)
                .where(crewMember.member.id.eq(memberId))
                .orderBy(crewMember.requestAt.desc())
                .fetch();
    }

    /**
     * @see #fetchParticipantsByCrewId(Long, Pageable)
     * @deprecated
     */
    @Deprecated
    public Page<CrewMemberResult> fetchParticipantsByCrewIdLegacy(Long crewId, Pageable pageable) {
        List<CrewMemberResult> results = jpaQueryFactory
                .select(new QCrewMemberResult(
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        ),
                        crewMember.requestAt
                ))
                .from(crewMember)
                .innerJoin(crewMember.member, member)
                .where(crewMember.crew.id.eq(crewId))
                .orderBy(crewMember.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crewMember.id.count())
                .from(crewMember)
                .where(crewMember.crew.id.eq(crewId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    /**
     * @see #fetchJoinedCrewsByMemberId(Long)
     * @deprecated
     */
    @Deprecated
    public List<JoinedCrewResult> fetchJoinedCrewsByMemberIdLegacy(Long memberId) {
        BooleanExpression isCrewOwner = new CaseBuilder()
                .when(member.id.eq(memberId))
                .then(true)
                .otherwise(false);

        return jpaQueryFactory
                .select(new QJoinedCrewResult(
                        crew.id,
                        crew.name,
                        crew.imageUrl,
                        isCrewOwner,
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(crewMember)
                .innerJoin(crewMember.crew, crew)
                .innerJoin(crew.member, member)
                .where(crewMember.member.id.eq(memberId))
                .orderBy(crewMember.requestAt.desc(), isCrewOwner.desc())
                .fetch();
    }
}
