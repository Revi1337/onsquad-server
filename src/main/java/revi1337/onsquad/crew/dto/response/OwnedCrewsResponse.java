package revi1337.onsquad.crew.dto.response;

import revi1337.onsquad.crew.dto.CrewDto;

public record OwnedCrewsResponse(
        Long id,
        String name,
        String introduce,
        String detail,
        String hashTags,
        String kakaoLink
) {
    public static OwnedCrewsResponse from(CrewDto crewDto) {
        return new OwnedCrewsResponse(
                crewDto.getId(),
                crewDto.getName().getValue(),
                crewDto.getIntroduce().getValue(),
                crewDto.getDetail().getValue(),
                crewDto.getHashTags().getValue(),
                crewDto.getKakaoLink()
        );
    }
}
