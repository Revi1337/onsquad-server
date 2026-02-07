package revi1337.onsquad.notification.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NotificationDetail {

    CONNECT(null),
    HEARTBEAT(null),

    CREW_REQUEST("%s 님이 크루 합류를 요청하였습니다."),
    CREW_ACCEPT("크루에 합류하였습니다. 지금 활동을 시작해보세요!"),
    CREW_REJECT("크루 합류가 거절되었습니다."),

    SQUAD_REQUEST("%s 님이 스쿼드 합류를 요청하였습니다."),
    SQUAD_ACCEPT("스쿼드에 합류하였습니다. 지금 활동을 시작해보세요!"),
    SQUAD_REJECT("스쿼드 합류가 거절되었습니다."),

    COMMENT("%s 님이 스쿼드에 댓글을 남겼습니다."),
    COMMENT_REPLY("%s 님이 대댓글을 남겼습니다.");

    private final String messageTemplate;

    public String formatMessage(Object... args) {
        if (messageTemplate == null) {
            return "";
        }

        return String.format(messageTemplate, args);
    }
}
