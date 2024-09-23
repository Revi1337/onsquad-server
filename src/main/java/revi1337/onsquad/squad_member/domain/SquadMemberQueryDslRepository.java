package revi1337.onsquad.squad_member.domain;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.QSimpleCrewInfoDomainDto;
import revi1337.onsquad.crew.domain.dto.SimpleCrewInfoDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.QCrewMember;
import revi1337.onsquad.member.domain.QMember;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.QSimpleSquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad_member.domain.dto.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.*;
import static java.lang.Boolean.*;
import static revi1337.onsquad.category.domain.QCategory.*;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.image.domain.QImage.*;
import static revi1337.onsquad.member.domain.QMember.*;
import static revi1337.onsquad.squad.domain.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.QSquadCategory.*;
import static revi1337.onsquad.squad_member.domain.QSquadMember.*;

@RequiredArgsConstructor
@Repository
public class SquadMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QSquadMember NEW_SQUAD_MEMBER = new QSquadMember("newSquadMember");
    private final QCrewMember SQUAD_CREW_MEMBER = new QCrewMember("squadCrewMember");
    private final QMember CREW_CREATOR = new QMember("crewCreator");
    private final QMember SQUAD_CREATOR = new QMember("squadCreator");

    /**
     * Squad 와 Crew 의 정렬조건을 따로 줄 수 여지가 있으므로, 쿼리를 2개로 쪼갠다.
     * @param memberId
     * @return
     */
    public List<EnrolledSquadDomainDto> findEnrolledSquads(Long memberId) {
        List<SimpleSquadInfoDomainDto> squadInfoDtos = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                .innerJoin(squadMember.squad, squad)
                .innerJoin(squad.crew, crew)
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(SQUAD_CREW_MEMBER.member, SQUAD_CREATOR)
                .innerJoin(squad.categories, squadCategory)
                .innerJoin(squadCategory.category, category)
                .orderBy(squadMember.requestAt.desc())
                .transform(groupBy(squad.id)
                        .list(new QSimpleSquadInfoDomainDto(
                                crew.id,
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.address,
                                squad.kakaoLink,
                                squad.discordLink,
                                new CaseBuilder()
                                        .when(SQUAD_CREATOR.id.eq(memberId))
                                        .then(TRUE)
                                        .otherwise(FALSE),
                                list(category.categoryType),
                                new QSimpleMemberInfoDomainDto(
                                        SQUAD_CREATOR.id,
                                        SQUAD_CREATOR.nickname
                                )
                        ))
                );

        Map<Long, List<SimpleSquadInfoDomainDto>> squadMembersMap = squadInfoDtos.stream()
                .collect(Collectors.groupingBy(SimpleSquadInfoDomainDto::crewId));

        Map<Long, SimpleCrewInfoDomainDto> crewDtoMap = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.image, image)
                .innerJoin(crew.member, CREW_CREATOR)
                .where(crew.id.in(squadMembersMap.keySet()))
                .transform(groupBy(crew.id)
                        .as(new QSimpleCrewInfoDomainDto(
                                crew.id,
                                crew.name,
                                crew.kakaoLink,
                                image.imageUrl,
                                new QSimpleMemberInfoDomainDto(
                                        CREW_CREATOR.id,
                                        CREW_CREATOR.nickname
                                )
                        ))
                );

        return squadMembersMap.keySet().stream()
                .map(crewId -> {
                    SimpleCrewInfoDomainDto crewInfo = crewDtoMap.get(crewId);
                    List<SimpleSquadInfoDomainDto> squadsInCrew = squadMembersMap.get(crewId);
                    return new EnrolledSquadDomainDto(
                            crewInfo.id(),
                            crewInfo.name(),
                            crewInfo.imageUrl(),
                            crewInfo.owner(),
                            squadsInCrew
                    );
                })
                .toList();
    }

    public Optional<SquadWithMemberDomainDto> findSquadMembers(Long memberId, Name crewName, Long squadId) {
        Map<Long, SquadWithMemberDomainDto> transform = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.squad, squad).on(squad.id.eq(squadId))
                .innerJoin(squad.crew, crew).on(crew.name.eq(crewName))
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(squad.categories, squadCategory)
                .innerJoin(squadCategory.category, category)
                .innerJoin(squad.squadMembers, NEW_SQUAD_MEMBER)
                .innerJoin(NEW_SQUAD_MEMBER.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .distinct()
                .transform(groupBy(squad.id)
                        .as(
                                new QSquadWithMemberDomainDto(
                                        squad.id,
                                        squad.title,
                                        squad.capacity,
                                        squad.address,
                                        squad.kakaoLink,
                                        squad.discordLink,
                                        new CaseBuilder()
                                                .when(SQUAD_CREW_MEMBER.member.id.eq(memberId))
                                                .then(TRUE)
                                                .otherwise(FALSE),
                                        set(category.categoryType),
                                        list(new QSquadMemberDomainDto(
                                                new QSimpleMemberInfoDomainDto(
                                                        member.id,
                                                        member.nickname
                                                ),
                                                squadMember.requestAt

                                        ))
                                )
                        ));

        return Optional.ofNullable(transform.get(squadId));
    }
}
