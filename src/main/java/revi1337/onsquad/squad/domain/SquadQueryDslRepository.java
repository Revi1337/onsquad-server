package revi1337.onsquad.squad.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.QSimpleSquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.QSquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.jpa.JPAExpressions.select;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.category.domain.QCategory.*;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.QMember.member;
import static revi1337.onsquad.squad.domain.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.QSquadCategory.*;

@RequiredArgsConstructor
@Repository
public class SquadQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<SquadInfoDomainDto> findSquadById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .from(squad)
                        .innerJoin(squad.crewMember, crewMember).on(squad.id.eq(id))
                        .innerJoin(crewMember.member, member)
                        .innerJoin(squad.categories, squadCategory)
                        .innerJoin(squadCategory.category, category)
                        .transform(groupBy(squad.id)
                                .as(new QSquadInfoDomainDto(
                                        squad.id,
                                        squad.title,
                                        squad.content,
                                        squad.capacity,
                                        squad.address,
                                        squad.kakaoLink,
                                        squad.discordLink,
                                        list(category.categoryType),
                                        new QSimpleMemberInfoDomainDto(
                                                member.id,
                                                member.nickname
                                        )
                                ))
                        ).get(id)
        );
    }

    // TODO Join 이 한번 더 들어가지만, 처음부터 on 절로 필터링하는게 빠른지, 아니면 join 이 하나 덜 들어가지만 where 절로 필터링하는게 더 빠를까? Execution Plan 을 확인해 볼 필요가 있음.
    public Page<SquadInfoDomainDto> findSquadsByCrewName(Name crewName, CategoryType categoryType, Pageable pageable) {
        List<SquadInfoDomainDto> transformedResults = jpaQueryFactory
                .from(squad)
                .innerJoin(squad.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .leftJoin(squad.categories, squadCategory)
                .innerJoin(squadCategory.category, category)
                .where(
                        squad.crew.id.eq(findCrewIdByCrewName(crewName)),
                        categoryEq(categoryType)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squad.createdAt.desc())
                .transform(groupBy(squad.id).list(new QSquadInfoDomainDto(
                        squad.id,
                        squad.title,
                        squad.content,
                        squad.capacity,
                        squad.address,
                        squad.kakaoLink,
                        squad.discordLink,
                        list(category.categoryType),
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname
                        )
                )));

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squad.count())
                .from(squad)
                .where(squad.crew.id.eq(findCrewIdByCrewName(crewName)));

        return PageableExecutionUtils.getPage(transformedResults, pageable, countQuery::fetchOne);
    }

    public List<SimpleSquadInfoDomainDto> findSquadsInCrew(Long memberId, Long crewId) {
        return jpaQueryFactory
                .from(squad)
                .innerJoin(squad.crew, crew).on(squad.crew.id.eq(crewId))
                .innerJoin(squad.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .leftJoin(squad.categories, squadCategory)
                .innerJoin(squadCategory.category, category)
                .transform(groupBy(squad.id)
                        .list(new QSimpleSquadInfoDomainDto(
                                crew.id,
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.address,
                                squad.kakaoLink,
                                squad.discordLink,
                                new CaseBuilder()
                                        .when(member.id.eq(memberId))
                                        .then(TRUE)
                                        .otherwise(FALSE),
                                list(category.categoryType),
                                new QSimpleMemberInfoDomainDto(
                                        member.id,
                                        member.nickname
                                )
                        ))
                );
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
