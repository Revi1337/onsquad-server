package revi1337.onsquad.crew.domain;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.jpa.JPAExpressions.select;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_hashtag.domain.QCrewHashtag.crewHashtag;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.hashtag.domain.QHashtag.hashtag;
import static revi1337.onsquad.member.domain.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.crew.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew.domain.dto.QCrewDomainDto;
import revi1337.onsquad.crew.domain.dto.QEnrolledCrewDomainDto;
import revi1337.onsquad.crew_member.domain.vo.CrewRole;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.domain.QMember;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<CrewDomainDto> findCrewById(Long id) { // TODO 성능 문제 있을거임. 고쳐야 함.
        Map<Long, CrewDomainDto> crewInfoDomainDtoMap = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.member, member).on(crew.id.eq(id))
                .leftJoin(crew.hashtags, crewHashtag)
                .leftJoin(crewHashtag.hashtag, hashtag)
                .transform(groupBy(crew.id)
                        .as(new QCrewDomainDto(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                crew.detail,
                                crew.imageUrl,
                                crew.kakaoLink,
                                list(hashtag.hashtagType),
                                select(crewMember.count())
                                        .from(crewMember)
                                        .where(crewMember.crew.id.eq(crew.id)),
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        ))
                );

        return Optional.ofNullable(crewInfoDomainDtoMap.get(id));
    }

    public Page<CrewDomainDto> fetchCrewsByName(String name, Pageable pageable) {
        List<CrewDomainDto> transformedCrewInfos = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.member, member)
                .where(crewNameStartsWith(name))
                .orderBy(crew.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(groupBy(crew.id)
                        .list(new QCrewDomainDto(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                crew.imageUrl,
                                crew.kakaoLink,
                                select(crewMember.count())
                                        .from(crewMember)
                                        .where(crewMember.crew.id.eq(crew.id)),
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        ))
                );

        Map<Long, List<HashtagType>> transformedHashtags = fetchHashtags(extractCrewIds(transformedCrewInfos));
        linkHashtags(transformedCrewInfos, transformedHashtags);

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crew.count())
                .from(crew)
                .where(crewNameStartsWith(name));

        return PageableExecutionUtils.getPage(transformedCrewInfos, pageable, countQuery::fetchOne);
    }

    public Page<CrewDomainDto> fetchCrewsByMemberId(Long memberId, Pageable pageable) {
        List<CrewDomainDto> transformedCrewInfos = jpaQueryFactory
                .from(crew)
                .leftJoin(crew.crewMembers, crewMember).on(crewMember.member.id.eq(memberId), crewMember.role.eq(CrewRole.OWNER))
                .innerJoin(crewMember.member, member)
                .orderBy(crew.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(groupBy(crew.id)
                        .list(new QCrewDomainDto(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                crew.imageUrl,
                                crew.kakaoLink,
                                select(crewMember.count())
                                        .from(crewMember)
                                        .where(crewMember.crew.id.eq(crew.id)),
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        ))
                );

        Map<Long, List<HashtagType>> transformedHashtags = fetchHashtags(extractCrewIds(transformedCrewInfos));
        linkHashtags(transformedCrewInfos, transformedHashtags);

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crew.count())
                .from(crew)
                .leftJoin(crew.crewMembers, crewMember).on(crewMember.member.id.eq(memberId), crewMember.role.eq(CrewRole.OWNER))
                .innerJoin(crewMember.member, member);

        return PageableExecutionUtils.getPage(transformedCrewInfos, pageable, countQuery::fetchOne);
    }

    public List<EnrolledCrewDomainDto> fetchEnrolledCrewsByMemberId(Long memberId) {
        QMember crewCreator = new QMember("crewCreator");
        return jpaQueryFactory
                .select(new QEnrolledCrewDomainDto(
                        crew.id,
                        crew.name,
                        crew.imageUrl,
                        new CaseBuilder()
                                .when(crewCreator.id.eq(memberId))
                                .then(true)
                                .otherwise(false),
                        new QSimpleMemberDomainDto(
                                crewCreator.id,
                                crewCreator.nickname,
                                crewCreator.introduce,
                                crewCreator.mbti
                        )
                ))
                .from(crew)
                .innerJoin(crew.member, crewCreator)
                .leftJoin(crew.crewMembers, crewMember).on(crewMember.member.id.eq(memberId))
                .orderBy(crewMember.requestAt.desc())
                .fetch();
    }

    /**
     * Deprecated method. See also {@link #fetchEnrolledCrewsByMemberId(Long)}.
     */
    @Deprecated
    public List<EnrolledCrewDomainDto> fetchEnrolledCrewsByMemberIdV2(Long memberId) {
        QMember crewCreator = new QMember("crewCreator");

        BooleanExpression isCrewOwner = new CaseBuilder()
                .when(crewCreator.id.eq(memberId))
                .then(true)
                .otherwise(false);

        return jpaQueryFactory
                .select(new QEnrolledCrewDomainDto(
                        crew.id,
                        crew.name,
                        crew.imageUrl,
                        isCrewOwner,
                        new QSimpleMemberDomainDto(
                                crewCreator.id,
                                crewCreator.nickname,
                                crewCreator.introduce,
                                crewCreator.mbti
                        )
                ))
                .from(crewMember)
                .innerJoin(crewMember.crew, crew).on(crewMember.member.id.eq(memberId))
                .innerJoin(crew.member, crewCreator)
                .orderBy(
                        crewMember.requestAt.desc(),
                        isCrewOwner.desc()
                )
                .fetch();
    }

    @Deprecated
    public Optional<CrewDomainDto> findCrewWithJoinStatusByIdAndMemberId(Long id) {
        Map<Long, CrewDomainDto> crewInfoDomainDtoMap = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.member, member).on(crew.id.eq(id))
                .leftJoin(crew.hashtags, crewHashtag)
                .leftJoin(crewHashtag.hashtag, hashtag)
                .transform(groupBy(crew.id)
                        .as(new QCrewDomainDto(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                crew.detail,
                                crew.imageUrl,
                                crew.kakaoLink,
                                list(hashtag.hashtagType),
                                select(crewMember.count())
                                        .from(crewMember)
                                        .where(crewMember.crew.id.eq(crew.id)),
                                new QSimpleMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        ))
                );

        return Optional.ofNullable(crewInfoDomainDtoMap.get(id));
    }

    private Map<Long, List<HashtagType>> fetchHashtags(List<Long> crewIds) {
        return jpaQueryFactory
                .from(crewHashtag)
                .innerJoin(crewHashtag.hashtag, hashtag)
                .where(crewHashtag.crew.id.in(crewIds))
                .transform(groupBy(crewHashtag.crew.id).as(list(hashtag.hashtagType)));
    }

    private BooleanExpression crewNameStartsWith(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return crew.name.value.startsWithIgnoreCase(name);
    }

    private List<Long> extractCrewIds(List<CrewDomainDto> transformedCrewInfos) {
        return transformedCrewInfos.stream()
                .map(CrewDomainDto::getId)
                .toList();
    }

    private void linkHashtags(List<CrewDomainDto> crewDomainDtos, Map<Long, List<HashtagType>> hashtags) {
        crewDomainDtos.forEach(crewInfo -> {
            List<HashtagType> crewHashtags = hashtags.get(crewInfo.getId());
            if (crewHashtags != null) {
                crewInfo.addHashtagTypes(crewHashtags);
            }
        });
    }
}
