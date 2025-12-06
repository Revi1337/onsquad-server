package revi1337.onsquad.crew.application.dto;

import revi1337.onsquad.crew.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

public record EnrolledCrewDto(
        Long id,
        String name,
        String imageUrl,
        boolean isOwner,
        SimpleMemberDto owner
) {

    public static EnrolledCrewDto from(EnrolledCrewDomainDto crewDomainDto) {
        return new EnrolledCrewDto(
                crewDomainDto.id(),
                crewDomainDto.name().getValue(),
                crewDomainDto.imageUrl(),
                crewDomainDto.isOwner(),
                SimpleMemberDto.from(crewDomainDto.owner())
        );
    }
}
