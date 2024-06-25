package revi1337.onsquad.comment.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    INVALID_LENGTH(400, "CM001", "댓글은 비어있거나 %d 자를 넘을 수 없습니다.");

    private final int status;
    private final String code;
    private final String description;

}
