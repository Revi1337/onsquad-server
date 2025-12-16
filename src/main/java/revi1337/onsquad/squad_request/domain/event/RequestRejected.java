package revi1337.onsquad.squad_request.domain.event;

public record RequestRejected(
        Long squadId,
        Long requesterId,
        Long rejecterId
) {

}
