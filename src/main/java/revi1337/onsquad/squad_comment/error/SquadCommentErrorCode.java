package revi1337.onsquad.squad_comment.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum SquadCommentErrorCode implements ErrorCode {

    INVALID_LENGTH(400, "SC001", "댓글은 비어있거나 %d 자를 넘을 수 없습니다."),
    NOTFOUND_COMMENT(404, "SC002", "id 가 %d 인 댓글을 찾을 수 없습니다."),
    NOT_PARENT(400, "SC003", "id 가 %d 인 댓글은 부모댓글이 아닙니다."),
    NOTFOUND_CREW_COMMENT(404, "SC004", "id 가 %d 인 크루 게시글에 id 가 %d 인 댓글을 찾을 수 없습니다."),
    NON_MATCH_SQUAD_ID(400, "SC001", "댓글이 속한 스쿼드 id 와 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String description;

}
