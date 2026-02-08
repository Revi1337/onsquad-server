package revi1337.onsquad.crew.domain.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.jpa.JPAExpressions.select;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_hashtag.domain.entity.QCrewHashtag.crewHashtag;
import static revi1337.onsquad.crew_request.domain.entity.QCrewRequest.crewRequest;
import static revi1337.onsquad.hashtag.domain.entity.QHashtag.hashtag;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;

import com.querydsl.core.types.Projections;
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
import revi1337.onsquad.crew.domain.model.CrewDetail;
import revi1337.onsquad.crew.domain.model.CrewStatistic;
import revi1337.onsquad.crew.domain.model.CrewWithOwnerState;
import revi1337.onsquad.crew.domain.model.SimpleCrew;
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;

@Repository
@RequiredArgsConstructor
public class CrewQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<CrewDetail> fetchCrewWithDetailById(Long id) {
        Map<Long, CrewDetail> crewDetailMap = jpaQueryFactory
                .from(crew)
                .innerJoin(crew.member, member)
                .leftJoin(crew.hashtags, crewHashtag)
                .leftJoin(crewHashtag.hashtag, hashtag)
                .where(crew.id.eq(id))
                .transform(groupBy(crew.id)
                        .as(Projections.constructor(CrewDetail.class,
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

        return Optional.ofNullable(crewDetailMap.get(id));
    }

    public Page<CrewDetail> fetchCrewsWithDetailByName(String name, Pageable pageable) {
        List<CrewDetail> results = jpaQueryFactory
                .select(Projections.constructor(CrewDetail.class,
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

    public List<CrewWithOwnerState> fetchCrewsWithStateByIdIn(List<Long> ids, Long currentMemberId) {
        ComparableExpression<Boolean> isCrewOwner = new CaseBuilder()
                .when(member.id.eq(currentMemberId))
                .then(TRUE)
                .otherwise(FALSE);

        return jpaQueryFactory
                .select(Projections.constructor(CrewWithOwnerState.class,
                        isCrewOwner,
                        Projections.constructor(SimpleCrew.class,
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

    public CrewStatistic getStatisticById(Long crewId) {
        return jpaQueryFactory
                .select(Projections.constructor(CrewStatistic.class,
                        select(crewRequest.id.count())
                                .from(crewRequest)
                                .where(crewRequest.crew.id.eq(crewId)),
                        select(squad.id.count())
                                .from(squad)
                                .where(squad.crew.id.eq(crewId)),
                        crew.currentSize
                ))
                .from(crew)
                .where(crew.id.eq(crewId))
                .fetchOne();
    }

    private BooleanExpression crewNameStartsWith(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return crew.name.value.startsWithIgnoreCase(name);
    }
}
