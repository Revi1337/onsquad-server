package revi1337.onsquad.squad_comment.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum SquadCommentErrorCode implements ErrorCode {

    INVALID_LENGTH(400, "SC001", "댓글은 비어있거나 %d 자를 넘을 수 없습니다."),
    NOTFOUND_COMMENT(404, "SC002", "댓글을 찾을 수 없습니다."),
    NOT_PARENT(400, "SC003", "대댓글은 부모댓글에서만 등록할 수 있습니다."),
    DELETED(400, "SC004", "삭제된 댓글입니다."),
    MISMATCH_SQUAD_REFERENCE(400, "SC005", "댓글이 속한 스쿼드가 일치하지 않습니다."),
    MISMATCH_WRITER(403, "SC006", "댓글 작성자 정보가 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String description;

}
