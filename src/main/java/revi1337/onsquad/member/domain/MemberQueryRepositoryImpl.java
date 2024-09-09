package revi1337.onsquad.member.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.member.domain.QMember.*;

@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Member> findMemberWithRefCrewAndRefCrewMembersById(Long memberId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(member)
                        .leftJoin(member.crews, crew)
                        .innerJoin(crew.crewMembers, crewMember)
                        .where(member.id.eq(memberId))
                        .fetchOne()
        );
    }
}
