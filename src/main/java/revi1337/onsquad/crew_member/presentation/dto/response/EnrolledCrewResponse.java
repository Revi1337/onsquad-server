package revi1337.onsquad.crew_member.presentation.dto.response;

import revi1337.onsquad.crew_member.application.dto.EnrolledCrewDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;

public record EnrolledCrewResponse(
        Long id,
        String name,
        String imageUrl,
        boolean isOwner,
        SimpleMemberInfoResponse owner
) {
    public static EnrolledCrewResponse from(EnrolledCrewDto enrolledCrewDto) {
        return new EnrolledCrewResponse(
                enrolledCrewDto.id(),
                enrolledCrewDto.name(),
                enrolledCrewDto.imageUrl(),
                enrolledCrewDto.isOwner(),
                SimpleMemberInfoResponse.from(enrolledCrewDto.owner())
        );
    }
}
