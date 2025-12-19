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
import revi1337.onsquad.crew.domain.result.QSimpleCrewResult;
import revi1337.onsquad.crew_request.domain.result.CrewRequestWithCrewResult;
import revi1337.onsquad.crew_request.domain.result.CrewRequestWithMemberResult;
import revi1337.onsquad.crew_request.domain.result.QCrewRequestResult;
import revi1337.onsquad.crew_request.domain.result.QCrewRequestWithCrewResult;
import revi1337.onsquad.crew_request.domain.result.QCrewRequestWithMemberResult;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewRequestQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<CrewRequestWithCrewResult> fetchAllWithSimpleCrewByMemberId(Long memberId) {
        return jpaQueryFactory
                .select(new QCrewRequestWithCrewResult(
                        new QSimpleCrewResult(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                crew.kakaoLink,
                                crew.imageUrl,
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        ),
                        new QCrewRequestResult(
                                crewRequest.id,
                                crewRequest.requestAt
                        )
                ))
                .from(crewRequest)
                .innerJoin(crewRequest.crew, crew).on(crewRequest.member.id.eq(memberId))
                .innerJoin(crew.member, member)
                .orderBy(crewRequest.requestAt.desc())
                .fetch();
    }

    public Page<CrewRequestWithMemberResult> fetchCrewRequests(Long crewId, Pageable pageable) {
        List<CrewRequestWithMemberResult> results = jpaQueryFactory
                .select(new QCrewRequestWithMemberResult(
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        ),
                        new QCrewRequestResult(
                                crewRequest.id,
                                crewRequest.requestAt
                        )
                ))
                .from(crewRequest)
                .innerJoin(crewRequest.member, member).on(crewRequest.crew.id.eq(crewId))
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
