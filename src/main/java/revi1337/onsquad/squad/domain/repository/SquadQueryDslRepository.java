package revi1337.onsquad.squad.domain.repository;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.category.domain.entity.QCategory.category;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.entity.QSquadCategory.squadCategory;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.domain.model.SimpleMember;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.model.SimpleSquad;
import revi1337.onsquad.squad.domain.model.SquadDetail;
import revi1337.onsquad.squad.domain.model.SquadWithLeaderState;

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

    public List<SquadDetail> fetchSquadsWithDetailByCrewIdAndCategory(Long crewId, CategoryType categoryType, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(SquadDetail.class,
                        squad.id,
                        squad.title,
                        squad.content,
                        squad.capacity,
                        squad.remain,
                        squad.address,
                        squad.kakaoLink,
                        squad.discordLink,
                        Projections.constructor(SimpleMember.class,
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

    public List<SquadWithLeaderState> fetchManageList(Long memberId, Long crewId, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(SquadWithLeaderState.class,
                        new CaseBuilder()
                                .when(member.id.eq(memberId))
                                .then(TRUE)
                                .otherwise(FALSE),
                        Projections.constructor(SimpleSquad.class,
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                Projections.constructor(SimpleMember.class,
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
