package revi1337.onsquad.squad_participant.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.QSimpleCrewInfoDomainDto;
import revi1337.onsquad.crew.domain.dto.SimpleCrewInfoDomainDto;
import revi1337.onsquad.crew_member.domain.QCrewMember;
import revi1337.onsquad.member.domain.QMember;
import revi1337.onsquad.member.dto.QSimpleMemberInfoDomainDto;
import revi1337.onsquad.squad_category.domain.dto.QSquadCategoryDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.QSquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.QSquadParticipantDomainDto_RequestParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;

import java.util.ArrayList;
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
                                list(new QSquadCategoryDomainDto(
                                        category.id,
                                        category.categoryType
                                )),
                                new QSimpleMemberInfoDomainDto(
                                        SQUAD_CREATOR.id,
                                        SQUAD_CREATOR.nickname
                                ),
                                new QSquadParticipantDomainDto_RequestParticipantDomainDto(
                                        squadParticipant.id,
                                        squadParticipant.requestAt
                                )
                        ))
                );

        Map<Long, List<SquadParticipantDomainDto>> squadParticipantDatas = squadParticipantDtos.stream()
                .collect(Collectors.groupingBy(SquadParticipantDomainDto::crewId));

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
                .where(crew.id.in(squadParticipantDatas.keySet()))
                .fetch();

        ArrayList<SquadParticipantRequest> squadParticipantRequests = new ArrayList<>();
        crewDtos.forEach(crewDto -> {
            Long crewId = crewDto.id();
            List<SquadParticipantDomainDto> partOfCrew = squadParticipantDatas.get(crewId);
            squadParticipantRequests.add(new SquadParticipantRequest(
                    crewDto.id(), crewDto.name(), crewDto.imageUrl(), crewDto.owner(), partOfCrew
            ));
        });

        return squadParticipantRequests;
    }
}





//package revi1337.onsquad.squad_participant.domain;
//
//import com.querydsl.core.group.Group;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//import revi1337.onsquad.crew.domain.vo.Name;
//import revi1337.onsquad.crew_member.domain.QCrewMember;
//import revi1337.onsquad.member.domain.QMember;
//import revi1337.onsquad.member.dto.QSimpleMemberInfoDomainDto;
//import revi1337.onsquad.member.dto.SimpleMemberInfoDomainDto;
//import revi1337.onsquad.squad.domain.dto.QSquadInfoDomainDto;
//import revi1337.onsquad.squad_participant.domain.dto.QSquadParticipantDomainDto;
//import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantDomainDto;
//import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import static com.querydsl.core.group.GroupBy.*;
//import static revi1337.onsquad.crew.domain.QCrew.*;
//import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
//import static revi1337.onsquad.image.domain.QImage.*;
//import static revi1337.onsquad.squad.domain.QSquad.*;
//import static revi1337.onsquad.squad_participant.domain.QSquadParticipant.*;
//
//@RequiredArgsConstructor
//@Repository
//public class SquadParticipantQueryDslRepository {
//
//    private final JPAQueryFactory jpaQueryFactory;
//
//    private final QCrewMember SQUAD_CREW_MEMBER = new QCrewMember("squadCrewMember");
//    private final QMember CREW_CREATOR = new QMember("crewCreator");
//    private final QMember SQUAD_CREATOR = new QMember("squadCreator");
//
//    public List<SquadParticipantRequest> findSquadParticipantRequestsByMemberId(Long memberId) {
//        Map<Long, Group> transformedResults = jpaQueryFactory
//                .from(squadParticipant)
//                .innerJoin(squadParticipant.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
//                .innerJoin(squadParticipant.squad, squad)
//                .innerJoin(squad.crewMember.member, SQUAD_CREATOR)
//                .innerJoin(squad.crew, crew)
//                .innerJoin(crew.image, image)
//                .innerJoin(crew.member, CREW_CREATOR)
//                .orderBy(squadParticipant.requestAt.desc())
//                .transform(groupBy(crew.id)
//                        .as(
//                                crew.id,
//                                crew.name,
//                                new QSimpleMemberInfoDomainDto(
//                                        CREW_CREATOR.id,
//                                        CREW_CREATOR.nickname
//                                ),
//                                list(
//                                        new QSquadParticipantDomainDto(
//                                                squadParticipant.id,
//                                                squadParticipant.requestAt,
//                                                new QSquadInfoDomainDto(
//                                                        squad.id,
//                                                        squad.title,
//                                                        squad.content,
//                                                        squad.capacity,
//                                                        squad.address,
//                                                        squad.kakaoLink,
//                                                        squad.discordLink,
//                                                        new QSimpleMemberInfoDomainDto(
//                                                                SQUAD_CREATOR.id,
//                                                                SQUAD_CREATOR.nickname
//                                                        )
//                                                )
//                                        )
//                                )
//                        )
//                );
//
//        List<SquadParticipantRequest> squadParticipantRequests = new ArrayList<>();
//        transformedResults.forEach((ignored, group) -> {
//            Object[] array = group.toArray();
//            SquadParticipantRequest squadParticipantRequest = new SquadParticipantRequest(
//                    (Long) array[1], (Name) array[2], (SimpleMemberInfoDomainDto) array[3], (List<SquadParticipantDomainDto>) array[4]
//            );
//            squadParticipantRequests.add(squadParticipantRequest);
//        });
//
//        return squadParticipantRequests;
//    }
//}







