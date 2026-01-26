package revi1337.onsquad.hashtag.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum HashtagErrorCode implements ErrorCode {

    INVALID_HASHTAG(400, "HS001", "유효하지 않은 해시태그입니다.");

    private final int status;
    private final String code;
    private final String description;

}
