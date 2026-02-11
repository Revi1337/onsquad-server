package revi1337.onsquad.crew_request.domain.repository;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestAcceptedContext;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestAddedContext;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestRejectedContext;
import revi1337.onsquad.member.domain.entity.QMember;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewRequestContextReader {

    private final QMember requester = new QMember("requester");
    private final JPAQueryFactory jpaQueryFactory;

    public Optional<RequestAddedContext> readAddedContext(Long crewId, Long requesterId, Long requestId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        RequestAddedContext.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        crew.member.id.as("crewMemberId"),
                        Expressions.as(Expressions.constant(requestId), "requestId"),
                        requester.id.as("requesterId"),
                        requester.nickname.value.as("requesterNickname")
                ))
                .from(crew)
                .innerJoin(requester).on(requester.id.eq(requesterId))
                .where(crew.id.eq(crewId))
                .fetchOne()
        );
    }

    public Optional<RequestAcceptedContext> readAcceptedContext(Long crewId, Long accepterId, Long requesterId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        RequestAcceptedContext.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        Expressions.as(Expressions.constant(accepterId), "accepterId"),
                        requester.id.as("requesterId"),
                        requester.nickname.value.as("requesterNickname")
                ))
                .from(crew)
                .innerJoin(requester).on(requester.id.eq(requesterId))
                .where(crew.id.eq(crewId))
                .fetchOne()
        );
    }

    public Optional<RequestRejectedContext> readRejectedContext(Long crewId, Long rejecterId, Long requesterId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        RequestRejectedContext.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        Expressions.as(Expressions.constant(rejecterId), "rejecterId"),
                        requester.id.as("requesterId"),
                        requester.nickname.value.as("requesterNickname")
                ))
                .from(crew)
                .innerJoin(requester).on(requester.id.eq(requesterId))
                .where(crew.id.eq(crewId))
                .fetchOne()
        );
    }
}
