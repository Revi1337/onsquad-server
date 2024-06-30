package revi1337.onsquad.crew.dto.response;

import revi1337.onsquad.crew.dto.OwnedCrewsDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record OwnedCrewsResponse(
        String crewName,
        String crewDetail,
        List<String> hashTags,
        String crewOwner
) {
    public static OwnedCrewsResponse from(OwnedCrewsDto ownedCrewsDto) {
        return new OwnedCrewsResponse(
                ownedCrewsDto.crewName().getValue(),
                ownedCrewsDto.crewDetail().getValue(),
                ownedCrewsDto.hashTags() == null ? new ArrayList<>() : Arrays.stream(ownedCrewsDto.hashTags().getValue().split(",")).toList(),
                ownedCrewsDto.crewOwner().getValue()
        );
    }
}
