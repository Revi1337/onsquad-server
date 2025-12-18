package revi1337.onsquad.crew_request.application.notification;

public class RequestContext {

    public record RequestAddedContext(
            Long crewId,
            String crewName,
            Long crewMemberId,
            Long requestId,
            Long requesterId,
            String requesterNickname
    ) {

    }

    public record RequestAcceptedContext(
            Long crewId,
            String crewName,
            Long accepterId,
            Long requesterId,
            String requesterNickname
    ) {

    }

    public record RequestRejectedContext(
            Long crewId,
            String crewName,
            Long rejecterId,
            Long requesterId,
            String requesterNickname
    ) {

    }
}
