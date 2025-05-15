package revi1337.onsquad.announce.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum AnnounceErrorCode implements ErrorCode {

    NOT_FOUND(404, "ANN001", "id 가 %s 인 공지사항을 찾을 수 없습니다."),
    INVALID_LENGTH(400, "ANN002", "공지사항은 1자 이상 30자 이하여야 합니다."),
    INVALID_REFERENCE(400, "ANN003", "공지사항 작성자 정보가 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String description;

}
