package revi1337.onsquad.crew.presentation.dto.response;

import revi1337.onsquad.crew.application.dto.CrewStatisticDto;

public record CrewStatisticResponse(
        long requestCnt,
        long squadCnt,
        long memberCnt
) {
    public static CrewStatisticResponse from(CrewStatisticDto crewStatisticDto) {
        return new CrewStatisticResponse(
                crewStatisticDto.requestCnt(),
                crewStatisticDto.squadCnt(),
                crewStatisticDto.memberCnt()
        );
    }
}
