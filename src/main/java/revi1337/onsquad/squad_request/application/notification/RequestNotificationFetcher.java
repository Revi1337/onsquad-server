package revi1337.onsquad.squad_request.application.notification;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.member.domain.entity.QMember.member;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.domain.entity.QMember;
import revi1337.onsquad.squad_request.application.notification.RequestNotificationFetchResult.RequestAcceptedNotificationResult;
import revi1337.onsquad.squad_request.application.notification.RequestNotificationFetchResult.RequestAddedNotificationResult;
import revi1337.onsquad.squad_request.application.notification.RequestNotificationFetchResult.RequestRejectedNotificationResult;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service("squadRequestNotificationFetcher")
public class RequestNotificationFetcher {

    private final QMember requester = new QMember("requester");
    private final JPAQueryFactory jpaQueryFactory;

    public Optional<RequestAddedNotificationResult> fetchAddedInformation(Long squadId, Long requesterId, Long requestId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        RequestAddedNotificationResult.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        squad.id.as("squadId"),
                        squad.title.value.as("squadTitle"),
                        member.id.as("squadMemberId"),
                        member.nickname.value.as("squadMemberNickname"),
                        Expressions.as(Expressions.constant(requestId), "requestId"),
                        requester.id.as("requesterId"),
                        requester.nickname.value.as("requesterNickname")
                ))
                .from(squad)
                .innerJoin(squad.member, member)
                .innerJoin(squad.crew, crew)
                .innerJoin(requester).on(requester.id.eq(requesterId))
                .where(squad.id.eq(squadId))
                .fetchOne()
        );
    }

    public Optional<RequestAcceptedNotificationResult> fetchAcceptedInformation(Long squadId, Long accepterId, Long requesterId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        RequestAcceptedNotificationResult.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        squad.id.as("squadId"),
                        squad.title.value.as("squadTitle"),
                        Expressions.as(Expressions.constant(accepterId), "accepterId"),
                        member.id.as("requesterId")
                ))
                .from(squad)
                .innerJoin(squad.member, member)
                .innerJoin(squad.crew, crew)
                .innerJoin(requester).on(requester.id.eq(requesterId))
                .where(squad.id.eq(squadId))
                .fetchOne()
        );
    }

    public Optional<RequestRejectedNotificationResult> fetchRejectedInformation(Long squadId, Long rejecterId, Long requesterId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        RequestRejectedNotificationResult.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        squad.id.as("squadId"),
                        squad.title.value.as("squadTitle"),
                        Expressions.as(Expressions.constant(rejecterId), "rejecterId"),
                        member.id.as("requesterId")
                ))
                .from(squad)
                .innerJoin(squad.member, member)
                .innerJoin(squad.crew, crew)
                .innerJoin(requester).on(requester.id.eq(requesterId))
                .where(squad.id.eq(squadId))
                .fetchOne()
        );
    }
}
