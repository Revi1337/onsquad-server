package revi1337.onsquad.squad.domain.repository;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.category.domain.entity.QCategory.category;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.entity.QSquadCategory.squadCategory;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.result.QSimpleSquadResult;
import revi1337.onsquad.squad.domain.result.QSquadResult;
import revi1337.onsquad.squad.domain.result.QSquadWithLeaderStateResult;
import revi1337.onsquad.squad.domain.result.SquadResult;
import revi1337.onsquad.squad.domain.result.SquadWithLeaderStateResult;

@Repository
@RequiredArgsConstructor
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
                        new QSimpleMemberResult(
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
                                new QSimpleMemberResult(
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
}
