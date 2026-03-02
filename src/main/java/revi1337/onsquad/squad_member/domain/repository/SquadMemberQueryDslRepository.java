package revi1337.onsquad.squad_member.domain.repository;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_member.domain.entity.QSquadMember.squadMember;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.common.domain.OrderSpecifierBuilder;
import revi1337.onsquad.member.domain.model.SimpleMember;
import revi1337.onsquad.squad.domain.model.SimpleSquad;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.model.MyParticipantSquad;

@Repository
@RequiredArgsConstructor
public class SquadMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<SquadMember> fetchParticipantsBySquadId(Long squadId, Pageable pageable) {
        List<SquadMember> participants = jpaQueryFactory
                .selectFrom(squadMember)
                .innerJoin(squadMember.member, member).fetchJoin()
                .where(squadMember.squad.id.eq(squadId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(OrderSpecifierBuilder.startWith(squadMember, pageable).build())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(squadMember.id.count())
                .from(squadMember)
                .where(squadMember.squad.id.eq(squadId));

        return PageableExecutionUtils.getPage(participants, pageable, countQuery::fetchOne);
    }

    public List<MyParticipantSquad> fetchParticipantSquads(Long memberId) {
        return jpaQueryFactory
                .select(Projections.constructor(MyParticipantSquad.class,
                        squad.crew.id,
                        new CaseBuilder()
                                .when(member.id.eq(memberId))
                                .then(TRUE)
                                .otherwise(FALSE),
                        squadMember.participateAt,
                        Projections.constructor(SimpleSquad.class,
                                squad.id,
                                squad.title,
                                squad.capacity,
                                squad.remain,
                                Projections.constructor(SimpleMember.class,
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
                .orderBy(squadMember.participateAt.desc())
                .fetch();
    }
}
