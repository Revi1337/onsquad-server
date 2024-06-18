package revi1337.onsquad.crew.dto.response;

import revi1337.onsquad.crew.dto.CrewWithMemberAndImage;

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
    public static CrewWithMemberAndImageResponse from(CrewWithMemberAndImage crewWithMemberAndImage) {
        return new CrewWithMemberAndImageResponse(
                crewWithMemberAndImage.crewName().getValue() + "크루장",
                crewWithMemberAndImage.crewDetail().getValue(),
                crewWithMemberAndImage.hashTags() == null ? new ArrayList<>() : Arrays.stream(crewWithMemberAndImage.hashTags().getValue().split(",")).toList(),
                crewWithMemberAndImage.crewOwner().getValue(),
                crewWithMemberAndImage.image()
        );
    }
}
