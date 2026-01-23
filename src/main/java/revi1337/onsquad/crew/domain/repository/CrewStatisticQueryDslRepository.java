package revi1337.onsquad.crew.domain.repository;

import static com.querydsl.jpa.JPAExpressions.select;
import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.crew_request.domain.entity.QCrewRequest.crewRequest;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.result.CrewStatisticResult;
import revi1337.onsquad.crew.domain.result.QCrewStatisticResult;

@Repository
@RequiredArgsConstructor
public class CrewStatisticQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CrewStatisticResult getStatisticById(Long crewId) {
        return jpaQueryFactory
                .select(new QCrewStatisticResult(
                        select(crewRequest.id.count())
                                .from(crewRequest)
                                .where(crewRequest.crew.id.eq(crewId)),
                        select(squad.id.count())
                                .from(squad)
                                .where(squad.crew.id.eq(crewId)),
                        crew.currentSize
                ))
                .from(crew)
                .where(crew.id.eq(crewId))
                .fetchOne();
    }
}
