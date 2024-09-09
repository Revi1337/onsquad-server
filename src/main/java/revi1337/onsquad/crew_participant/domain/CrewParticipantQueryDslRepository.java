package revi1337.onsquad.crew_participant.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_participant.domain.dto.*;
import revi1337.onsquad.member.dto.QSimpleMemberInfoDomainDto;

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
                                member.nickname
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

    public List<SimpleCrewParticipantRequest> findCrewRequestsInCrew(Name name) {
        return jpaQueryFactory
                .select(new QSimpleCrewParticipantRequest(
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname
                        ),
                        new QCrewParticipantDomainDto(
                                crewParticipant.id,
                                crewParticipant.requestAt
                        )
                ))
                .from(crewParticipant)
                .innerJoin(crewParticipant.crew, crew).on(crew.name.eq(name))
                .innerJoin(crewParticipant.member, member)
                .orderBy(crewParticipant.requestAt.desc())
                .fetch();
    }
}
