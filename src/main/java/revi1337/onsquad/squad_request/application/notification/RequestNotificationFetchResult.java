package revi1337.onsquad.squad_request.application.notification;

public class RequestNotificationFetchResult {

    public record RequestAddedNotificationResult(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long squadMemberId,
            String squadMemberNickname,
            Long requestId,
            Long requesterId,
            String requesterNickname
    ) {

    }

    public record RequestAcceptedNotificationResult(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long accepterId,
            Long requesterId
    ) {

    }

    public record RequestRejectedNotificationResult(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long rejecterId,
            Long requesterId
    ) {

    }
}
