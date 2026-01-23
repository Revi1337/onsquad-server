package revi1337.onsquad.crew.domain.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_hashtag.domain.entity.QCrewHashtag.crewHashtag;
import static revi1337.onsquad.hashtag.domain.entity.QHashtag.hashtag;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpression;
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
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew.domain.result.CrewWithOwnerStateResult;
import revi1337.onsquad.crew.domain.result.QCrewResult;
import revi1337.onsquad.crew.domain.result.QCrewWithOwnerStateResult;
import revi1337.onsquad.crew.domain.result.QSimpleCrewResult;
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;

@Repository
@RequiredArgsConstructor
public class CrewQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<CrewResult> fetchCrewWithDetailById(Long id) {
        Map<Long, CrewResult> crewInfoDomainDtoMap = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.member, member)
                .leftJoin(crew.hashtags, crewHashtag)
                .leftJoin(crewHashtag.hashtag, hashtag)
                .where(crew.id.eq(id))
                .transform(groupBy(crew.id)
                        .as(new QCrewResult(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                crew.detail,
                                crew.imageUrl,
                                crew.kakaoLink,
                                list(hashtag.hashtagType),
                                crew.currentSize,
                                new QSimpleMemberResult(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        ))
                );

        return Optional.ofNullable(crewInfoDomainDtoMap.get(id));
    }

    public Page<CrewResult> fetchCrewsWithDetailByName(String name, Pageable pageable) {
        List<CrewResult> results = jpaQueryFactory
                .select(new QCrewResult(
                        crew.id,
                        crew.name,
                        crew.introduce,
                        crew.imageUrl,
                        crew.kakaoLink,
                        crew.currentSize,
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(crew)
                .innerJoin(crew.member, member)
                .where(crewNameStartsWith(name))
                .orderBy(crew.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crew.id.count())
                .from(crew)
                .where(crewNameStartsWith(name));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    public List<CrewWithOwnerStateResult> fetchCrewsWithStateByIdIn(List<Long> ids, Long currentMemberId) {
        ComparableExpression<Boolean> isCrewOwner = new CaseBuilder()
                .when(member.id.eq(currentMemberId))
                .then(TRUE)
                .otherwise(FALSE);

        return jpaQueryFactory
                .select(new QCrewWithOwnerStateResult(
                        isCrewOwner,
                        new QSimpleCrewResult(
                                crew.id,
                                crew.name.value,
                                crew.introduce.value,
                                crew.kakaoLink,
                                crew.imageUrl,
                                new QSimpleMemberResult(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                )
                        )
                ))
                .from(crew)
                .innerJoin(crew.member, member)
                .where(crew.id.in(ids))
                .orderBy(isCrewOwner.desc(), crew.createdAt.desc())
                .fetch();
    }

    private BooleanExpression crewNameStartsWith(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return crew.name.value.startsWithIgnoreCase(name);
    }
}
