package revi1337.onsquad.squad_request.domain.repository;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_request.domain.entity.QSquadRequest.squadRequest;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.member.domain.model.SimpleMember;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.model.SquadRequestDetail;

@Repository
@RequiredArgsConstructor
public class SquadRequestQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<SquadRequestDetail> fetchAllBySquadId(Long squadId, Pageable pageable) {
        List<SquadRequestDetail> results = jpaQueryFactory
                .select(Projections.constructor(SquadRequestDetail.class,
                        squadRequest.id,
                        squadRequest.requestAt,
                        Projections.constructor(SimpleMember.class,
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(squadRequest)
                .innerJoin(squadRequest.member, member)
                .where(squadRequest.squad.id.eq(squadId))
                .orderBy(squadRequest.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squadRequest.id)
                .from(squadRequest)
                .where(squadRequest.squad.id.eq(squadId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    public List<SquadRequest> fetchMySquadRequestsWithDetails(Long memberId) {
        return jpaQueryFactory
                .selectFrom(squadRequest)
                .innerJoin(squadRequest.squad, squad).fetchJoin()
                .innerJoin(squad.member, member).fetchJoin()
                .innerJoin(squad.crew, crew).fetchJoin()
                .where(squadRequest.member.id.eq(memberId))
                .orderBy(squadRequest.requestAt.desc())
                .fetch();
    }
}
