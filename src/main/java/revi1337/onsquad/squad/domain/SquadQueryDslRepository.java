package revi1337.onsquad.squad.domain;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.QMember.member;
import static revi1337.onsquad.squad.domain.QSquad.squad;

@RequiredArgsConstructor
@Repository
public class SquadQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Squad> findSquadsByCrewName(Name crewName, Pageable pageable) {
        List<Squad> pageableSquads = jpaQueryFactory
                .selectFrom(squad)
                .innerJoin(squad.crewMember, crewMember).fetchJoin()
                .innerJoin(crewMember.member, member).fetchJoin()
                .leftJoin(squad.categories)
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

    private JPQLQuery<Long> findCrewIdByCrewName(Name crewName) {
        return select(crew.id)
                .from(crew)
                .where(crew.name.eq(crewName));
    }
}
