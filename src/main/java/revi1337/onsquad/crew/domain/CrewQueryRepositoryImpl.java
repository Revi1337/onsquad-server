package revi1337.onsquad.crew.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.Optional;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.image.domain.QImage.*;

@RequiredArgsConstructor
public class CrewQueryRepositoryImpl implements CrewQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Crew> findByNameWithImage(Name name) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(crew)
                        .from(crew)
                        .innerJoin(crew.image, image).fetchJoin()
                        .where(crew.name.eq(name))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Crew> findByNameWithCrewMembers(Name name) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(crew)
                        .leftJoin(crew.crewMembers).fetchJoin()
                        .where(crew.name.eq(name))
                        .fetchOne()
        );
    }
}
