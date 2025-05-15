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
import revi1337.onsquad.crew.domain.dto.QSimpleCrewInfoDomainDto;
import revi1337.onsquad.crew_participant.domain.dto.CrewRequestWithCrewDomainDto;
import revi1337.onsquad.crew_participant.domain.dto.CrewRequestWithMemberDomainDto;
import revi1337.onsquad.crew_participant.domain.dto.QCrewRequestDomainDto;
import revi1337.onsquad.crew_participant.domain.dto.QCrewRequestWithCrewDomainDto;
import revi1337.onsquad.crew_participant.domain.dto.QCrewRequestWithMemberDomainDto;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewParticipantQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<CrewRequestWithCrewDomainDto> fetchAllWithSimpleCrewByMemberId(Long memberId) {
        return jpaQueryFactory
                .select(new QCrewRequestWithCrewDomainDto(
                        new QSimpleCrewInfoDomainDto(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                crew.kakaoLink,
                                crew.imageUrl,
                                new QSimpleMemberInfoDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.mbti
                                )
                        ),
                        new QCrewRequestDomainDto(
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

    public Page<CrewRequestWithMemberDomainDto> fetchCrewRequests(Long crewId, Pageable pageable) {
        List<CrewRequestWithMemberDomainDto> results = jpaQueryFactory
                .select(new QCrewRequestWithMemberDomainDto(
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname,
                                member.mbti
                        ),
                        new QCrewRequestDomainDto(
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
