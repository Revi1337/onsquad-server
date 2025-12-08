package revi1337.onsquad.squad_request.domain.repository;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_request.domain.entity.QSquadRequest.squadRequest;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

@RequiredArgsConstructor
public class SquadRequestQueryRepositoryImpl implements SquadRequestQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<SquadRequest> findByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squadRequest)
                        .innerJoin(squadRequest.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                        .innerJoin(squadRequest.squad, squad).on(squad.id.eq(squadId))
                        .innerJoin(squad.crew, crew).on(crew.id.eq(crewId))
                        .fetchOne()
        );
    }
}
