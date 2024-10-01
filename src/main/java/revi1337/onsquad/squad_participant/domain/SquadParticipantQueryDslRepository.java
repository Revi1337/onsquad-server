package revi1337.onsquad.squad_participant.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.QSimpleCrewInfoDomainDto;
import revi1337.onsquad.crew.domain.dto.SimpleCrewInfoDomainDto;
import revi1337.onsquad.crew_member.domain.QCrewMember;
import revi1337.onsquad.member.domain.QMember;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.QSquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.QSquadParticipantDomainDto_RequestParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.*;
import static revi1337.onsquad.category.domain.QCategory.category;
import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.image.domain.QImage.image;
import static revi1337.onsquad.squad.domain.QSquad.*;
import static revi1337.onsquad.squad_category.domain.QSquadCategory.squadCategory;
import static revi1337.onsquad.squad_participant.domain.QSquadParticipant.*;

@RequiredArgsConstructor
@Repository
public class SquadParticipantQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QCrewMember SQUAD_CREW_MEMBER = new QCrewMember("squadCrewMember");
    private final QMember CREW_CREATOR = new QMember("crewCreator");
    private final QMember SQUAD_CREATOR = new QMember("squadCreator");

    /**
     * Squad 와 Crew 의 정렬조건을 따로 줄 수 여지가 있으므로, 쿼리를 2개로 쪼갠다.
     * @param memberId
     * @return
     */
    public List<SquadParticipantRequest> findSquadParticipantRequestsByMemberId(Long memberId) {
        List<SquadParticipantDomainDto> squadParticipantDtos = jpaQueryFactory
                .from(squadParticipant)
                .innerJoin(squadParticipant.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                .innerJoin(squadParticipant.squad, squad)
                .innerJoin(squad.crewMember.member, SQUAD_CREATOR)
                .innerJoin(squad.categories, squadCategory)
//                .innerJoin(squadCategory.category, category)
                .orderBy(squadParticipant.requestAt.desc())
                .transform(groupBy(squad.id)
                        .list(new QSquadParticipantDomainDto(
                                squad.crew.id,
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.address,
                                squad.kakaoLink,
                                squad.discordLink,
                                list(category.categoryType),
                                new QSimpleMemberInfoDomainDto(
                                        SQUAD_CREATOR.id,
                                        SQUAD_CREATOR.nickname,
                                        SQUAD_CREATOR.mbti
                                ),
                                new QSquadParticipantDomainDto_RequestParticipantDomainDto(
                                        squadParticipant.id,
                                        squadParticipant.requestAt
                                )
                        ))
                );

        Map<Long, List<SquadParticipantDomainDto>> squadParticipantsMap = squadParticipantDtos.stream()
                .collect(Collectors.groupingBy(SquadParticipantDomainDto::crewId));

        Map<Long, SimpleCrewInfoDomainDto> crewDtoMap = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.image, image)
                .innerJoin(crew.member, CREW_CREATOR)
                .where(crew.id.in(squadParticipantsMap.keySet()))
                .transform(groupBy(crew.id)
                        .as(new QSimpleCrewInfoDomainDto(
                                crew.id,
                                crew.name,
                                crew.kakaoLink,
                                image.imageUrl,
                                new QSimpleMemberInfoDomainDto(
                                        CREW_CREATOR.id,
                                        CREW_CREATOR.nickname,
                                        CREW_CREATOR.mbti
                                )
                        ))
                );

        return squadParticipantsMap.keySet().stream()
                .map(crewId -> {
                    SimpleCrewInfoDomainDto crewInfo = crewDtoMap.get(crewId);
                    List<SquadParticipantDomainDto> squadsInCrew = squadParticipantsMap.get(crewId);
                    return new SquadParticipantRequest(
                            crewInfo.id(),
                            crewInfo.name(),
                            crewInfo.imageUrl(),
                            crewInfo.owner(),
                            squadsInCrew
                    );
                })
                .toList();
    }
}
