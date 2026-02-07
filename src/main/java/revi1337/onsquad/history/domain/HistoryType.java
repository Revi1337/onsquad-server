package revi1337.onsquad.history.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HistoryType {

    CREW_CREATE("[%s] 크루를 생성했습니다."),
    CREW_REQUEST("[%s] 크루 합류를 요청했습니다."),
    CREW_ACCEPT("[%s] %s 님의 크루 합류를 수락했습니다."),
    CREW_REJECT("[%s] %s 님의 크루 합류를 거절헀습니다."),
    CREW_CANCEL("[%s] 크루 합류 요청을 취소했습니다."),

    SQUAD_CREATE("[%s | %s] 스쿼드를 생성했습니다."),
    SQUAD_REQUEST("[%s | %s] 스쿼드 합류를 요청했습니다."),
    SQUAD_ACCEPT("[%s | %s] %s 님의 스쿼드 합류를 수락했습니다."),
    SQUAD_REJECT("[%s | %s] %s 님의 스쿼드 합류를 거절헀습니다."),
    SQUAD_CANCEL("[%s | %s] 스쿼드 합류 요청을 취소했습니다."),
    SQUAD_COMMENT("[%s | %s] 스쿼드에 댓글을 남겼습니다."),
    SQUAD_COMMENT_REPLY("[%s | %s] 스쿼드에 대댓글을 남겼습니다.");

    private final String messageTemplate;

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
