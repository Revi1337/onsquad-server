package revi1337.onsquad.squad_participant.domain.repository;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_participant.domain.entity.QSquadParticipant.squadParticipant;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.squad_participant.domain.entity.SquadParticipant;

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
