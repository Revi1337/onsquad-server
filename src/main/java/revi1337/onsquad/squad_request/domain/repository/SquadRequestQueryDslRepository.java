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
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.result.QSquadRequestResult;
import revi1337.onsquad.squad_request.domain.result.SquadRequestResult;

@Repository
@RequiredArgsConstructor
public class SquadRequestQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<SquadRequestResult> fetchAllBySquadId(Long squadId, Pageable pageable) {
        List<SquadRequestResult> results = jpaQueryFactory
                .select(new QSquadRequestResult(
                        squadRequest.id,
                        squadRequest.requestAt,
                        new QSimpleMemberResult(
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
