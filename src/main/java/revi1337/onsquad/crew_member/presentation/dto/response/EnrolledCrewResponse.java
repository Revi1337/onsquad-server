package revi1337.onsquad.crew_member.presentation.dto.response;

import revi1337.onsquad.crew_member.application.dto.EnrolledCrewDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;

public record EnrolledCrewResponse(
        Long crewId,
        String crewName,
        String imageUrl,
        boolean isOwner,
        SimpleMemberInfoResponse crewOwner
) {
    public static EnrolledCrewResponse from(EnrolledCrewDto enrolledCrewDto) {
        return new EnrolledCrewResponse(
                enrolledCrewDto.crewId(),
                enrolledCrewDto.crewName(),
                enrolledCrewDto.imageUrl(),
                enrolledCrewDto.isOwner(),
                SimpleMemberInfoResponse.from(enrolledCrewDto.crewOwner())
        );
    }
}
