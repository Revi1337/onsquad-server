package revi1337.onsquad.squad_request.application.history;

import java.time.LocalDateTime;
import lombok.Getter;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.squad_request.application.notification.RequestContext.RequestAddedContext;

@Getter
public class RequestAddHistory implements History {

    private final Long memberId;
    private final Long crewId;
    private final Long squadId;
    private final HistoryType type = HistoryType.SQUAD_REQUEST;
    private final String message;
    private final LocalDateTime timeStamp;

    public RequestAddHistory(RequestAddedContext context) {
        this.memberId = context.requesterId();
        this.crewId = context.crewId();
        this.squadId = context.squadId();
        this.message = type.formatMessage(context.crewName(), context.squadTitle());
        this.timeStamp = LocalDateTime.now();
    }

    @Override
    public HistoryEntity toEntity() {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(type)
                .message(message)
                .recordedAt(timeStamp)
                .build();
    }
}
