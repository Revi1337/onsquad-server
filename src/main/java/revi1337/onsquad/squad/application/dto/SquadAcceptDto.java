package revi1337.onsquad.squad.application.dto;

public record SquadAcceptDto(
        String requestCrewName,
        Long squadId,
        Long requestMemberId
) {
}
