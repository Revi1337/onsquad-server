package revi1337.onsquad.squad_request.application.history;

import lombok.Getter;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.squad_request.application.notification.RequestContext.RequestAddedContext;

@Getter
public class RequestAddHistory implements History {

    private static final String MESSAGE_FORMAT = "[%s | %s] 스쿼드 합류를 요청했습니다.";

    private final Long memberId;
    private final Long crewId;
    private final Long squadId;
    private final HistoryType type = HistoryType.SQUAD_REQUEST;
    private final String message;

    public RequestAddHistory(RequestAddedContext context) {
        this.memberId = context.requesterId();
        this.crewId = context.crewId();
        this.squadId = context.squadId();
        this.message = String.format(MESSAGE_FORMAT, context.crewName(), context.squadTitle());
    }

    @Override
    public HistoryEntity toEntity() {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(type)
                .message(message)
                .build();
    }
}
