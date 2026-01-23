package revi1337.onsquad.crew_member.domain.repository;

import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@Deprecated
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
}
