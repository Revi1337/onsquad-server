package revi1337.onsquad.crew_member.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

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
}
