package revi1337.onsquad.category.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {

    INVALID_CATEGORY(400, "CT001", "유효하지 않은 카테고리입니다.");

    private final int status;
    private final String code;
    private final String description;

}
