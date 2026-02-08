package revi1337.onsquad.crew_member.domain.repository;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.model.SimpleCrew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.result.MyParticipantCrewResult;
import revi1337.onsquad.crew_member.domain.result.QMyParticipantCrewResult;
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;

@Repository
@RequiredArgsConstructor
public class CrewMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<CrewMember> fetchParticipantsByCrewId(Long crewId, Pageable pageable) {
        List<CrewMember> participants = jpaQueryFactory
                .selectFrom(crewMember)
                .innerJoin(crewMember.member, member).fetchJoin()
                .where(crewMember.crew.id.eq(crewId))
                .orderBy(crewMember.participateAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crew.currentSize)
                .from(crew)
                .where(crew.id.eq(crewId));

        return PageableExecutionUtils.getPage(participants, pageable, countQuery::fetchOne);
    }

    public List<MyParticipantCrewResult> fetchParticipantCrews(Long memberId) {
        ComparableExpression<Boolean> isCrewOwner = new CaseBuilder()
                .when(member.id.eq(memberId))
                .then(TRUE)
                .otherwise(FALSE);

        return jpaQueryFactory
                .select(new QMyParticipantCrewResult(
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
                .from(crewMember)
                .innerJoin(crewMember.crew, crew)
                .innerJoin(crew.member, member)
                .where(crewMember.member.id.eq(memberId))
                .orderBy(isCrewOwner.desc(), crewMember.participateAt.desc())
                .fetch();
    }
}
