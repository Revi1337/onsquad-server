package revi1337.onsquad.crew_request.domain.event;

public record RequestAdded(
        Long crewId,
        Long requesterId,
        Long requestId
) {

}
