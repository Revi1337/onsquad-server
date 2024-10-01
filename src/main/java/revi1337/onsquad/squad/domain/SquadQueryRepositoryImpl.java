package revi1337.onsquad.squad.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.squad.domain.QSquad.*;
import static revi1337.onsquad.squad_member.domain.QSquadMember.*;

@RequiredArgsConstructor
public class SquadQueryRepositoryImpl implements SquadQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Squad> findSquadByIdWithCrew(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squad)
                        .innerJoin(squad.crew, crew).fetchJoin()
                        .where(squad.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Squad> findSquadByIdWithSquadMembers(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squad)
                        .innerJoin(squad.squadMembers, squadMember).fetchJoin()
                        .where(squad.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Squad> findByIdWithOwnerAndCrewAndSquadMembers(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squad)
                        .innerJoin(squad.crew, crew).fetchJoin()
                        .innerJoin(squad.crewMember, crewMember).fetchJoin()
                        .innerJoin(squad.squadMembers, squadMember).fetchJoin()
                        .where(squad.id.eq(id))
                        .fetchOne()
        );
    }
}
