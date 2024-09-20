package revi1337.onsquad.crew_comment.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CrewCommentErrorCode implements ErrorCode {

    INVALID_LENGTH(400, "CM001", "댓글은 비어있거나 %d 자를 넘을 수 없습니다."),

    NOTFOUND_COMMENT(404, "CM002", "id 가 %d 인 댓글을 찾을 수 없습니다."),
    NOT_PARENT(400, "CM003", "id 가 %d 인 댓글은 부모댓글이 아닙니다."),
    NOTFOUND_CREW_COMMENT(404, "CM004", "id 가 %d 인 스쿼드 게시글에 id 가 %d 인 댓글을 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String description;

}
