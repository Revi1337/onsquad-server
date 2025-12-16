package revi1337.onsquad.crew_request.application.notification;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_request.application.notification.RequestNotificationFetchResult.RequestAcceptedNotificationResult;
import revi1337.onsquad.crew_request.application.notification.RequestNotificationFetchResult.RequestAddedNotificationResult;
import revi1337.onsquad.crew_request.application.notification.RequestNotificationFetchResult.RequestRejectedNotificationResult;
import revi1337.onsquad.member.domain.entity.QMember;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service("crewRequestNotificationFetcher")
public class RequestNotificationFetcher {

    private final QMember requester = new QMember("requester");
    private final JPAQueryFactory jpaQueryFactory;

    public Optional<RequestAddedNotificationResult> fetchAddedInformation(Long crewId, Long requesterId, Long requestId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        RequestAddedNotificationResult.class,
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

    public Optional<RequestAcceptedNotificationResult> fetchAcceptedInformation(Long crewId, Long accepterId, Long requesterId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        RequestAcceptedNotificationResult.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        Expressions.as(Expressions.constant(accepterId), "accepterId"),
                        member.id.as("requesterId")
                ))
                .from(crew)
                .innerJoin(requester).on(requester.id.eq(requesterId))
                .where(crew.id.eq(crewId))
                .fetchOne()
        );
    }

    public Optional<RequestRejectedNotificationResult> fetchRejectedInformation(Long crewId, Long rejecterId, Long requesterId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        RequestRejectedNotificationResult.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        Expressions.as(Expressions.constant(rejecterId), "rejecterId"),
                        member.id.as("requesterId")
                ))
                .from(crew)
                .innerJoin(requester).on(requester.id.eq(requesterId))
                .where(crew.id.eq(crewId))
                .fetchOne()
        );
    }
}
