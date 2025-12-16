package revi1337.onsquad.crew_request.application.notification;

public class RequestNotificationFetchResult {

    public record RequestAddedNotificationResult(
            Long crewId,
            String crewName,
            Long crewMemberId,
            Long requestId,
            Long requesterId,
            String requesterNickname
    ) {

    }

    public record RequestAcceptedNotificationResult(
            Long crewId,
            String crewName,
            Long accepterId,
            Long requesterId
    ) {

    }

    public record RequestRejectedNotificationResult(
            Long crewId,
            String crewName,
            Long rejecterId,
            Long requesterId
    ) {

    }
}
