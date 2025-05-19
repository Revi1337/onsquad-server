package revi1337.onsquad.crew_member.domain;

import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.QMember.member;

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
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew_member.domain.dto.QCrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.QEnrolledCrewDomainDto;
import revi1337.onsquad.member.domain.QMember;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QMember CREW_CREATOR = new QMember("crewCreator");

    public List<EnrolledCrewDomainDto> fetchEnrolledCrewsByMemberId(Long memberId) {
        return jpaQueryFactory
                .select(new QEnrolledCrewDomainDto(
                        crew.id,
                        crew.name,
                        crew.imageUrl,
                        new CaseBuilder()
                                .when(CREW_CREATOR.id.eq(memberId))
                                .then(true)
                                .otherwise(false),
                        new QSimpleMemberDomainDto(
                                CREW_CREATOR.id,
                                CREW_CREATOR.nickname,
                                CREW_CREATOR.mbti
                        )
                ))
                .from(crewMember)
                .innerJoin(crewMember.crew, crew).on(crewMember.member.id.eq(memberId))
                .innerJoin(crew.member, CREW_CREATOR)
                .orderBy(crewMember.requestAt.desc())
                .fetch();
    }

    public Page<CrewMemberDomainDto> fetchAllByCrewId(Long crewId, Pageable pageable) {
        List<CrewMemberDomainDto> results = jpaQueryFactory
                .select(new QCrewMemberDomainDto(
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
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

    @Deprecated
    public List<EnrolledCrewDomainDto> fetchAllWithCrewsByMemberIdV2(Long memberId) {
        BooleanExpression isCrewOwner = new CaseBuilder()
                .when(CREW_CREATOR.id.eq(memberId))
                .then(true)
                .otherwise(false);

        return jpaQueryFactory
                .select(new QEnrolledCrewDomainDto(
                        crew.id,
                        crew.name,
                        crew.imageUrl,
                        isCrewOwner,
                        new QSimpleMemberDomainDto(
                                CREW_CREATOR.id,
                                CREW_CREATOR.nickname,
                                CREW_CREATOR.mbti
                        )
                ))
                .from(crewMember)
                .innerJoin(crewMember.crew, crew).on(crewMember.member.id.eq(memberId))
                .innerJoin(crew.member, CREW_CREATOR)
                .orderBy(
                        crewMember.requestAt.desc(),
                        isCrewOwner.desc()
                )
                .fetch();
    }
}
