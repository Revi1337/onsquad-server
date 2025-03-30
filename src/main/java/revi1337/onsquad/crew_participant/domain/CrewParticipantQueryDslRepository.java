package revi1337.onsquad.crew_participant.domain;

import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_participant.domain.QCrewParticipant.crewParticipant;
import static revi1337.onsquad.member.domain.QMember.member;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_participant.domain.dto.CrewParticipantRequest;
import revi1337.onsquad.crew_participant.domain.dto.QCrewParticipantDomainDto;
import revi1337.onsquad.crew_participant.domain.dto.QCrewParticipantRequest;
import revi1337.onsquad.crew_participant.domain.dto.QSimpleCrewParticipantRequest;
import revi1337.onsquad.crew_participant.domain.dto.SimpleCrewParticipantRequest;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewParticipantQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<CrewParticipantRequest> fetchAllCrewRequestsByMemberId(Long memberId) {
        return jpaQueryFactory
                .select(new QCrewParticipantRequest(
                        crew.id,
                        crew.name,
                        crew.imageUrl,
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname,
                                member.mbti
                        ),
                        new QCrewParticipantDomainDto(
                                crewParticipant.id,
                                crewParticipant.requestAt
                        )
                ))
                .from(crewParticipant)
                .innerJoin(crewParticipant.crew, crew).on(crewParticipant.member.id.eq(memberId))
                .innerJoin(crew.member, member)
                .orderBy(crewParticipant.requestAt.desc())
                .fetch();
    }

    public Page<SimpleCrewParticipantRequest> fetchCrewRequests(Long crewId, Pageable pageable) {
        List<SimpleCrewParticipantRequest> results = jpaQueryFactory
                .select(new QSimpleCrewParticipantRequest(
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname,
                                member.mbti
                        ),
                        new QCrewParticipantDomainDto(
                                crewParticipant.id,
                                crewParticipant.requestAt
                        )
                ))
                .from(crewParticipant)
                .innerJoin(crewParticipant.member, member).on(crewParticipant.crew.id.eq(crewId))
                .orderBy(crewParticipant.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crewParticipant.id.count())
                .from(crewParticipant)
                .where(crewParticipant.crew.id.eq(crewId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
