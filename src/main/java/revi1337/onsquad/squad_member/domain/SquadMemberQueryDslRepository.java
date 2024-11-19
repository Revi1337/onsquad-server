package revi1337.onsquad.squad_member.domain;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.group.GroupBy.set;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.category.domain.QCategory.category;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.image.domain.QImage.image;
import static revi1337.onsquad.member.domain.QMember.member;
import static revi1337.onsquad.squad.domain.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.QSquadCategory.squadCategory;
import static revi1337.onsquad.squad_member.domain.QSquadMember.squadMember;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.QSimpleCrewInfoDomainDto;
import revi1337.onsquad.crew.domain.dto.SimpleCrewInfoDomainDto;
import revi1337.onsquad.crew_member.domain.QCrewMember;
import revi1337.onsquad.member.domain.QMember;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.QSimpleSquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.QSquadMemberDomainDto;
import revi1337.onsquad.squad_member.domain.dto.QSquadWithMemberDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadWithMemberDomainDto;

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
     *
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
                                        SQUAD_CREATOR.nickname,
                                        SQUAD_CREATOR.mbti
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
                                        CREW_CREATOR.nickname,
                                        CREW_CREATOR.mbti
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

    public Optional<SquadWithMemberDomainDto> findSquadMembers(Long memberId, Long crewId, Long squadId) {
        Map<Long, SquadWithMemberDomainDto> results = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.squad, squad)
                .on(
                        squad.crew.id.eq(crewId),
                        squad.id.eq(squadId)
                )
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(squadMember.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .innerJoin(squad.categories, squadCategory)
                .innerJoin(squadCategory.category, category)
                .orderBy(squadMember.requestAt.desc())
                .transform(groupBy(squad.id)
                        .as(new QSquadWithMemberDomainDto(
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
                                                member.nickname,
                                                member.mbti
                                        ),
                                        squadMember.requestAt
                                ))
                        )));

        return Optional.ofNullable(results.get(squadId));
    }
}
