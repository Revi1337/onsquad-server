package revi1337.onsquad.squad_member.domain.repository;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_member.domain.entity.QSquadMember.squadMember;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;
import revi1337.onsquad.squad.domain.result.QSimpleSquadResult;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.result.MyParticipantSquadResult;
import revi1337.onsquad.squad_member.domain.result.QMyParticipantSquadResult;

@Repository
@RequiredArgsConstructor
public class SquadMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<SquadMember> fetchParticipantsBySquadId(Long squadId) {
        return jpaQueryFactory
                .selectFrom(squadMember)
                .innerJoin(squadMember.member, member).fetchJoin()
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
}
