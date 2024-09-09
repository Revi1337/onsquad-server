package revi1337.onsquad.crew_member.presentation.dto.response;

import revi1337.onsquad.crew_member.application.dto.EnrolledCrewDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record OwnedCrewResponse(
        Long crewId,
        String crewName,
        String crewDetail,
        String imageUrl,
        boolean isOwner,
        List<String> hashTags,
        SimpleMemberInfoResponse crewOwner
) {
    public static OwnedCrewResponse from(EnrolledCrewDto enrolledCrewDto) {
        return new OwnedCrewResponse(
                enrolledCrewDto.crewId(),
                enrolledCrewDto.crewName(),
                enrolledCrewDto.crewDetail(),
                enrolledCrewDto.imageUrl(),
                enrolledCrewDto.isOwner(),
                enrolledCrewDto.hashTags() == null ? new ArrayList<>() : Arrays.stream(enrolledCrewDto.hashTags().split(",")).toList(),
                SimpleMemberInfoResponse.from(enrolledCrewDto.crewOwner())
        );
    }
}
