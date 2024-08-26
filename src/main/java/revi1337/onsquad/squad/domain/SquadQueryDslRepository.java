package revi1337.onsquad.squad.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.squad.domain.vo.category.CategoryType;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.QMember.member;
import static revi1337.onsquad.squad.domain.QSquad.squad;
import static revi1337.onsquad.squad.domain.category.QCategory.*;
import static revi1337.onsquad.squad.domain.squad_category.QSquadCategory.*;

@RequiredArgsConstructor
@Repository
public class SquadQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Squad> findSquadsByCrewName(Name crewName, CategoryType categoryType, Pageable pageable) {
        List<Squad> pageableSquads = jpaQueryFactory
                .selectFrom(squad)
                .innerJoin(squad.crewMember, crewMember).fetchJoin()
                .innerJoin(crewMember.member, member).fetchJoin()
                .leftJoin(squad.categories, squadCategory)
                .innerJoin(squadCategory.category, category)
                .where(
                        squad.crew.id.eq(findCrewIdByCrewName(crewName)),
                        categoryEq(categoryType)
                )
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

    private BooleanExpression categoryEq(CategoryType categoryType) {
        if (categoryType == CategoryType.ALL) {
            return null;
        }

        return category.id.eq(categoryType.getPk());
    }

    private JPQLQuery<Long> findCrewIdByCrewName(Name crewName) {
        return select(crew.id)
                .from(crew)
                .where(crew.name.eq(crewName));
    }
}
