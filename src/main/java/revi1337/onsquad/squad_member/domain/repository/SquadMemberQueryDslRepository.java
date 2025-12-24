package revi1337.onsquad.squad_member.domain.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.category.domain.entity.QCategory.category;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_category.domain.entity.QSquadCategory.squadCategory;
import static revi1337.onsquad.squad_member.domain.entity.QSquadMember.squadMember;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.domain.entity.QMember;
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;
import revi1337.onsquad.squad.domain.result.QSimpleSquadResult;
import revi1337.onsquad.squad_member.domain.result.EnrolledSquadResult;
import revi1337.onsquad.squad_member.domain.result.EnrolledSquadResult.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad_member.domain.result.MyParticipantSquadResult;
import revi1337.onsquad.squad_member.domain.result.QEnrolledSquadResult;
import revi1337.onsquad.squad_member.domain.result.QEnrolledSquadResult_SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad_member.domain.result.QMyParticipantSquadResult;
import revi1337.onsquad.squad_member.domain.result.QSquadInMembersResult;
import revi1337.onsquad.squad_member.domain.result.QSquadMemberResult;
import revi1337.onsquad.squad_member.domain.result.SquadInMembersResult;
import revi1337.onsquad.squad_member.domain.result.SquadMemberResult;

@RequiredArgsConstructor
@Repository
public class SquadMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMember crewCreator = new QMember("crew-creator");
    private final QMember squadCreator = new QMember("squad-creator");

    public List<SquadMemberResult> fetchParticipantsBySquadId(Long squadId) {
        return jpaQueryFactory
                .select(new QSquadMemberResult(
                        squadMember.requestAt,
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(squadMember)
                .innerJoin(squadMember.member, member)
                .where(squadMember.squad.id.eq(squadId))
                .fetch();
    }

    public List<MyParticipantSquadResult> fetchParticipantSquads(Long memberId) {
        return jpaQueryFactory
                .select(new QMyParticipantSquadResult(
                        squad.crew.id,
                        new CaseBuilder()
                                .when(member.id.eq(memberId))
                                .then(TRUE)
                                .otherwise(FALSE),
                        new QSimpleSquadResult(
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new QSimpleMemberResult(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        )
                ))
                .from(squadMember)
                .innerJoin(squadMember.squad, squad)
                .innerJoin(squad.member, member)
                .where(squadMember.member.id.eq(memberId))
                .orderBy(squadMember.requestAt.desc())
                .fetch();
    }

    /**
     * @see #fetchParticipantSquads(Long)
     * @deprecated
     */
    @Deprecated
    public List<EnrolledSquadResult> findEnrolledSquadsLegacy(Long memberId) {
        List<SimpleSquadInfoDomainDto> squads = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.member, member).on(member.id.eq(memberId))
                .innerJoin(squadMember.squad, squad)
                .leftJoin(squad.categories, squadCategory)
                .innerJoin(squadCategory.category, category)
                .orderBy(squadMember.requestAt.desc())
                .transform(groupBy(squad.id)
                        .list(new QEnrolledSquadResult_SimpleSquadInfoDomainDto(
                                squad.crew.id,
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new CaseBuilder()
                                        .when(member.id.eq(memberId))
                                        .then(true)
                                        .otherwise(false),
                                list(category.categoryType),
                                new QSimpleMemberResult(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        )));

        Map<Long, List<SimpleSquadInfoDomainDto>> transformed = squads.stream()
                .collect(Collectors.groupingBy(SimpleSquadInfoDomainDto::crewId));

        Map<Long, EnrolledSquadResult> crewMap = jpaQueryFactory
                .from(crewMember)
                .innerJoin(crewMember.crew, crew).on(crew.id.in(transformed.keySet()), crewMember.member.id.eq(memberId))
                .innerJoin(crew.member, crewCreator)
                .transform(groupBy(crew.id)
                        .as(new QEnrolledSquadResult(
                                crew.id,
                                crew.name,
                                crew.imageUrl,
                                new QSimpleMemberResult(
                                        crewCreator.id,
                                        crewCreator.nickname,
                                        crewCreator.introduce,
                                        crewCreator.mbti
                                )
                        )));

        return transformed.keySet().stream()
                .map(crewId -> {
                    EnrolledSquadResult temp = crewMap.get(crewId);
                    return new EnrolledSquadResult(
                            temp.id(),
                            temp.name(),
                            temp.imageUrl(),
                            temp.owner(),
                            transformed.get(crewId)
                    );
                }).toList();
    }

    @Deprecated
    public List<SquadMemberResult> fetchParticipantsBySquadIdLegacy(Long squadId, Long currentMemberId) {
        return jpaQueryFactory
                .select(new QSquadMemberResult(
                        new CaseBuilder()
                                .when(member.id.eq(currentMemberId))
                                .then(TRUE)
                                .otherwise(FALSE),
                        squadMember.requestAt,
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(squadMember)
                .innerJoin(squadMember.member, member)
                .where(squadMember.squad.id.eq(squadId))
                .fetch();
    }

    @Deprecated
    public SquadInMembersResult fetchAllWithSquad(Long memberId, Long squadId) {
        SquadInMembersResult result = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.squad, squad).on(squad.id.eq(squadId))
                .innerJoin(squad.member, squadCreator)
                .innerJoin(squadMember.member, member)
                .orderBy(squadMember.requestAt.asc())
                .transform(groupBy(squad.id)
                        .as(new QSquadInMembersResult(
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new CaseBuilder()
                                        .when(squadCreator.id.eq(memberId))
                                        .then(TRUE)
                                        .otherwise(FALSE),
                                new QSimpleMemberResult(
                                        squadCreator.id,
                                        squadCreator.nickname,
                                        squadCreator.introduce,
                                        squadCreator.mbti
                                ),
                                list(new QSquadMemberResult(
                                        squadMember.requestAt,
                                        new QSimpleMemberResult(
                                                member.id,
                                                member.nickname,
                                                member.introduce,
                                                member.mbti
                                        )
                                ))
                        ))).get(squadId);

        List<CategoryType> categoryTypes = jpaQueryFactory
                .select(category.categoryType)
                .from(squadCategory)
                .innerJoin(squadCategory.category, category).on(squadCategory.squad.id.eq(squadId))
                .fetch();

        result.registerCategories(categoryTypes);

        return result;
    }

    @Deprecated
    public Optional<SquadInMembersResult> fetchAllWithSquad(Long memberId, Long crewId, Long squadId) {
        Map<Long, SquadInMembersResult> result = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.squad, squad).on(squad.crew.id.eq(crewId), squad.id.eq(squadId))
                .innerJoin(squad.member, squadCreator)
                .innerJoin(squadMember.member, member)
                .transform(groupBy(squad.id)
                        .as(new QSquadInMembersResult(
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new CaseBuilder()
                                        .when(squadCreator.id.eq(memberId))
                                        .then(TRUE)
                                        .otherwise(FALSE),
                                new QSimpleMemberResult(
                                        squadCreator.id,
                                        squadCreator.nickname,
                                        squadCreator.introduce,
                                        squadCreator.mbti
                                ),
                                list(new QSquadMemberResult(
                                        squadMember.requestAt,
                                        new QSimpleMemberResult(
                                                member.id,
                                                member.nickname,
                                                member.introduce,
                                                member.mbti
                                        )
                                ))
                        )));

        if (result.get(squadId) != null) {
            List<CategoryType> categories = jpaQueryFactory
                    .select(category.categoryType)
                    .from(squadCategory)
                    .innerJoin(squadCategory.category, category)
                    .where(squadCategory.squad.id.eq(squadId))
                    .fetch();

            SquadInMembersResult squadMembersWithSquadDomainDto = result.get(squadId);
            squadMembersWithSquadDomainDto.registerCategories(categories);
            return Optional.of(squadMembersWithSquadDomainDto);
        }

        return Optional.empty();
    }
}
