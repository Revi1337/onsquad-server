package revi1337.onsquad.squad_request.domain.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static revi1337.onsquad.category.domain.entity.QCategory.category;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.entity.QSquadCategory.squadCategory;
import static revi1337.onsquad.squad_request.domain.entity.QSquadRequest.squadRequest;

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
import revi1337.onsquad.squad_request.domain.dto.MySquadRequestDomainDto;
import revi1337.onsquad.squad_request.domain.dto.QMySquadRequestDomainDto;
import revi1337.onsquad.squad_request.domain.dto.QMySquadRequestDomainDto_SquadWithParticipant;
import revi1337.onsquad.squad_request.domain.dto.QMySquadRequestDomainDto_SquadWithParticipant_RequestParticipantDomainDto;
import revi1337.onsquad.squad_request.domain.dto.QSquadRequestDomainDto;
import revi1337.onsquad.squad_request.domain.dto.QSquadRequestWithSquadDomainDto;
import revi1337.onsquad.squad_request.domain.dto.QSquadRequestWithSquadDomainDto_RequestParticipantDomainDto;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestDomainDto;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestWithSquadAndCrewDomainDto;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestWithSquadDomainDto;

@RequiredArgsConstructor
@Repository
public class SquadRequestQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QCrewMember SQUAD_CREW_MEMBER = new QCrewMember("squadCrewMember");
    private final QMember CREW_CREATOR = new QMember("crewCreator");
    private final QMember SQUAD_CREATOR = new QMember("squadCreator");

    /**
     * @deprecated Use {@link #findSquadParticipantRequestsByMemberIdV2(Long)} (Long)} instead.
     */
    @Deprecated
    public List<SquadRequestWithSquadAndCrewDomainDto> findSquadParticipantRequestsByMemberId(Long memberId) {
        Map<Long, List<MySquadRequestDomainDto>> transformed = jpaQueryFactory
                .from(squadRequest)
                .innerJoin(squadRequest.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                .innerJoin(squadRequest.squad, squad)
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(SQUAD_CREW_MEMBER.member, SQUAD_CREATOR)
                .innerJoin(squad.crew, crew)
                .innerJoin(crew.member, member)
                .orderBy(squadRequest.requestAt.desc())
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
                                                squadRequest.id,
                                                squadRequest.requestAt
                                        )
                                ))
                        ))
                ));

        return List.of();
    }

    /**
     * Squad 와 Crew 의 정렬조건을 따로 줄 수 여지가 있으므로, 쿼리를 2개로 쪼갠다.
     */
    public List<SquadRequestWithSquadAndCrewDomainDto> findSquadParticipantRequestsByMemberIdV2(Long memberId) {
        List<SquadRequestWithSquadDomainDto> squadParticipantDtos = jpaQueryFactory
                .from(squadRequest)
                .innerJoin(squadRequest.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                .innerJoin(squadRequest.squad, squad)
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(SQUAD_CREW_MEMBER.member, SQUAD_CREATOR)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .orderBy(squadRequest.requestAt.desc())
                .transform(groupBy(squad.id)
                        .list(new QSquadRequestWithSquadDomainDto(
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
                                new QSquadRequestWithSquadDomainDto_RequestParticipantDomainDto(
                                        squadRequest.id,
                                        squadRequest.requestAt
                                )
                        ))
                );

        Map<Long, List<SquadRequestWithSquadDomainDto>> squadParticipantsMap = squadParticipantDtos.stream()
                .collect(Collectors.groupingBy(SquadRequestWithSquadDomainDto::crewId));

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
                    List<SquadRequestWithSquadDomainDto> squadsInCrew = squadParticipantsMap.get(crewId);
                    return new SquadRequestWithSquadAndCrewDomainDto(
                            crewInfo.id(),
                            crewInfo.name(),
                            crewInfo.imageUrl(),
                            crewInfo.owner(),
                            squadsInCrew
                    );
                })
                .toList();
    }

    public Page<SquadRequestDomainDto> fetchAllBySquadId(Long squadId, Pageable pageable) {
        List<SquadRequestDomainDto> results = jpaQueryFactory
                .select(new QSquadRequestDomainDto(
                        squadRequest.id,
                        squadRequest.requestAt,
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(squadRequest)
                .innerJoin(squadRequest.crewMember, crewMember).on(squadRequest.squad.id.eq(squadId))
                .innerJoin(crewMember.member, member)
                .orderBy(squadRequest.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squadRequest.id.count())
                .from(squadRequest)
                .where(squadRequest.squad.id.eq(squadId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
