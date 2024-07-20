package revi1337.onsquad.squad.domain;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.OrPointcut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.squad.domain.vo.Title;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.member.domain.QMember.*;
import static revi1337.onsquad.squad.domain.QSquad.*;

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
    public Optional<Squad> findSquadWithMemberByIdAndTitle(Long id, Title title) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squad)
                        .innerJoin(squad.member, member).fetchJoin()
                        .where(
                                squad.id.eq(id),
                                squad.title.eq(title)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Page<Squad> findSquadsByCrewName(Name crewName, Pageable pageable) {
        List<Squad> pageableSquads = jpaQueryFactory
                .selectFrom(squad)
                .innerJoin(squad.member, member).fetchJoin()
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

    private JPQLQuery<Long> findCrewIdByCrewName(Name crewName) {
        return JPAExpressions
                .select(crew.id)
                .from(crew)
                .where(crew.name.eq(crewName));
    }
}
