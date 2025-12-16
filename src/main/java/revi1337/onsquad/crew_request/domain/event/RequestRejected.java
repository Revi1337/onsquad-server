package revi1337.onsquad.crew_request.domain.event;

public record RequestRejected(
        Long crewId,
        Long rejecterId,
        Long requesterId
) {

}
