package revi1337.onsquad.squad_participant.domain.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static revi1337.onsquad.category.domain.entity.QCategory.category;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.entity.QSquadCategory.squadCategory;
import static revi1337.onsquad.squad_participant.domain.entity.QSquadParticipant.squadParticipant;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.QSimpleCrewDomainDto;
import revi1337.onsquad.crew.domain.dto.SimpleCrewDomainDto;
import revi1337.onsquad.crew_member.domain.entity.QCrewMember;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;
import revi1337.onsquad.member.domain.entity.QMember;
import revi1337.onsquad.squad_participant.domain.dto.MySquadRequestDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.QMySquadRequestDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.QMySquadRequestDomainDto_SquadWithParticipant;
import revi1337.onsquad.squad_participant.domain.dto.QMySquadRequestDomainDto_SquadWithParticipant_RequestParticipantDomainDto;
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

    public List<SquadParticipantRequest> findSquadParticipantRequestsByMemberId(Long memberId) {
        Map<Long, List<MySquadRequestDomainDto>> transformed = jpaQueryFactory
                .from(squadParticipant)
                .innerJoin(squadParticipant.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                .innerJoin(squadParticipant.squad, squad)
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(SQUAD_CREW_MEMBER.member, SQUAD_CREATOR)
                .innerJoin(squad.crew, crew)
                .innerJoin(crew.member, member)
                .orderBy(squadParticipant.requestAt.desc())
                .transform(groupBy(crew.id).as(
                        list(new QMySquadRequestDomainDto(
                                crew.id,
                                crew.name,
                                crew.imageUrl,
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                ),
                                list(new QMySquadRequestDomainDto_SquadWithParticipant(
                                        squad.id,
                                        squad.title,
                                        squad.capacity,
                                        squad.remain,
                                        new QSimpleMemberDomainDto(
                                                SQUAD_CREATOR.id,
                                                SQUAD_CREATOR.nickname,
                                                SQUAD_CREATOR.introduce,
                                                SQUAD_CREATOR.mbti
                                        ),
                                        new QMySquadRequestDomainDto_SquadWithParticipant_RequestParticipantDomainDto(
                                                squadParticipant.id,
                                                squadParticipant.requestAt
                                        )
                                ))
                        ))
                ));

        return List.of();
    }

    public Page<SimpleSquadParticipantDomainDto> fetchAllBySquadId(Long squadId, Pageable pageable) {
        List<SimpleSquadParticipantDomainDto> results = jpaQueryFactory
                .select(new QSimpleSquadParticipantDomainDto(
                        squadParticipant.id,
                        squadParticipant.requestAt,
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
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

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squadParticipant.id.count())
                .from(squadParticipant)
                .where(squadParticipant.squad.id.eq(squadId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    /**
     * Squad 와 Crew 의 정렬조건을 따로 줄 수 여지가 있으므로, 쿼리를 2개로 쪼갠다.
     *
     * @param memberId
     * @deprecated Use {@link #findSquadParticipantRequestsByMemberId(Long)} instead.
     */
    @Deprecated
    public List<SquadParticipantRequest> findSquadParticipantRequestsByMemberIdV2(Long memberId) {
        List<SquadParticipantDomainDto> squadParticipantDtos = jpaQueryFactory
                .from(squadParticipant)
                .innerJoin(squadParticipant.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                .innerJoin(squadParticipant.squad, squad)
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(SQUAD_CREW_MEMBER.member, SQUAD_CREATOR)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .orderBy(squadParticipant.requestAt.desc())
                .transform(groupBy(squad.id)
                        .list(new QSquadParticipantDomainDto(
                                squad.crew.id,
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                list(category.categoryType),
                                new QSimpleMemberDomainDto(
                                        SQUAD_CREATOR.id,
                                        SQUAD_CREATOR.nickname,
                                        SQUAD_CREATOR.introduce,
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

        Map<Long, SimpleCrewDomainDto> crewDtoMap = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.member, CREW_CREATOR)
                .where(crew.id.in(squadParticipantsMap.keySet()))
                .transform(groupBy(crew.id)
                        .as(new QSimpleCrewDomainDto(
                                crew.id,
                                crew.name,
                                crew.kakaoLink,
                                crew.imageUrl,
                                new QSimpleMemberDomainDto(
                                        CREW_CREATOR.id,
                                        CREW_CREATOR.nickname,
                                        CREW_CREATOR.introduce,
                                        CREW_CREATOR.mbti
                                )
                        ))
                );

        return squadParticipantsMap.keySet().stream()
                .map(crewId -> {
                    SimpleCrewDomainDto crewInfo = crewDtoMap.get(crewId);
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
