package revi1337.onsquad.announce.domain.repository;

import static revi1337.onsquad.announce.domain.entity.QAnnounce.announce;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.announce.domain.result.QAnnounceResult;
import revi1337.onsquad.member.domain.result.QSimpleMemberResult;

@Repository
@RequiredArgsConstructor
public class AnnounceQueryDslRepository {

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
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(announce)
                .leftJoin(announce.member, member)
                .where(announce.crew.id.eq(crewId))
                .orderBy(
                        announce.fixed.desc(),
                        announce.fixedAt.desc(),
                        announce.createdAt.desc()
                )
                .fetch();
    }

    public List<AnnounceResult> fetchAllInDefaultByCrewId(Long crewId, int limit) {
        return jpaQueryFactory
                .select(new QAnnounceResult(
                        announce.id,
                        announce.title,
                        announce.content,
                        announce.createdAt,
                        announce.fixed,
                        announce.fixedAt,
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(announce)
                .leftJoin(announce.member, member)
                .where(announce.crew.id.eq(crewId))
                .orderBy(
                        announce.fixed.desc(),
                        announce.fixedAt.desc(),
                        announce.createdAt.desc()
                )
                .limit(limit)
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
                        new QSimpleMemberResult(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(announce)
                .leftJoin(announce.member, member)
                .where(announce.id.eq(announceId), announce.crew.id.eq(crewId))
                .fetchOne()
        );
    }
}
