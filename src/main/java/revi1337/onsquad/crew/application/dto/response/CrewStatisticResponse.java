package revi1337.onsquad.crew.application.dto.response;

import revi1337.onsquad.crew.domain.result.CrewStatisticResult;

public record CrewStatisticResponse(
        boolean canModify,
        boolean canDelete,
        Data data
) {

    public static CrewStatisticResponse from(boolean canModify, boolean canDelete, CrewStatisticResult result) {
        return new CrewStatisticResponse(
                canModify,
                canDelete,
                new Data(result.requestCnt(), result.squadCnt(), result.memberCnt())
        );
    }

    public record Data(
            long requestCnt,
            long squadCnt,
            long memberCnt
    ) {

    }
}
