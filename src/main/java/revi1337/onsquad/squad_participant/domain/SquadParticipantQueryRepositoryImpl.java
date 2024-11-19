package revi1337.onsquad.squad_participant.domain;

import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.squad.domain.QSquad.squad;
import static revi1337.onsquad.squad_participant.domain.QSquadParticipant.squadParticipant;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SquadParticipantQueryRepositoryImpl implements SquadParticipantQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<SquadParticipant> findByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(squadParticipant)
                        .from(squadParticipant)
                        .innerJoin(squadParticipant.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                        .innerJoin(squadParticipant.squad, squad).on(squad.id.eq(squadId))
                        .innerJoin(squad.crew, crew).on(crew.id.eq(crewId))
                        .fetchOne()
        );
    }
}
