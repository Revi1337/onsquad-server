package revi1337.onsquad.history.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record HistoryResponse(
        Long crewId,
        Long squadId,
        Long squadCommentId,
        HistoryType type,
        String message,
        LocalDateTime recordedAt
) {

    public static HistoryResponse from(HistoryEntity history) {
        return new HistoryResponse(
                history.getCrewId(),
                history.getSquadId(),
                history.getSquadCommentId(),
                history.getType(),
                history.getMessage(),
                history.getRecordedAt()
        );
    }
}
