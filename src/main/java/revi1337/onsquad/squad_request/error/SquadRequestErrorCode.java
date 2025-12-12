package revi1337.onsquad.squad_request.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadRequestErrorCode implements ErrorCode {

    NOT_FOUND(404, "SP001", "신청 정보를 찾을 수 없습니다."),
    MISMATCH_SQUAD_REFERENCE(400, "SP002", "신청한 스쿼드 정보가 일치하지 않습니다."),
    INSUFFICIENT_ACCEPT_AUTHORITY(403, "SP004", "스쿼드 신청 수락은 스쿼드 리더만 가능합니다."),
    INSUFFICIENT_REJECT_AUTHORITY(403, "SP005", "스쿼드 신청 거절은 스쿼드 리더만 가능합니다."),
    INSUFFICIENT_READ_LIST_AUTHORITY(403, "SP006", "스쿼드 신정 목록 조회는 스쿼드 리더만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
