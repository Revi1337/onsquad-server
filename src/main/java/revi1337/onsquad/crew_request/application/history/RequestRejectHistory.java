package revi1337.onsquad.crew_request.application.history;

import java.time.LocalDateTime;
import lombok.Getter;
import revi1337.onsquad.crew_request.application.notification.RequestContext.RequestRejectedContext;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

@Getter
public class RequestRejectHistory implements History {

    private final Long memberId;
    private final Long crewId;
    private final HistoryType type = HistoryType.CREW_REJECT;
    private final String message;
    private final LocalDateTime timeStamp;

    public RequestRejectHistory(RequestRejectedContext context) {
        this.memberId = context.rejecterId();
        this.crewId = context.crewId();
        this.message = type.formatMessage(context.crewName(), context.requesterNickname());
        this.timeStamp = LocalDateTime.now();
    }

    @Override
    public HistoryEntity toEntity() {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .type(type)
                .message(message)
                .recordedAt(timeStamp)
                .build();
    }
}
