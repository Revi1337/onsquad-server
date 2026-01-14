package revi1337.onsquad.crew_request.application.history;

import java.time.LocalDateTime;
import lombok.Getter;
import revi1337.onsquad.crew_request.application.notification.RequestContext.RequestAcceptedContext;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

@Getter
public class RequestAcceptHistory implements History {

    private static final String MESSAGE_FORMAT = "[%s] %s 님의 크루 합류를 수락했습니다.";

    private final Long memberId;
    private final Long crewId;
    private final HistoryType type = HistoryType.CREW_ACCEPT;
    private final String message;
    private final LocalDateTime timeStamp;

    public RequestAcceptHistory(RequestAcceptedContext context) {
        this.memberId = context.accepterId();
        this.crewId = context.crewId();
        this.message = String.format(MESSAGE_FORMAT, context.crewName(), context.requesterNickname());
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
