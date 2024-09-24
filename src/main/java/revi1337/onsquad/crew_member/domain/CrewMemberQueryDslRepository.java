package revi1337.onsquad.crew_member.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.QCrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.QEnrolledCrewDomainDto;
import revi1337.onsquad.member.domain.QMember;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;

import java.util.List;

import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.image.domain.QImage.image;
import static revi1337.onsquad.member.domain.QMember.member;

@RequiredArgsConstructor
@Repository
public class CrewMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QMember CREW_CREATOR = new QMember("crewCreator");

    public List<EnrolledCrewDomainDto> findOwnedCrews(Long memberId) {
        BooleanExpression isCrewOwner = new CaseBuilder()
                .when(crew.member.id.eq(memberId))
                .then(true)
                .otherwise(false);

        return jpaQueryFactory
                .select(new QEnrolledCrewDomainDto(
                        crew.id,
                        crew.name,
                        image.imageUrl,
                        isCrewOwner,
                        new QSimpleMemberInfoDomainDto(
                                CREW_CREATOR.id,
                                CREW_CREATOR.nickname
                        )
                ))
                .from(crewMember)
                .innerJoin(crewMember.member, member).on(member.id.eq(memberId))
                .innerJoin(crewMember.crew, crew)
                .innerJoin(crew.image, image)
                .innerJoin(crew.member, CREW_CREATOR)
                .orderBy(
                        crewMember.requestAt.desc(),
                        isCrewOwner.desc()
                )
                .fetch();
    }

    public List<CrewMemberDomainDto> findCrewMembersByCrewId(Long crewId) {
        return jpaQueryFactory
                .select(new QCrewMemberDomainDto(
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname
                        ),
                        crewMember.requestAt
                ))
                .from(crewMember)
                .innerJoin(crewMember.member, member).on(crewMember.crew.id.eq(crewId))
                .orderBy(crewMember.requestAt.desc())
                .fetch();
    }
}
