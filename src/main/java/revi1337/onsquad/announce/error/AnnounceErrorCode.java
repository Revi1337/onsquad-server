package revi1337.onsquad.announce.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum AnnounceErrorCode implements ErrorCode {

    NOT_FOUND(404, "ANN001", "id 가 %s 인 공지사항을 찾을 수 없습니다."),
    CANT_MAKE(403, "ANN002", "공지사항은 크루장 혹은 매니저만 작성할 수 있습니다"),
    CANT_FIX(403, "ANN003", "공지사항 고정은 크루장만 이용할 수 있습니다.");

    private final int status;
    private final String code;
    private final String description;

}
