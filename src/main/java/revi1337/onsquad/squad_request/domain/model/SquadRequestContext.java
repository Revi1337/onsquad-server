package revi1337.onsquad.squad_request.domain.model;

public class SquadRequestContext {

    public record RequestAddedContext(
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

    public record RequestAcceptedContext(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long accepterId,
            Long requesterId,
            String requesterNickname
    ) {

    }

    public record RequestRejectedContext(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long rejecterId,
            Long requesterId,
            String requesterNickname
    ) {

    }
}
