package revi1337.onsquad.crew_member.domain;

import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrewMemberQueryRepositoryImpl implements CrewMemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsCrewMember(Long memberId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(crewMember)
                .where(crewMember.member.id.eq(memberId))
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public boolean existsParticipantCrewMember(Long memberId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(crewMember)
                .innerJoin(crewMember.member, member)
                .where(
                        member.id.eq(memberId)
                )
                .fetchFirst();

        return fetchOne != null;
    }
}
