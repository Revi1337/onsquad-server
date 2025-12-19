package revi1337.onsquad.squad_request.domain.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static revi1337.onsquad.category.domain.entity.QCategory.category;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
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
import revi1337.onsquad.crew.domain.result.QSimpleCrewResult;
import revi1337.onsquad.crew.domain.result.SimpleCrewResult;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;
import revi1337.onsquad.member.domain.entity.QMember;
import revi1337.onsquad.squad_request.domain.result.MySquadRequestResult;
import revi1337.onsquad.squad_request.domain.result.QMySquadRequestResult;
import revi1337.onsquad.squad_request.domain.result.QMySquadRequestResult_SquadWithParticipant;
import revi1337.onsquad.squad_request.domain.result.QMySquadRequestResult_SquadWithParticipant_RequestParticipantDomainDto;
import revi1337.onsquad.squad_request.domain.result.QSquadRequestResult;
import revi1337.onsquad.squad_request.domain.result.QSquadRequestWithSquadResult;
import revi1337.onsquad.squad_request.domain.result.QSquadRequestWithSquadResult_RequestParticipantDomainDto;
import revi1337.onsquad.squad_request.domain.result.SquadRequestResult;
import revi1337.onsquad.squad_request.domain.result.SquadRequestWithSquadAndCrewResult;
import revi1337.onsquad.squad_request.domain.result.SquadRequestWithSquadResult;

@RequiredArgsConstructor
@Repository
public class SquadRequestQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;
    public final QMember crewCreator = new QMember("crew-creator");
    public final QMember squadCreator = new QMember("squad-creator");

    /**
     * @deprecated Use {@link #findSquadParticipantRequestsByMemberIdV2(Long)} (Long)} instead
     */
    @Deprecated
    public List<SquadRequestWithSquadAndCrewResult> findSquadParticipantRequestsByMemberId(Long memberId) {
        Map<Long, List<MySquadRequestResult>> results = jpaQueryFactory
                .from(squadRequest)
                .innerJoin(squadRequest.squad, squad).on(squadRequest.member.id.eq(memberId))
                .innerJoin(squad.member, squadCreator)
                .innerJoin(squad.crew, crew)
                .innerJoin(crew.member, crewCreator)
                .orderBy(squadRequest.requestAt.desc())
                .transform(groupBy(crew.id).as(
                        list(new QMySquadRequestResult(
                                crew.id,
                                crew.name,
                                crew.imageUrl,
                                new QSimpleMemberDomainDto(
                                        crewCreator.id,
                                        crewCreator.nickname,
                                        crewCreator.introduce,
                                        crewCreator.mbti
                                ),
                                list(new QMySquadRequestResult_SquadWithParticipant(
                                        squad.id,
                                        squad.title,
                                        squad.capacity,
                                        squad.remain,
                                        new QSimpleMemberDomainDto(
                                                squadCreator.id,
                                                squadCreator.nickname,
                                                squadCreator.introduce,
                                                squadCreator.mbti
                                        ),
                                        new QMySquadRequestResult_SquadWithParticipant_RequestParticipantDomainDto(
                                                squadRequest.id,
                                                squadRequest.requestAt
                                        )
                                ))
                        ))
                ));

        return List.of();
    }

    /**
     * Since Squad and Crew may have separate sorting criteria, the query is split into two.
     */
    public List<SquadRequestWithSquadAndCrewResult> findSquadParticipantRequestsByMemberIdV2(Long memberId) {
        List<SquadRequestWithSquadResult> squadParticipantDtos = jpaQueryFactory
                .from(squadRequest)
                .innerJoin(squadRequest.squad, squad).on(squadRequest.member.id.eq(memberId))
                .innerJoin(squad.member, squadCreator)
                .leftJoin(squad.categories, squadCategory)
                .leftJoin(squadCategory.category, category)
                .orderBy(squadRequest.requestAt.desc())
                .transform(groupBy(squad.id)
                        .list(new QSquadRequestWithSquadResult(
                                squad.crew.id,
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                list(category.categoryType),
                                new QSimpleMemberDomainDto(
                                        squadCreator.id,
                                        squadCreator.nickname,
                                        squadCreator.introduce,
                                        squadCreator.mbti
                                ),
                                new QSquadRequestWithSquadResult_RequestParticipantDomainDto(
                                        squadRequest.id,
                                        squadRequest.requestAt
                                )
                        ))
                );

        Map<Long, List<SquadRequestWithSquadResult>> squadParticipantsMap = squadParticipantDtos.stream()
                .collect(Collectors.groupingBy(SquadRequestWithSquadResult::crewId));

        Map<Long, SimpleCrewResult> crewDtoMap = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.member, crewCreator)
                .where(crew.id.in(squadParticipantsMap.keySet()))
                .transform(groupBy(crew.id)
                        .as(new QSimpleCrewResult(
                                crew.id,
                                crew.name,
                                crew.kakaoLink,
                                crew.imageUrl,
                                new QSimpleMemberDomainDto(
                                        crewCreator.id,
                                        crewCreator.nickname,
                                        crewCreator.introduce,
                                        crewCreator.mbti
                                )
                        ))
                );

        return squadParticipantsMap.keySet().stream()
                .map(crewId -> {
                    SimpleCrewResult crewInfo = crewDtoMap.get(crewId);
                    List<SquadRequestWithSquadResult> squadsInCrew = squadParticipantsMap.get(crewId);
                    return new SquadRequestWithSquadAndCrewResult(
                            crewInfo.id(),
                            crewInfo.name(),
                            crewInfo.imageUrl(),
                            crewInfo.owner(),
                            squadsInCrew
                    );
                })
                .toList();
    }

    public Page<SquadRequestResult> fetchAllBySquadId(Long squadId, Pageable pageable) {
        List<SquadRequestResult> results = jpaQueryFactory
                .select(new QSquadRequestResult(
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
                .innerJoin(squadRequest.member, member).on(squadRequest.squad.id.eq(squadId))
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
