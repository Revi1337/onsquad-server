package revi1337.onsquad.crew.dto.response;

import revi1337.onsquad.crew.dto.CrewDto;

public record CrewResponse(
        Long id,
        String name,
        String introduce,
        String detail,
        String hashTags,
        String kakaoLink,
        String crewOwner
) {
    public static CrewResponse from(CrewDto crewDto) {
        return new CrewResponse(
                crewDto.getId(),
                crewDto.getName().getValue(),
                crewDto.getIntroduce().getValue(),
                crewDto.getDetail().getValue(),
                crewDto.getHashTags().getValue(),
                crewDto.getKakaoLink(),
                crewDto.getMemberDto().getNickname().getValue()
        );
    }
}
