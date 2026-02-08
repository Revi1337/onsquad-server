package revi1337.onsquad.announce.domain.repository;

import static revi1337.onsquad.announce.domain.entity.QAnnounce.announce;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.model.AnnounceDetail;
import revi1337.onsquad.member.domain.model.SimpleMember;

@Repository
@RequiredArgsConstructor
public class AnnounceQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<AnnounceDetail> fetchAllByCrewId(Long crewId) {
        return jpaQueryFactory
                .select(Projections.constructor(AnnounceDetail.class,
                        announce.id,
                        announce.title,
                        announce.content,
                        announce.createdAt,
                        announce.pinned,
                        announce.pinnedAt,
                        Projections.constructor(SimpleMember.class,
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
                        announce.pinned.desc(),
                        announce.pinnedAt.desc(),
                        announce.createdAt.desc()
                )
                .fetch();
    }

    public List<AnnounceDetail> fetchAllInDefaultByCrewId(Long crewId, int limit) {
        return jpaQueryFactory
                .select(Projections.constructor(AnnounceDetail.class,
                        announce.id,
                        announce.title,
                        announce.content,
                        announce.createdAt,
                        announce.pinned,
                        announce.pinnedAt,
                        Projections.constructor(SimpleMember.class,
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
                        announce.pinned.desc(),
                        announce.pinnedAt.desc(),
                        announce.createdAt.desc()
                )
                .limit(limit)
                .fetch();
    }

    public Optional<AnnounceDetail> fetchByIdAndCrewId(Long id, Long crewId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(AnnounceDetail.class,
                        announce.id,
                        announce.title,
                        announce.content,
                        announce.createdAt,
                        announce.pinned,
                        announce.pinnedAt,
                        Projections.constructor(SimpleMember.class,
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        )
                ))
                .from(announce)
                .leftJoin(announce.member, member)
                .where(announce.id.eq(id), announce.crew.id.eq(crewId))
                .fetchOne()
        );
    }
}
