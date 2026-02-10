package revi1337.onsquad.crew_request.domain.repository;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_request.domain.entity.QCrewRequest.crewRequest;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;

@Repository
@RequiredArgsConstructor
public class CrewRequestQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<CrewRequest> fetchAllWithSimpleCrewByMemberId(Long memberId, Pageable pageable) {
        List<CrewRequest> results = jpaQueryFactory
                .selectFrom(crewRequest)
                .innerJoin(crewRequest.crew, crew).fetchJoin()
                .innerJoin(crew.member, member).fetchJoin()
                .where(crewRequest.member.id.eq(memberId))
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crewRequest.id.count())
                .from(crewRequest)
                .where(crewRequest.member.id.eq(memberId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    public Page<CrewRequest> fetchCrewRequests(Long crewId, Pageable pageable) {
        List<CrewRequest> results = jpaQueryFactory
                .selectFrom(crewRequest)
                .innerJoin(crewRequest.member).fetchJoin()
                .where(crewRequest.crew.id.eq(crewId))
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crewRequest.id.count())
                .from(crewRequest)
                .where(crewRequest.crew.id.eq(crewId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder<CrewRequest> pathBuilder = new PathBuilder<>(crewRequest.getType(), crewRequest.getMetadata());
            orderSpecifiers.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
        }
        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}
