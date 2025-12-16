package revi1337.onsquad.squad_request.domain.event;

public record RequestAccepted(
        Long squadId,
        Long requesterId,
        Long accepterId
) {

}
