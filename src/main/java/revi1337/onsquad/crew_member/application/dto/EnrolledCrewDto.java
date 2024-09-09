package revi1337.onsquad.crew_member.application.dto;

import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.member.dto.SimpleMemberInfoDto;

public record EnrolledCrewDto(
        Long crewId,
        String crewName,
        String imageUrl,
        boolean isOwner,
        SimpleMemberInfoDto crewOwner
) {
    public static EnrolledCrewDto from(EnrolledCrewDomainDto crewDomainDto) {
        return new EnrolledCrewDto(
                crewDomainDto.crewId(),
                crewDomainDto.crewName().getValue(),
                crewDomainDto.imageUrl(),
                crewDomainDto.isOwner(),
                SimpleMemberInfoDto.from(crewDomainDto.crewOwner())
        );
    }
}
