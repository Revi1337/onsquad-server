package revi1337.onsquad.crew_member.domain.repository;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.model.SimpleCrew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.model.MyParticipantCrew;
import revi1337.onsquad.member.domain.model.SimpleMember;

@Repository
@RequiredArgsConstructor
public class CrewMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<CrewMember> fetchParticipantsByCrewId(Long crewId, Pageable pageable) {
        List<CrewMember> participants = jpaQueryFactory
                .selectFrom(crewMember)
                .innerJoin(crewMember.member, member).fetchJoin()
                .where(crewMember.crew.id.eq(crewId))
                .orderBy(getOrderSpecifiers(crewMember, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crew.currentSize)
                .from(crew)
                .where(crew.id.eq(crewId));

        return PageableExecutionUtils.getPage(participants, pageable, countQuery::fetchOne);
    }

    public Page<MyParticipantCrew> fetchParticipantCrews(Long memberId, Pageable pageable) {
        ComparableExpression<Boolean> isCrewOwner = new CaseBuilder()
                .when(member.id.eq(memberId))
                .then(TRUE)
                .otherwise(FALSE);

        List<MyParticipantCrew> participants = jpaQueryFactory
                .select(Projections.constructor(MyParticipantCrew.class,
                        isCrewOwner,
                        crewMember.participateAt,
                        Projections.constructor(SimpleCrew.class,
                                crew.id,
                                crew.name.value,
                                crew.introduce.value,
                                crew.kakaoLink,
                                crew.imageUrl,
                                Projections.constructor(SimpleMember.class,
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        )
                ))
                .from(crewMember)
                .innerJoin(crewMember.crew, crew)
                .innerJoin(crew.member, member)
                .where(crewMember.member.id.eq(memberId))
                .orderBy(isCrewOwner.desc(), crewMember.participateAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crewMember.id.count())
                .from(crewMember)
                .where(crewMember.member.id.eq(memberId));

        return PageableExecutionUtils.getPage(participants, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(EntityPathBase<?> entityPathBase, Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        PathBuilder<?> pathBuilder = new PathBuilder<>(entityPathBase.getType(), entityPathBase.getMetadata());
        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            orderSpecifiers.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
        }
        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}
