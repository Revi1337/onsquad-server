package revi1337.onsquad.announce.domain.repository;

import static revi1337.onsquad.announce.domain.entity.QAnnounce.announce;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.announce.domain.result.QAnnounceResult;
import revi1337.onsquad.crew_member.domain.result.QSimpleCrewMemberResult;

@RequiredArgsConstructor
@Repository
public class AnnounceQueryDslRepository {

    private static final Long DEFAULT_FETCH_SIZE = 4L;

    private final JPAQueryFactory jpaQueryFactory;

    public List<AnnounceResult> fetchAllByCrewId(Long crewId) {
        return jpaQueryFactory
                .select(new QAnnounceResult(
                        announce.id,
                        announce.title,
                        announce.content,
                        announce.createdAt,
                        announce.fixed,
                        announce.fixedAt,
                        new QSimpleCrewMemberResult(
                                member.id,
                                member.nickname,
                                crewMember.role
                        )
                ))
                .from(announce)
                .innerJoin(announce.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .where(announce.crew.id.eq(crewId))
                .orderBy(
                        announce.fixed.desc(),
                        announce.fixedAt.desc(),
                        announce.createdAt.desc()
                )
                .fetch();
    }

    public List<AnnounceResult> fetchAllInDefaultByCrewId(Long crewId) {
        return jpaQueryFactory
                .select(new QAnnounceResult(
                        announce.id,
                        announce.title,
                        announce.content,
                        announce.createdAt,
                        announce.fixed,
                        announce.fixedAt,
                        new QSimpleCrewMemberResult(
                                member.id,
                                member.nickname,
                                crewMember.role
                        )
                ))
                .from(announce)
                .innerJoin(announce.crewMember, crewMember).on(announce.crew.id.eq(crewId))
                .innerJoin(crewMember.member, member)
                .orderBy(
                        announce.fixed.desc(),
                        announce.fixedAt.desc(),
                        announce.createdAt.desc()
                )
                .limit(DEFAULT_FETCH_SIZE)
                .fetch();
    }

    public Optional<AnnounceResult> fetchByCrewIdAndId(Long crewId, Long announceId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(new QAnnounceResult(
                        announce.id,
                        announce.title,
                        announce.content,
                        announce.createdAt,
                        announce.fixed,
                        announce.fixedAt,
                        new QSimpleCrewMemberResult(
                                member.id,
                                member.nickname,
                                crewMember.role
                        )
                ))
                .from(announce)
                .innerJoin(announce.crewMember, crewMember)
                .on(
                        announce.id.eq(announceId),
                        announce.crew.id.eq(crewId)
                )
                .innerJoin(crewMember.member, member)
                .fetchOne()
        );
    }
}
