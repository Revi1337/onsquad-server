package revi1337.onsquad.squad_member.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.presentation.dto.response.SimpleSquadInfoResponse;
import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto;

public record EnrolledSquadResponse(
        Long id,
        String name,
        String imageUrl,
        SimpleMemberInfoResponse owner,
        List<SimpleSquadInfoResponse> squads
) {
    public static EnrolledSquadResponse from(EnrolledSquadDto enrolledSquadDto) {
        return new EnrolledSquadResponse(
                enrolledSquadDto.id(),
                enrolledSquadDto.name(),
                enrolledSquadDto.imageUrl(),
                SimpleMemberInfoResponse.from(enrolledSquadDto.owner()),
                enrolledSquadDto.squads().stream()
                        .map(SimpleSquadInfoResponse::from)
                        .toList()
        );
    }
}
