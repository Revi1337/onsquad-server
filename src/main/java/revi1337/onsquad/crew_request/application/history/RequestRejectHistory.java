package revi1337.onsquad.crew_request.application.history;

import lombok.Getter;
import revi1337.onsquad.crew_request.application.notification.RequestContext.RequestRejectedContext;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

@Getter
public class RequestRejectHistory implements History {

    private static final String MESSAGE_FORMAT = "[%s] %s 님의 크루 합류를 거절헀습니다.";

    private final Long memberId;
    private final Long crewId;
    private final HistoryType type = HistoryType.CREW_REJECT;
    private final String message;

    public RequestRejectHistory(RequestRejectedContext context) {
        this.memberId = context.rejecterId();
        this.crewId = context.crewId();
        this.message = String.format(MESSAGE_FORMAT, context.crewName(), context.requesterNickname());
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
