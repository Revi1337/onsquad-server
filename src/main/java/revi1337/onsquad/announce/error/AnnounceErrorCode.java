package revi1337.onsquad.announce.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum AnnounceErrorCode implements ErrorCode {

    NOT_FOUND(404, "ANN001", "공지사항을 찾을 수 없습니다."),
    INVALID_LENGTH(400, "ANN002", "공지사항은 1자 이상 30자 이하여야 합니다."),
    MISMATCH_CREW_REFERENCE(400, "ANN003", "공지사항이 속한 크루가 일치하지 않습니다."),
    INSUFFICIENT_CREATE_AUTHORITY(403, "ANN004", "공지사항을 생성은 크루장 또는 크루매니저만 가능합니다."),
    INSUFFICIENT_UPDATE_AUTHORITY(403, "ANN005", "공지사항을 수정은 크루장 또는 크루매니저만 가능합니다."),
    INSUFFICIENT_FIX_AUTHORITY(403, "ANN006", "공지사항을 상단고정은 크루장만 가능합니다."),
    INSUFFICIENT_DELETE_AUTHORITY(403, "ANN007", "공지사항을 삭제는 크루장 또는 크루매니저만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
