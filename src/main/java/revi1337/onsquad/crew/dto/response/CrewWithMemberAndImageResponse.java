package revi1337.onsquad.crew.dto.response;

import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record CrewWithMemberAndImageResponse(
        String crewName,
        String crewDetail,
        List<String> hashTags,
        String crewOwner,
        byte[] image
) {
    public static CrewWithMemberAndImageResponse from(CrewWithMemberAndImageDto crewWithMemberAndImageDto) {
        return new CrewWithMemberAndImageResponse(
                crewWithMemberAndImageDto.crewName().getValue(),
                crewWithMemberAndImageDto.crewDetail().getValue(),
                crewWithMemberAndImageDto.hashTags().getValue().equals("[EMPTY]") ? new ArrayList<>() : Arrays.stream(crewWithMemberAndImageDto.hashTags().getValue().split(",")).toList(),
                crewWithMemberAndImageDto.crewOwner().getValue() + " 크루장",
                crewWithMemberAndImageDto.image()
        );
    }
}
