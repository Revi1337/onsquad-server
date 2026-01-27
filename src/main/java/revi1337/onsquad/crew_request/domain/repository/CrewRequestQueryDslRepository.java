package revi1337.onsquad.crew_request.domain.repository;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_request.domain.entity.QCrewRequest.crewRequest;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;

@Repository
@RequiredArgsConstructor
public class CrewRequestQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<CrewRequest> fetchAllWithSimpleCrewByMemberId(Long memberId) {
        return jpaQueryFactory
                .selectFrom(crewRequest)
                .innerJoin(crewRequest.crew, crew).fetchJoin()
                .innerJoin(crew.member, member).fetchJoin()
                .where(crewRequest.member.id.eq(memberId))
                .orderBy(crewRequest.requestAt.desc())
                .fetch();
    }

    public Page<CrewRequest> fetchCrewRequests(Long crewId, Pageable pageable) {
        List<CrewRequest> results = jpaQueryFactory
                .selectFrom(crewRequest)
                .innerJoin(crewRequest.member).fetchJoin()
                .where(crewRequest.crew.id.eq(crewId))
                .orderBy(crewRequest.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crewRequest.id.count())
                .from(crewRequest)
                .where(crewRequest.crew.id.eq(crewId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
