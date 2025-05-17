package revi1337.onsquad.squad_member.domain;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.category.domain.QCategory.category;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
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
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.crew_member.domain.QCrewMember;
import revi1337.onsquad.member.domain.QMember;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad_member.domain.dto.QEnrolledSquadDomainDto;
import revi1337.onsquad.squad_member.domain.dto.QEnrolledSquadDomainDto_SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad_member.domain.dto.QSquadInMembersDomainDto;
import revi1337.onsquad.squad_member.domain.dto.QSquadMemberDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadInMembersDomainDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class SquadMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QSquadMember NEW_SQUAD_MEMBER = new QSquadMember("newSquadMember");
    private final QCrewMember SQUAD_CREW_MEMBER = new QCrewMember("squadCrewMember");
    private final QMember CREW_CREATOR = new QMember("crewCreator");
    private final QMember SQUAD_CREATOR = new QMember("squadCreator");

    /**
     * Squad 와 Crew 의 정렬조건을 따로 줄 수 여지가 있으므로, 쿼리를 2개로 분리한다.
     */
    public List<EnrolledSquadDomainDto> findEnrolledSquads(Long memberId) {
        List<SimpleSquadInfoDomainDto> squads = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.crewMember, crewMember).on(crewMember.member.id.eq(memberId))
                .innerJoin(crewMember.member, member)
                .innerJoin(squadMember.squad, squad)
                .leftJoin(squad.categories, squadCategory)
                .innerJoin(squadCategory.category, category)
                .orderBy(squadMember.requestAt.desc())
                .transform(groupBy(squad.id)
                        .list(new QEnrolledSquadDomainDto_SimpleSquadInfoDomainDto(
                                squad.crew.id,
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new CaseBuilder()
                                        .when(crewMember.member.id.eq(memberId))
                                        .then(true)
                                        .otherwise(false),
                                list(category.categoryType),
                                new QSimpleMemberInfoDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.mbti
                                )
                        )));

        Map<Long, List<SimpleSquadInfoDomainDto>> transformed = squads.stream()
                .collect(Collectors.groupingBy(SimpleSquadInfoDomainDto::crewId));

        Map<Long, EnrolledSquadDomainDto> crewMap = jpaQueryFactory
                .from(crewMember)
                .innerJoin(crewMember.crew, crew)
                .on(
                        crew.id.in(transformed.keySet()),
                        crewMember.member.id.eq(memberId)
                )
                .innerJoin(crew.member, CREW_CREATOR)
                .transform(groupBy(crew.id)
                        .as(new QEnrolledSquadDomainDto(
                                crew.id,
                                crew.name,
                                crew.imageUrl,
                                new QSimpleMemberInfoDomainDto(
                                        CREW_CREATOR.id,
                                        CREW_CREATOR.nickname,
                                        CREW_CREATOR.mbti
                                )
                        )));

        return transformed.keySet().stream()
                .map(crewId -> {
                    EnrolledSquadDomainDto temp = crewMap.get(crewId);
                    return new EnrolledSquadDomainDto(
                            temp.id(),
                            temp.name(),
                            temp.imageUrl(),
                            temp.owner(),
                            transformed.get(crewId)
                    );
                }).toList();
    }

    public List<SquadMemberDomainDto> fetchAllBySquadId(Long squadId) {
        return jpaQueryFactory
                .select(new QSquadMemberDomainDto(
                        squadMember.requestAt,
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname,
                                member.mbti
                        )
                ))
                .from(squadMember)
                .innerJoin(squadMember.crewMember, crewMember).on(squadMember.squad.id.eq(squadId))
                .innerJoin(crewMember.member, member)
                .fetch();
    }

    @Deprecated
    public SquadInMembersDomainDto fetchAllWithSquad(Long crewMemberId, Long squadId) {
        SquadInMembersDomainDto result = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.squad, squad).on(squad.id.eq(squadId))
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(SQUAD_CREW_MEMBER.member, SQUAD_CREATOR)
                .innerJoin(squadMember.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .orderBy(squadMember.requestAt.asc())
                .transform(groupBy(squad.id)
                        .as(new QSquadInMembersDomainDto(
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new CaseBuilder()
                                        .when(squad.crewMember.id.eq(crewMemberId))
                                        .then(TRUE)
                                        .otherwise(FALSE),
                                new QSimpleMemberInfoDomainDto(
                                        SQUAD_CREATOR.id,
                                        SQUAD_CREATOR.nickname,
                                        SQUAD_CREATOR.mbti
                                ),
                                list(new QSquadMemberDomainDto(
                                        squadMember.requestAt,
                                        new QSimpleMemberInfoDomainDto(
                                                member.id,
                                                member.nickname,
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
    public Optional<SquadInMembersDomainDto> fetchAllWithSquad(Long memberId, Long crewId, Long squadId) {
        Map<Long, SquadInMembersDomainDto> result = jpaQueryFactory
                .from(squadMember)
                .innerJoin(squadMember.squad, squad).on(squad.crew.id.eq(crewId), squad.id.eq(squadId))
                .innerJoin(squad.crewMember, SQUAD_CREW_MEMBER)
                .innerJoin(SQUAD_CREW_MEMBER.member, SQUAD_CREATOR)
                .innerJoin(squadMember.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .transform(groupBy(squad.id)
                        .as(new QSquadInMembersDomainDto(
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                new CaseBuilder()
                                        .when(SQUAD_CREW_MEMBER.member.id.eq(memberId))
                                        .then(TRUE)
                                        .otherwise(FALSE),
                                new QSimpleMemberInfoDomainDto(
                                        SQUAD_CREATOR.id,
                                        SQUAD_CREATOR.nickname,
                                        SQUAD_CREATOR.mbti
                                ),
                                list(new QSquadMemberDomainDto(
                                        squadMember.requestAt,
                                        new QSimpleMemberInfoDomainDto(
                                                member.id,
                                                member.nickname,
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

            SquadInMembersDomainDto squadMembersWithSquadDomainDto = result.get(squadId);
            squadMembersWithSquadDomainDto.registerCategories(categories);
            return Optional.of(squadMembersWithSquadDomainDto);
        }

        return Optional.empty();
    }
}
