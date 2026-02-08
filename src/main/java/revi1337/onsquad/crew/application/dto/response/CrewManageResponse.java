package revi1337.onsquad.crew.application.dto.response;

import revi1337.onsquad.crew.domain.model.CrewStatistic;

public record CrewManageResponse(
        CrewStates states,
        long requestCnt,
        long squadCnt,
        long memberCnt
) {

    public static CrewManageResponse from(boolean canModify, boolean canDelete, CrewStatistic result) {
        return new CrewManageResponse(
                new CrewStates(canModify, canDelete),
                result.requestCnt(),
                result.squadCnt(),
                result.memberCnt()
        );
    }
}
