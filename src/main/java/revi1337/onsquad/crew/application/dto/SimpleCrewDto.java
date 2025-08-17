package revi1337.onsquad.crew.application.dto;

import revi1337.onsquad.crew.domain.dto.SimpleCrewDomainDto;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

public record SimpleCrewDto(
        Long id,
        String name,
        String introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberDto owner
) {
    public static SimpleCrewDto from(SimpleCrewDomainDto simpleCrewDomainDto) {
        return new SimpleCrewDto(
                simpleCrewDomainDto.id(),
                simpleCrewDomainDto.name().getValue(),
                simpleCrewDomainDto.introduce() != null ? simpleCrewDomainDto.introduce().getValue() : "",
                simpleCrewDomainDto.kakaoLink() != null ? simpleCrewDomainDto.kakaoLink() : "",
                simpleCrewDomainDto.imageUrl() != null ? simpleCrewDomainDto.imageUrl() : "",
                SimpleMemberDto.from(simpleCrewDomainDto.owner())
        );
    }
}
