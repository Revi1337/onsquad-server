package revi1337.onsquad.crew.application.dto.response;

import revi1337.onsquad.crew.domain.result.CrewStatisticResult;

public record CrewManageResponse(
        boolean canModify,
        boolean canDelete,
        Statistic statistic
) {

    public static CrewManageResponse from(boolean canModify, boolean canDelete, CrewStatisticResult result) {
        return new CrewManageResponse(
                canModify,
                canDelete,
                new Statistic(result.requestCnt(), result.squadCnt(), result.memberCnt())
        );
    }

    public record Statistic(
            long requestCnt,
            long squadCnt,
            long memberCnt
    ) {

    }
}
