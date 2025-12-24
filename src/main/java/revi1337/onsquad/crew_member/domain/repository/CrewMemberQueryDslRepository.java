package revi1337.onsquad.crew_member.domain.repository;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;

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
import revi1337.onsquad.crew.domain.result.QSimpleCrewResult;
import revi1337.onsquad.crew_member.domain.result.CrewMemberResult;
import revi1337.onsquad.crew_member.domain.result.CrewMemberWithCountResult;
import revi1337.onsquad.crew_member.domain.result.MyParticipantCrewResult;
import revi1337.onsquad.crew_member.domain.result.QCrewMemberResult;
import revi1337.onsquad.crew_member.domain.result.QCrewMemberWithCountResult;
import revi1337.onsquad.crew_member.domain.result.QMyParticipantCrewResult;
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;

@RequiredArgsConstructor
@Repository
public class CrewMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<CrewMemberWithCountResult> fetchParticipantsWithCountByCrewId(Long crewId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QCrewMemberWithCountResult(
                        crew.currentSize,
                        new QCrewMemberResult(
                                new QSimpleMemberResult(
                                        member.id,
                                        member.nickname,
                                        member.introduce,
                                        member.mbti
                                ),
                                crewMember.requestAt
                        )
                ))
                .from(crewMember)
                .innerJoin(crewMember.member, member)
                .innerJoin(crewMember.crew, crew)
                .where(crewMember.crew.id.eq(crewId))
                .orderBy(crewMember.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<MyParticipantCrewResult> fetchParticipantCrews(Long memberId) {
        ComparableExpression<Boolean> isCrewOwner = new CaseBuilder()
                .when(member.id.eq(memberId))
                .then(TRUE)
                .otherwise(FALSE);

        return jpaQueryFactory
                .select(new QMyParticipantCrewResult(
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
                .from(crewMember)
                .innerJoin(crewMember.crew, crew)
                .innerJoin(crew.member, member)
                .where(crewMember.member.id.eq(memberId))
                .orderBy(isCrewOwner.desc(), crewMember.requestAt.desc())
                .fetch();
    }

    /**
     * @see #fetchParticipantsWithCountByCrewId(Long, Pageable)
     * @deprecated
     */
    public List<CrewMemberResult> fetchParticipantsByCrewIdLegacyV2(Long crewId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QCrewMemberResult(
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        ),
                        crewMember.requestAt
                ))
                .from(crewMember)
                .innerJoin(crewMember.member, member)
                .where(crewMember.crew.id.eq(crewId))
                .orderBy(crewMember.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    /**
     * @see #fetchParticipantsByCrewIdLegacyV2(Long, Pageable)
     * @deprecated
     */
    @Deprecated
    public Page<CrewMemberResult> fetchParticipantsByCrewIdLegacy(Long crewId, Pageable pageable) {
        List<CrewMemberResult> results = jpaQueryFactory
                .select(new QCrewMemberResult(
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        ),
                        crewMember.requestAt
                ))
                .from(crewMember)
                .innerJoin(crewMember.member, member)
                .where(crewMember.crew.id.eq(crewId))
                .orderBy(crewMember.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crewMember.id.count())
                .from(crewMember)
                .where(crewMember.crew.id.eq(crewId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
