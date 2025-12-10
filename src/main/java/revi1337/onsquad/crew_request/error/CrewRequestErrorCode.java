package revi1337.onsquad.crew_request.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CrewRequestErrorCode implements ErrorCode {

    NOT_FOUND(404, "CP001", "신청 정보를 찾을 수 없습니다."),
    MISMATCH_CREW_REFERENCE(400, "CP003", "신청한 크루 정보가 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String description;

}
