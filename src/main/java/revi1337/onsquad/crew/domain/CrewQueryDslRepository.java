package revi1337.onsquad.crew.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.crew.domain.dto.QCrewInfoDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.domain.dto.QSimpleMemberInfoDomainDto;

import java.util.*;

import static com.querydsl.core.group.GroupBy.*;
import static com.querydsl.jpa.JPAExpressions.*;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_hashtag.domain.QCrewHashtag.*;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.hashtag.domain.QHashtag.*;
import static revi1337.onsquad.image.domain.QImage.image;
import static revi1337.onsquad.member.domain.QMember.member;

@RequiredArgsConstructor
@Repository
public class CrewQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<CrewInfoDomainDto> findCrewByName(Name name) {
        Map<Name, CrewInfoDomainDto> crewInfoDomainDtoMap = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.image, image).on(crew.name.eq(name))
                .leftJoin(crew.hashtags, crewHashtag)
                .leftJoin(crewHashtag.hashtag, hashtag)
                .innerJoin(crew.member, member)
                .transform(groupBy(crew.name)
                        .as(new QCrewInfoDomainDto(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                crew.detail,
                                image.imageUrl,
                                crew.kakaoLink,
                                list(hashtag.hashtagType),
                                select(crewMember.count())
                                        .from(crewMember)
                                        .where(crewMember.crew.id.eq(crew.id)),
                                new QSimpleMemberInfoDomainDto(
                                        member.id,
                                        member.nickname
                                )
                        ))
                );

        return Optional.ofNullable(crewInfoDomainDtoMap.get(name));
    }

    public Page<CrewInfoDomainDto> findCrewsByName(String name, Pageable pageable) {
        List<CrewInfoDomainDto> transformedCrewInfos = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.image, image)
                .innerJoin(crew.member, member)
                .where(crewNameStartsWith(name))
                .orderBy(crew.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(groupBy(crew.id)
                        .list(new QCrewInfoDomainDto(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                image.imageUrl,
                                crew.kakaoLink,
                                select(crewMember.count())
                                        .from(crewMember)
                                        .where(crewMember.crew.id.eq(crew.id)),
                                new QSimpleMemberInfoDomainDto(
                                        member.id,
                                        member.nickname
                                )
                        ))
                );

        List<Long> crewIds = transformedCrewInfos.stream()
                .map(CrewInfoDomainDto::getId)
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
                .where(crew.name.value.startsWithIgnoreCase(name));

        return PageableExecutionUtils.getPage(transformedCrewInfos, pageable, countQuery::fetchOne);
    }

    private BooleanExpression crewNameStartsWith(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return crew.name.value.startsWithIgnoreCase(name);
    }
}
