package revi1337.onsquad.squad_request.domain.event;

public record RequestAdded(
        Long squadId,
        Long requesterId,
        Long requestId
) {

}
