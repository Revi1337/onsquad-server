package revi1337.onsquad.crew_request.domain.model;

public class CrewRequestContext {

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
