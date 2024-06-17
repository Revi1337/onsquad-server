package revi1337.onsquad.crew.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.Optional;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.image.domain.QImage.*;
import static revi1337.onsquad.member.domain.QMember.*;

@RequiredArgsConstructor
public class CrewQueryRepositoryImpl implements CrewQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Crew> findCrewByName(Name name) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(crew)
                        .innerJoin(crew.image, image).fetchJoin()
                        .innerJoin(crew.member, member).fetchJoin()
                        .where(crew.name.eq(name))
                        .fetchOne()
        );
    }
}
