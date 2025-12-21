package revi1337.onsquad.squad.domain.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.category.domain.entity.QCategory.category;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.entity.QSquadCategory.squadCategory;

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
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.result.QSimpleSquadResult;
import revi1337.onsquad.squad.domain.result.QSquadResult;
import revi1337.onsquad.squad.domain.result.QSquadWithLeaderStateResult;
import revi1337.onsquad.squad.domain.result.SquadResult;
import revi1337.onsquad.squad.domain.result.SquadWithLeaderStateResult;

@RequiredArgsConstructor
@Repository
public class SquadQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Squad> findSquadWithDetailById(Long id) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(squad)
                .innerJoin(squad.member, member).fetchJoin()
                .leftJoin(squad.categories, squadCategory).fetchJoin()
                .leftJoin(squadCategory.category, category).fetchJoin()
                .where(squad.id.eq(id))
                .fetchOne()
        );
    }

    public List<SquadResult> fetchSquadsWithDetailByCrewIdAndCategory(Long crewId, CategoryType categoryType, Pageable pageable) {
        return jpaQueryFactory
                .select(new QSquadResult(
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
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(squad)
                .innerJoin(squad.member, member)
                .where(squad.crew.id.eq(crewId), categoryEq(categoryType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squad.createdAt.desc())
                .fetch();
    }

    public List<SquadWithLeaderStateResult> fetchManageList(Long memberId, Long crewId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QSquadWithLeaderStateResult(
                        new CaseBuilder()
                                .when(member.id.eq(memberId))
                                .then(TRUE)
                                .otherwise(FALSE),
                        new QSimpleSquadResult(
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        )))
                .from(squad)
                .innerJoin(squad.member, member)
                .where(squad.crew.id.eq(crewId))
                .orderBy(squad.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression categoryEq(CategoryType categoryType) {
        if (categoryType == CategoryType.ALL) {
            return null;
        }

        return category.id.eq(categoryType.getPk());
    }

    /**
     * @see #findSquadWithDetailById(Long)
     * @deprecated
     */
    @Deprecated
    public Optional<SquadResult> fetchSquadWithDetailByIdLegacy(Long id) {
        return Optional.ofNullable(jpaQueryFactory
                .select(new QSquadResult(
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
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(squad)
                .innerJoin(squad.member, member)
                .where(squad.id.eq(id))
                .fetchOne());
    }

    /**
     * @see #fetchSquadsWithDetailByCrewIdAndCategory(Long, CategoryType, Pageable)
     * @deprecated
     */
    @Deprecated
    public Page<SquadResult> fetchSquadsWithDetailByCrewIdAndCategoryLegacy(Long crewId, CategoryType categoryType, Pageable pageable) {
        List<SquadResult> transformedResults = jpaQueryFactory
                .from(squad)
                .innerJoin(squad.member, member)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .where(squad.crew.id.eq(crewId), categoryEq(categoryType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squad.createdAt.desc())
                .transform(groupBy(squad.id)
                        .list(new QSquadResult(
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
                                        member.introduce,
                                        member.mbti
                                )
                        ))
                );

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squad.count())
                .from(squad)
                .where(squad.crew.id.eq(crewId), categoryEq(categoryType));

        return PageableExecutionUtils.getPage(transformedResults, pageable, countQuery::fetchOne);
    }

    /**
     * @see #fetchSquadsWithDetailByCrewIdAndCategory(Long, CategoryType, Pageable)
     * @deprecated
     */
    @Deprecated
    public Page<SquadResult> fetchSquadsWithDetailByCrewIdAndCategoryLegacyV2(Long crewId, CategoryType categoryType, Pageable pageable) {
        List<Long> squadIds = jpaQueryFactory
                .select(squad.id)
                .from(squad)
                .where(squad.crew.id.eq(crewId), categoryEq(categoryType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(squad.createdAt.desc())
                .fetch();

        Map<Long, SquadResult> groupResults = jpaQueryFactory
                .from(squad)
                .innerJoin(squad.member, member)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .where(squad.id.in(squadIds))
                .transform(groupBy(squad.id)
                        .as(new QSquadResult(
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
                                        member.introduce,
                                        member.mbti
                                )
                        ))
                );

        List<SquadResult> results = squadIds.stream()
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

    /**
     * @see #fetchManageList(Long, Long, Pageable)
     * @deprecated
     */
    @Deprecated
    public Page<SquadWithLeaderStateResult> fetchManageListLegacy(Long memberId, Long crewId, Pageable pageable) {
        List<SquadWithLeaderStateResult> results = jpaQueryFactory
                .select(new QSquadWithLeaderStateResult(
                        new CaseBuilder()
                                .when(member.id.eq(memberId))
                                .then(TRUE)
                                .otherwise(FALSE),
                        new QSimpleSquadResult(
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        )))
                .from(squad)
                .innerJoin(squad.member, member)
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
                .map(SquadWithLeaderStateResult::squad)
                .forEach(squad -> {
                    if (categories.get(squad.id()) != null) {
                        squad.addCategories(categories.get(squad.id()));
                    }
                });

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