//package revi1337.onsquad.squad_participant.domain;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//import revi1337.onsquad.crew_member.domain.QCrewMember;
//import revi1337.onsquad.member.domain.QMember;
//import revi1337.onsquad.member.dto.QSimpleMemberInfoDomainDto;
//import revi1337.onsquad.squad.domain.dto.QSquadInfoDomainDto;
//import revi1337.onsquad.squad_participant.domain.dto.QSquadParticipantRequest;
//import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;
//
//import java.util.List;
//import java.util.Map;
//
//import static com.querydsl.core.group.GroupBy.*;
//        import static revi1337.onsquad.crew.domain.QCrew.*;
//        import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
//        import static revi1337.onsquad.image.domain.QImage.*;
//        import static revi1337.onsquad.squad.domain.QSquad.*;
//        import static revi1337.onsquad.squad_participant.domain.QSquadParticipant.*;
//
//@RequiredArgsConstructor
//@Repository
//public class SquadParticipantQueryDslRepository {
//
//    private final JPAQueryFactory jpaQueryFactory;
//
//    private final QCrewMember SQUAD_CREW_MEMBER = new QCrewMember("squadCrewMember");
//    private final QMember CREW_CREATOR = new QMember("crewCreator");
//    private final QMember SQUAD_CREATOR = new QMember("squadCreator");
//
//    /**
//     * 조금 더 묵시적인 방법 `.innerJoin(squad.crewMember.member, SQUAD_CREATOR)`
//     * 내부적으로 squad.crewMember 조인이 하나 더 나간다.
//     */
//    public List<SquadParticipantRequest> findSquadParticipantRequestsByMemberId(Long memberId) {
//        Map<Long, List<SquadParticipantRequest>> squadParticipants = jpaQueryFactory
//                .from(squadParticipant)
//                .innerJoin(squadParticipant.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
//                .innerJoin(squadParticipant.squad, squad)
//                .innerJoin(squad.crewMember.member, SQUAD_CREATOR)
//                .innerJoin(squad.crew, crew)
//                .innerJoin(crew.image, image)
//                .innerJoin(crew.member, CREW_CREATOR)
//                .orderBy(squadParticipant.requestAt.desc())
//                .transform(groupBy(crew.id).as(list(new QSquadParticipantRequest(
//                        crew.id,
//                        crew.name,
//                        new QSimpleMemberInfoDomainDto(
//                                CREW_CREATOR.id,
//                                CREW_CREATOR.nickname
//                        ),
//                        new QSquadInfoDomainDto(
//                                squad.id,
//                                squad.title,
//                                squad.content,
//                                squad.capacity,
//                                squad.address,
//                                squad.kakaoLink,
//                                squad.discordLink,
//                                new QSimpleMemberInfoDomainDto(
//                                        SQUAD_CREATOR.id,
//                                        SQUAD_CREATOR.nickname
//                                )
//                        )
//                ))));
//        return List.of();
//    }
//
//    public List<SquadParticipantRequest> findSquadParticipantRequestsByMemberIdOriginal(Long memberId) {
//        return jpaQueryFactory
//                .select(
//                        new QSquadParticipantRequest(
//                                crew.id,
//                                crew.name,
//                                new QSimpleMemberInfoDomainDto(
//                                        CREW_CREATOR.id,
//                                        CREW_CREATOR.nickname
//                                ),
//                                new QSquadInfoDomainDto(
//                                        squad.id,
//                                        squad.title,
//                                        squad.content,
//                                        squad.capacity,
//                                        squad.address,
//                                        squad.kakaoLink,
//                                        squad.discordLink,
//                                        new QSimpleMemberInfoDomainDto(
//                                                SQUAD_CREATOR.id,
//                                                SQUAD_CREATOR.nickname
//                                        )
//                                )
//                        )
//                )
//                .from(squadParticipant)
//                .innerJoin(squadParticipant.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
//                .innerJoin(squadParticipant.squad, squad)
//                .innerJoin(squad.crewMember.member, SQUAD_CREATOR)
//                .innerJoin(squad.crew, crew)
//                .innerJoin(crew.image, image)
//                .innerJoin(crew.member, CREW_CREATOR)
//                .fetch();
//    }
//
//    /**
//     * 조금 더 명시적인 방법 `.innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)`
//     */
//    private List<SquadParticipantRequest> findSquadParticipantRequestsByMemberIdV2(Long memberId) {
//        return jpaQueryFactory
//                .select(
//                        new QSquadParticipantRequest(
//                                crew.id,
//                                crew.name,
//                                new QSimpleMemberInfoDomainDto(
//                                        CREW_CREATOR.id,
//                                        CREW_CREATOR.nickname
//                                ),
//                                new QSquadInfoDomainDto(
//                                        squad.id,
//                                        squad.title,
//                                        squad.content,
//                                        squad.capacity,
//                                        squad.address,
//                                        squad.kakaoLink,
//                                        squad.discordLink,
//                                        new QSimpleMemberInfoDomainDto(
//                                                SQUAD_CREATOR.id,
//                                                SQUAD_CREATOR.nickname
//                                        )
//                                )
//                        )
//                )
//                .from(squadParticipant)
//                .innerJoin(squadParticipant.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
//                .innerJoin(squadParticipant.squad, squad)
//                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
//                .innerJoin(SQUAD_CREW_MEMBER.member, SQUAD_CREATOR)
//                .innerJoin(squad.crew, crew)
//                .innerJoin(crew.image, image)
//                .innerJoin(crew.member, CREW_CREATOR)
//                .fetch();
//    }
//}


