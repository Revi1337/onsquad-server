package revi1337.onsquad.squad.domain;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.category.domain.QCategory.category;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.QMember.member;
import static revi1337.onsquad.squad.domain.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.QSquadCategory.squadCategory;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;
import revi1337.onsquad.squad.domain.dto.QSimpleSquadDomainDto;
import revi1337.onsquad.squad.domain.dto.QSquadDomainDto;
import revi1337.onsquad.squad.domain.dto.QSquadWithLeaderStateDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadWithLeaderStateDomainDto;

@RequiredArgsConstructor
@Repository
public class SquadQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * Optimized Query Base {@link #fetchByIdV2(Long)}
     *
     * @param id
     */
    public Optional<SquadDomainDto> fetchById(Long id) {
        return Optional.ofNullable(jpaQueryFactory
                .from(squad)
                .innerJoin(squad.crewMember, crewMember).on(squad.id.eq(id))
                .innerJoin(crewMember.member, member)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .transform(groupBy(squad.id)
                        .as(new QSquadDomainDto(
                                squad.id,
                                squad.title,
                                squad.content,
                                squad.capacity,
                                squad.remain,
                                squad.address,
                                squad.kakaoLink,
                                squad.discordLink,
                                list(category.categoryType),
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.mbti
                                )
                        ))
                ).get(id)
        );
    }

    /**
     * Optimized Query Base {@link #fetchAllByCrewIdV2(Long, CategoryType, Pageable)}
     *
     * @param crewId
     * @param categoryType
     * @param pageable
     */
    public Page<SquadDomainDto> fetchAllByCrewId(Long crewId, CategoryType categoryType, Pageable pageable) {
        List<Long> squadIds = jpaQueryFactory
                .select(squad.id)
                .from(squad)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .where(
                        squad.crew.id.eq(crewId),
                        categoryEq(categoryType)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squad.createdAt.desc())
                .fetch();

        Map<Long, SquadDomainDto> groupResults = jpaQueryFactory
                .from(squad)
                .innerJoin(squad.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .where(squad.id.in(squadIds))
                .transform(groupBy(squad.id)
                        .as(new QSquadDomainDto(
                                squad.id,
                                squad.title,
                                squad.content,
                                squad.capacity,
                                squad.remain,
                                squad.address,
                                squad.kakaoLink,
                                squad.discordLink,
                                list(category.categoryType),
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.mbti
                                )
                        ))
                );

        List<SquadDomainDto> results = squadIds.stream()
                .map(groupResults::get)
                .toList();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squad.id.count())
                .from(squad)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .where(
                        squad.crew.id.eq(crewId),
                        categoryEq(categoryType)
                );

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    public Page<SquadWithLeaderStateDomainDto> fetchAllWithOwnerState(Long memberId,
                                                                      Long crewId,
                                                                      Pageable pageable) {
        List<SquadWithLeaderStateDomainDto> results = jpaQueryFactory
                .select(new QSquadWithLeaderStateDomainDto(
                        new CaseBuilder()
                                .when(member.id.eq(memberId))
                                .then(TRUE)
                                .otherwise(FALSE),
                        new QSimpleSquadDomainDto(
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.mbti
                                )
                        )))
                .from(squad)
                .innerJoin(squad.crewMember, crewMember).on(squad.crew.id.eq(crewId))
                .innerJoin(crewMember.member, member)
                .orderBy(squad.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squad.id.count())
                .from(squad)
                .where(squad.crew.id.eq(crewId));

        Set<Long> availableCategories = results.stream()
                .map(domainDto -> domainDto.squad().id())
                .collect(Collectors.toSet());

        Map<Long, List<CategoryType>> categories = jpaQueryFactory
                .select(squadCategory.squad.id, category.categoryType)
                .from(squadCategory)
                .innerJoin(squadCategory.category, category)
                .where(squadCategory.squad.id.in(availableCategories))
                .transform(groupBy(squadCategory.squad.id)
                        .as(list(category.categoryType)));

        results.stream()
                .map(SquadWithLeaderStateDomainDto::squad)
                .forEach(squad -> {
                    if (categories.get(squad.id()) != null) {
                        squad.addCategories(categories.get(squad.id()));
                    }
                });

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    private BooleanExpression categoryEq(CategoryType categoryType) {
        if (categoryType == CategoryType.ALL) {
            return null;
        }

        return category.id.eq(categoryType.getPk());
    }

    /**
     * @param id
     * @deprecated Use {@link #fetchById(Long)} instead.
     */
    @Deprecated
    public Optional<SquadDomainDto> fetchByIdV2(Long id) {
        return Optional.ofNullable(jpaQueryFactory
                .select(new QSquadDomainDto(
                        squad.id,
                        squad.title,
                        squad.content,
                        squad.capacity,
                        squad.remain,
                        squad.address,
                        squad.kakaoLink,
                        squad.discordLink,
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.mbti
                        )
                ))
                .from(squad)
                .innerJoin(squad.crewMember, crewMember).on(squad.id.eq(id))
                .innerJoin(crewMember.member, member)
                .fetchOne());
    }

    /**
     * @param crewId
     * @param categoryType
     * @param pageable
     * @deprecated Use {@link #fetchAllByCrewId(Long, CategoryType, Pageable)} instead.
     */
    // TODO Join 이 한번 더 들어가지만, 처음부터 on 절로 필터링하는게 빠른지, 아니면 join 이 하나 덜 들어가지만 where 절로 필터링하는게 더 빠를까? Execution Plan 을 확인해 볼 필요가 있음.
    @Deprecated
    public Page<SquadDomainDto> fetchAllByCrewIdV2(Long crewId, CategoryType categoryType, Pageable pageable) {
        List<SquadDomainDto> transformedResults = jpaQueryFactory
                .from(squad)
                .innerJoin(squad.crewMember, crewMember).on(squad.crew.id.eq(crewId))
                .innerJoin(crewMember.member, member)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .where(categoryEq(categoryType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squad.createdAt.desc())
                .transform(groupBy(squad.id)
                        .list(new QSquadDomainDto(
                                squad.id,
                                squad.title,
                                squad.content,
                                squad.capacity,
                                squad.remain,
                                squad.address,
                                squad.kakaoLink,
                                squad.discordLink,
                                list(category.categoryType),
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.mbti
                                )
                        ))
                );

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squad.count())
                .from(squad)
                .where(squad.crew.id.eq(crewId));

        return PageableExecutionUtils.getPage(transformedResults, pageable, countQuery::fetchOne);
    }
}
