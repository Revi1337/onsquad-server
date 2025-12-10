package revi1337.onsquad.squad_request.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadRequestErrorCode implements ErrorCode {

    NOT_FOUND(404, "SP001", "신청 정보를 찾을 수 없습니다."),
    MISMATCH_SQUAD_REFERENCE(400, "SP002", "신청한 스쿼드 정보가 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String description;

}
