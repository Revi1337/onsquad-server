package revi1337.onsquad.squad_participant.domain;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static revi1337.onsquad.category.domain.QCategory.category;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.image.domain.QImage.image;
import static revi1337.onsquad.member.domain.QMember.member;
import static revi1337.onsquad.squad.domain.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.QSquadCategory.squadCategory;
import static revi1337.onsquad.squad_participant.domain.QSquadParticipant.squadParticipant;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.QSimpleCrewInfoDomainDto;
import revi1337.onsquad.crew.domain.dto.SimpleCrewInfoDomainDto;
import revi1337.onsquad.crew_member.domain.QCrewMember;
import revi1337.onsquad.member.domain.QMember;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.QSimpleSquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.QSquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.QSquadParticipantDomainDto_RequestParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.SimpleSquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;

@RequiredArgsConstructor
@Repository
public class SquadParticipantQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QCrewMember SQUAD_CREW_MEMBER = new QCrewMember("squadCrewMember");
    private final QMember CREW_CREATOR = new QMember("crewCreator");
    private final QMember SQUAD_CREATOR = new QMember("squadCreator");

    /**
     * Squad 와 Crew 의 정렬조건을 따로 줄 수 여지가 있으므로, 쿼리를 2개로 쪼갠다.
     *
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
                .innerJoin(crew.member, CREW_CREATOR)
                .leftJoin(crew.image, image)
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

    public List<SimpleSquadParticipantDomainDto> fetchAllWithMemberBySquadId(Long squadId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QSimpleSquadParticipantDomainDto(
                        squadParticipant.id,
                        squadParticipant.requestAt,
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname,
                                member.mbti
                        )
                ))
                .from(squadParticipant)
                .innerJoin(squadParticipant.crewMember, crewMember).on(squadParticipant.squad.id.eq(squadId))
                .innerJoin(crewMember.member, member)
                .orderBy(squadParticipant.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
