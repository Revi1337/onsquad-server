package revi1337.onsquad.crew.presentation.dto.response;

import revi1337.onsquad.crew.application.dto.EnrolledCrewDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;

public record EnrolledCrewResponse(
        Long id,
        String name,
        String imageUrl,
        boolean isOwner,
        SimpleMemberResponse owner
) {
    public static EnrolledCrewResponse from(EnrolledCrewDto enrolledCrewDto) {
        return new EnrolledCrewResponse(
                enrolledCrewDto.id(),
                enrolledCrewDto.name(),
                enrolledCrewDto.imageUrl(),
                enrolledCrewDto.isOwner(),
                SimpleMemberResponse.from(enrolledCrewDto.owner())
        );
    }
}
