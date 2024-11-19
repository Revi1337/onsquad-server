package revi1337.onsquad.crew.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.image.domain.QImage.*;

@RequiredArgsConstructor
public class CrewQueryRepositoryImpl implements CrewQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Crew> findByIdWithImage(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(crew)
                        .from(crew)
                        .innerJoin(crew.image, image).fetchJoin()
                        .where(crew.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Crew> findByIdWithCrewMembers(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(crew)
                        .leftJoin(crew.crewMembers).fetchJoin()
                        .where(crew.id.eq(id))
                        .fetchOne()
        );
    }
}
