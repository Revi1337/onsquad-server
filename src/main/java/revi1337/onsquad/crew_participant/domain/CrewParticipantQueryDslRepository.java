package revi1337.onsquad.crew_participant.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_participant.domain.dto.*;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;

import java.util.List;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.crew_participant.domain.QCrewParticipant.crewParticipant;
import static revi1337.onsquad.image.domain.QImage.*;
import static revi1337.onsquad.member.domain.QMember.*;

@RequiredArgsConstructor
@Repository
public class CrewParticipantQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<CrewParticipantRequest> findMyCrewRequests(Long memberId) {
        return jpaQueryFactory
                .select(new QCrewParticipantRequest(
                        crew.id,
                        crew.name,
                        image.imageUrl,
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
                .innerJoin(crew.image, image)
                .innerJoin(crew.member, member)
                .orderBy(crewParticipant.requestAt.desc())
                .fetch();
    }

    public List<SimpleCrewParticipantRequest> findCrewRequestsInCrew(Long crewId) {
        return jpaQueryFactory
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
                .fetch();
    }
}
