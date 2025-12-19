package revi1337.onsquad.crew.application.dto.response;

import revi1337.onsquad.crew.domain.result.EnrolledCrewResult;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

public record EnrolledCrewResponse(
        Long id,
        String name,
        String imageUrl,
        boolean isOwner,
        SimpleMemberDto owner
) {

    public static EnrolledCrewResponse from(EnrolledCrewResult crewDomainDto) {
        return new EnrolledCrewResponse(
                crewDomainDto.id(),
                crewDomainDto.name().getValue(),
                crewDomainDto.imageUrl(),
                crewDomainDto.isOwner(),
                SimpleMemberDto.from(crewDomainDto.owner())
        );
    }
}
