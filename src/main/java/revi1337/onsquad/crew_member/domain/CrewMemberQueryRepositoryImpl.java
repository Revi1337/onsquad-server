package revi1337.onsquad.crew_member.domain;

import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew.domain.vo.Name;

@RequiredArgsConstructor
public class CrewMemberQueryRepositoryImpl implements CrewMemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByMemberIdAndCrewName(Long memberId, Name name) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(crewMember)
                .innerJoin(crewMember.crew, crew).on(crew.name.eq(name))
                .where(crewMember.member.id.eq(memberId))
                .fetchFirst();

        return fetchOne != null;
    }

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
