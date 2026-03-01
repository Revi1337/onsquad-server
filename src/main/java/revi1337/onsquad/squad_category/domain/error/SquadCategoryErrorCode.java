package revi1337.onsquad.squad_category.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum SquadCategoryErrorCode implements ErrorCode {

    INVALID_CATEGORY_SIZE(400, "SCT001", "카테고리는 최대 5개까지 선택할 수 있습니다.");

    private final int status;
    private final String code;
    private final String description;

}
