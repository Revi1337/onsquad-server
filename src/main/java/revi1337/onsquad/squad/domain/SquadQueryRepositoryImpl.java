package revi1337.onsquad.squad.domain;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.squad.domain.vo.Title;

import java.util.List;
import java.util.Optional;

import static com.querydsl.jpa.JPAExpressions.select;
import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.member.domain.QMember.*;
import static revi1337.onsquad.squad.domain.QSquad.*;
import static revi1337.onsquad.squad_member.domain.QSquadMember.*;

@RequiredArgsConstructor
public class SquadQueryRepositoryImpl implements SquadQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Squad> findSquadWithMembersById(Long squadId, Title squadTitle) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squad)
                        .leftJoin(squad.squadMembers).fetchJoin()
                        .where(
                                squad.id.eq(squadId),
                                squad.title.eq(squadTitle)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<Squad> findSquadByIdAndTitleWithMember(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squad)
                        .innerJoin(squad.crewMember, crewMember).fetchJoin()
                        .innerJoin(crewMember.member, member).fetchJoin()
                        .where(squad.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Page<Squad> findSquadsByCrewName(Name crewName, Pageable pageable) {
        List<Squad> pageableSquads = jpaQueryFactory
                .selectFrom(squad)
                .innerJoin(squad.crewMember, crewMember).fetchJoin()
                .innerJoin(crewMember.member, member).fetchJoin()
                .where(squad.crew.id.eq(findCrewIdByCrewName(crewName)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squad.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squad.count())
                .from(squad)
                .where(squad.crew.id.eq(findCrewIdByCrewName(crewName)));

        return PageableExecutionUtils.getPage(pageableSquads, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<Squad> findSquadWithCrewById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squad)
                        .innerJoin(squad.crew, crew)
                        .where(squad.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Squad> findSquadByIdWithCrewAndCrewMembers(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squad)
                        .innerJoin(squad.crew, crew).fetchJoin()
                        .innerJoin(squad.squadMembers, squadMember).fetchJoin()
                        .innerJoin(squad.crewMember, crewMember).fetchJoin()
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

    private JPQLQuery<Long> findCrewIdByCrewName(Name crewName) {
        return select(crew.id)
                .from(crew)
                .where(crew.name.eq(crewName));
    }
}
