package revi1337.onsquad.crew.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum CrewErrorCode implements ErrorCode {

    INVALID_DETAIL_LENGTH(400, "CR001", "크루 상세정보의 길이는 %d 자 이상 %d 자 입니다."),
    INVALID_HASHTAGS_SIZE(400, "CR002", "해시태그의 최대 개수는 %d 개 입니다."),
    INVALID_INTRODUCE_LENGTH(400, "CR003", "크루 소개는 %d 자 이상 %d 자 입니다."),
    INVALID_NAME_LENGTH(400, "CR004", "크루명의 길이는 %d 자 이상 %d 자 입니다.");

    private final int status;
    private final String code;
    private final String description;

}
