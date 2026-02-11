package revi1337.onsquad.crew_request.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CrewRequestErrorCode implements ErrorCode {

    NOT_FOUND(404, "CP001", "신청 정보를 찾을 수 없습니다."),
    MISMATCH_CREW_REFERENCE(400, "CP002", "신청한 크루 정보가 일치하지 않습니다."),
    INSUFFICIENT_ACCEPT_AUTHORITY(403, "CP003", "크루 신청 수락은 크루 매니저 이상만 가능합니다."),
    INSUFFICIENT_REJECT_AUTHORITY(403, "CP004", "크루 신청 거절은 크루 매니저 이상만 가능합니다."),
    INSUFFICIENT_READ_LIST_AUTHORITY(403, "CP005", "크루 신청 목록 조회는 크루 매니저 이상만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
