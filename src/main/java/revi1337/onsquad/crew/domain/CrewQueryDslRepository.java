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
import revi1337.onsquad.crew.domain.dto.QCrewDomainDto;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<CrewDomainDto> findCrewById(Long id) {
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
                                        member.mbti
                                )
                        ))
                );

        return Optional.ofNullable(crewInfoDomainDtoMap.get(id));
    }

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
                                        member.mbti
                                )
                        ))
                );

        List<Long> crewIds = transformedCrewInfos.stream()
                .map(CrewDomainDto::getId)
                .toList();

        Map<Long, List<HashtagType>> transformedHashtags = jpaQueryFactory
                .from(crewHashtag)
                .innerJoin(crewHashtag.hashtag, hashtag)
                .where(crewHashtag.crew.id.in(crewIds))
                .transform(groupBy(crewHashtag.crew.id).as(list(hashtag.hashtagType)));

        transformedCrewInfos.forEach(crewInfo -> {
            List<HashtagType> crewHashtags = transformedHashtags.get(crewInfo.getId());
            crewInfo.setHashtagTypes(crewHashtags);
        });

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crew.count())
                .from(crew)
                .where(crewNameStartsWith(name));

        return PageableExecutionUtils.getPage(transformedCrewInfos, pageable, countQuery::fetchOne);
    }

    private BooleanExpression crewNameStartsWith(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return crew.name.value.startsWithIgnoreCase(name);
    }
}
