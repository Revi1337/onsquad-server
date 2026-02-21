package revi1337.onsquad.announce.domain.repository;

import static revi1337.onsquad.announce.domain.entity.QAnnounce.announce;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.entity.Announce;

@Repository
@RequiredArgsConstructor
public class AnnounceQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Announce> fetchAllByCrewId(Long crewId) {
        return jpaQueryFactory
                .selectFrom(announce)
                .leftJoin(announce.member, member).fetchJoin()
                .where(announce.crew.id.eq(crewId))
                .orderBy(
                        announce.pinned.desc(),
                        announce.pinnedAt.desc(),
                        announce.createdAt.desc()
                )
                .fetch();
    }

    public List<Announce> fetchAllInDefaultByCrewId(Long crewId, int limit) {
        return jpaQueryFactory
                .selectFrom(announce)
                .leftJoin(announce.member, member).fetchJoin()
                .where(announce.crew.id.eq(crewId))
                .orderBy(
                        announce.pinned.desc(),
                        announce.pinnedAt.desc(),
                        announce.createdAt.desc()
                )
                .limit(limit)
                .fetch();
    }

    public Optional<Announce> fetchByIdAndCrewId(Long id, Long crewId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(announce)
                .from(announce)
                .leftJoin(announce.member, member).fetchJoin()
                .where(announce.id.eq(id), announce.crew.id.eq(crewId))
                .fetchOne()
        );
    }
}
