package revi1337.onsquad.squad_request.domain.repository;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_request.domain.entity.QSquadRequest.squadRequest;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.common.domain.OrderSpecifierBuilder;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

@Repository
@RequiredArgsConstructor
public class SquadRequestQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<SquadRequest> fetchAllBySquadId(Long squadId, Pageable pageable) {
        List<SquadRequest> requests = jpaQueryFactory
                .selectFrom(squadRequest)
                .innerJoin(squadRequest.member, member).fetchJoin()
                .where(squadRequest.squad.id.eq(squadId))
                .orderBy(OrderSpecifierBuilder.startWith(squadRequest, pageable).build())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squadRequest.id)
                .from(squadRequest)
                .where(squadRequest.squad.id.eq(squadId));

        return PageableExecutionUtils.getPage(requests, pageable, countQuery::fetchOne);
    }

    public Page<SquadRequest> fetchMySquadRequestsWithDetails(Long memberId, Pageable pageable) {
        List<SquadRequest> requests = jpaQueryFactory
                .selectFrom(squadRequest)
                .innerJoin(squadRequest.squad, squad).fetchJoin()
                .innerJoin(squad.member, member).fetchJoin()
                .innerJoin(squad.crew, crew).fetchJoin()
                .where(squadRequest.member.id.eq(memberId))
                .orderBy(OrderSpecifierBuilder.startWith(squadRequest, pageable).build())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squadRequest.id.count())
                .from(squadRequest)
                .where(squadRequest.member.id.eq(memberId));

        return PageableExecutionUtils.getPage(requests, pageable, countQuery::fetchOne);
    }
}
