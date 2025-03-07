package revi1337.onsquad.crew_member.application.dto;

import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

public record EnrolledCrewDto(
        Long id,
        String name,
        String imageUrl,
        boolean isOwner,
        SimpleMemberInfoDto owner
) {
    public static EnrolledCrewDto from(EnrolledCrewDomainDto crewDomainDto) {
        return new EnrolledCrewDto(
                crewDomainDto.id(),
                crewDomainDto.name().getValue(),
                crewDomainDto.imageUrl(),
                crewDomainDto.isOwner(),
                SimpleMemberInfoDto.from(crewDomainDto.owner())
        );
    }
}
