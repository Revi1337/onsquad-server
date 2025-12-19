package revi1337.onsquad.crew.application.dto.response;

import revi1337.onsquad.crew.domain.result.CrewStatisticResult;

public record CrewStatisticResponse(
        long requestCnt,
        long squadCnt,
        long memberCnt
) {

    public static CrewStatisticResponse from(CrewStatisticResult crewStatisticResult) {
        return new CrewStatisticResponse(
                crewStatisticResult.requestCnt(),
                crewStatisticResult.squadCnt(),
                crewStatisticResult.memberCnt()
        );
    }
}
