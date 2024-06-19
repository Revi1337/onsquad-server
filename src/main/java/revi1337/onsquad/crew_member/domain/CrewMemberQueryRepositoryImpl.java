package revi1337.onsquad.crew_member.domain;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;
import revi1337.onsquad.crew_member.dto.QEnrolledCrewMemberDto;

import java.util.List;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.member.domain.QMember.*;

@RequiredArgsConstructor
public class CrewMemberQueryRepositoryImpl implements CrewMemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CrewMember> findEnrolledCrewMembers(Long memberId) {
        return jpaQueryFactory
                .selectFrom(crewMember)
                .innerJoin(crewMember.member, member).fetchJoin()
                .innerJoin(crewMember.crew, crew).fetchJoin()
                .where(member.id.eq(memberId))
                .where(member.id.eq(memberId))
                .fetch();
    }

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
}
