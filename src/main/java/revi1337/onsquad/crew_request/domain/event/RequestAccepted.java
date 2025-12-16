package revi1337.onsquad.crew_request.domain.event;

public record RequestAccepted(
        Long crewId,
        Long accepterId,
        Long requesterId
) {

}
