package revi1337.onsquad.crew.application.dto;

import revi1337.onsquad.crew.domain.dto.SimpleCrewInfoDomainDto;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

public record SimpleCrewInfoDto(
        Long id,
        String name,
        String introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberInfoDto owner
) {
    public static SimpleCrewInfoDto from(SimpleCrewInfoDomainDto simpleCrewInfoDomainDto) {
        return new SimpleCrewInfoDto(
                simpleCrewInfoDomainDto.id(),
                simpleCrewInfoDomainDto.name().getValue(),
                simpleCrewInfoDomainDto.introduce() != null ? simpleCrewInfoDomainDto.introduce().getValue() : null,
                simpleCrewInfoDomainDto.kakaoLink(),
                simpleCrewInfoDomainDto.imageUrl(),
                SimpleMemberInfoDto.from(simpleCrewInfoDomainDto.owner())
        );
    }
}
