package revi1337.onsquad.crew_member.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;
import revi1337.onsquad.crew_member.dto.QEnrolledCrewMemberDto;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.member.domain.QMember.*;

@RequiredArgsConstructor
public class CrewMemberQueryRepositoryImpl implements CrewMemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsCrewMember(Long memberId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(crewMember)
                .innerJoin(crewMember.member, member)
                .where(member.id.eq(memberId))
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public Optional<CrewMember> findCrewMemberByMemberId(Long memberId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(crewMember)
                        .where(crewMember.member.id.eq(memberId))
                        .fetchOne()
        );
    }

    @Override
    public List<EnrolledCrewMemberDto> findMembersForSpecifiedCrew(Name crewName, Long memberId) {
        return jpaQueryFactory
                .select(new QEnrolledCrewMemberDto(
                        crew.name,
                        member.nickname,
                        member.email,
                        crewMember.status,
                        crewMember.createdAt
                ))
                .from(crewMember)
                .innerJoin(crewMember.member, member)
                .innerJoin(crewMember.crew, crew)
                .where(
                        member.id.eq(memberId),
                        crew.name.eq(crewName)
                )
                .fetch();
    }

    @Override
    public Optional<CrewMember> findCrewMemberByCrewIdAndMemberId(Long memberId, Long crewId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(crewMember)
                        .where(
                                crewMember.crew.id.eq(crewId),
                                crewMember.member.id.eq(memberId)
                        )
                        .fetchOne()
        );
    }
}
