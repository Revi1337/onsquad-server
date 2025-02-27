package revi1337.onsquad.crew.application.dto;

import revi1337.onsquad.crew.domain.dto.CrewStatisticDomainDto;

public record CrewStatisticDto(
        long requestCnt,
        long squadCnt,
        long memberCnt
) {
    public static CrewStatisticDto from(CrewStatisticDomainDto crewStatisticDomainDto) {
        return new CrewStatisticDto(
                crewStatisticDomainDto.requestCnt(),
                crewStatisticDomainDto.squadCnt(),
                crewStatisticDomainDto.memberCnt()
        );
    }
}
