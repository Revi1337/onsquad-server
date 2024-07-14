package revi1337.onsquad.squad.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.squad.domain.vo.Title;

import java.util.Optional;

import static revi1337.onsquad.squad.domain.QSquad.*;

@RequiredArgsConstructor
public class SquadQueryRepositoryImpl implements SquadQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Squad> findSquadWithMembersById(Long squadId, Title squadTitle) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(squad)
                        .leftJoin(squad.squadMembers).fetchJoin()
                        .where(
                                squad.id.eq(squadId),
                                squad.title.eq(squadTitle)
                        )
                        .fetchOne()
        );
    }
}
