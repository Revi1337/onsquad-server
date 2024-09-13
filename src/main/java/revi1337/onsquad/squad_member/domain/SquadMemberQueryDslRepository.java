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
import revi1337.onsquad.member.dto.QSimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.QSimpleSquadInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad_category.domain.dto.QSquadCategoryDomainDto;
import revi1337.onsquad.squad_member.domain.dto.*;

import java.util.ArrayList;
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

    public List<EnrolledSquadDomainDto> findEnrolledSquads(Long memberId) {
        List<SimpleSquadInfoDomainDto> squadInfos = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                .innerJoin(squadMember.squad, squad)
                .innerJoin(squad.crew, crew)
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(SQUAD_CREW_MEMBER.member, SQUAD_CREATOR)
                .innerJoin(squad.categories, squadCategory)
                .innerJoin(squadCategory.category, category)
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

        Map<Long, List<SimpleSquadInfoDomainDto>> squadDatas = squadInfos.stream()
                .collect(Collectors.groupingBy(SimpleSquadInfoDomainDto::crewId));

        List<SimpleCrewInfoDomainDto> crewDtos = jpaQueryFactory
                .select(new QSimpleCrewInfoDomainDto(
                        crew.id,
                        crew.name,
                        crew.kakaoLink,
                        image.imageUrl,
                        new QSimpleMemberInfoDomainDto(
                                CREW_CREATOR.id,
                                CREW_CREATOR.nickname
                        )
                ))
                .from(crew)
                .innerJoin(crew.image, image)
                .innerJoin(crew.member, CREW_CREATOR)
                .where(crew.id.in(squadDatas.keySet()))
                .fetch();

        ArrayList<EnrolledSquadDomainDto> enrolledDtos = new ArrayList<>();
        crewDtos.forEach(crewDto -> {
            Long crewId = crewDto.id();
            List<SimpleSquadInfoDomainDto> partOfCrew = squadDatas.get(crewId);
            enrolledDtos.add(new EnrolledSquadDomainDto(
                    crewDto.id(), crewDto.name(), crewDto.imageUrl(), crewDto.owner(), partOfCrew
            ));
        });

        return enrolledDtos;
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
