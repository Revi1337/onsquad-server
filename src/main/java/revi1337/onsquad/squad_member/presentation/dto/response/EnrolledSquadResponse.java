package revi1337.onsquad.squad_member.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.presentation.dto.response.SimpleSquadInfoResponse;
import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto;

public record EnrolledSquadResponse(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberInfoResponse crewOwner,
        List<SimpleSquadInfoResponse> squads
) {
    public static EnrolledSquadResponse from(EnrolledSquadDto enrolledSquadDto) {
        return new EnrolledSquadResponse(
                enrolledSquadDto.crewId(),
                enrolledSquadDto.crewName(),
                enrolledSquadDto.imageUrl(),
                SimpleMemberInfoResponse.from(enrolledSquadDto.crewOwner()),
                enrolledSquadDto.squads().stream()
                        .map(SimpleSquadInfoResponse::from)
                        .toList()
        );
    }
}
