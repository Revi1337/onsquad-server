package revi1337.onsquad.crew_request.application.history;

import lombok.Getter;
import revi1337.onsquad.crew_request.application.notification.RequestContext.RequestAddedContext;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

@Getter
public class RequestAddHistory implements History {

    private static final String MESSAGE_FORMAT = "[%s] 크루 합류를 요청했습니다.";

    private final Long memberId;
    private final Long crewId;
    private final HistoryType type = HistoryType.CREW_REQUEST;
    private final String message;

    public RequestAddHistory(RequestAddedContext context) {
        this.memberId = context.requesterId();
        this.crewId = context.crewId();
        this.message = String.format(MESSAGE_FORMAT, context.crewName());
    }

    @Override
    public HistoryEntity toEntity() {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .type(type)
                .message(message)
                .build();
    }
}
