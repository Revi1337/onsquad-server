package revi1337.onsquad.squad_request.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadRequestErrorCode implements ErrorCode {

    NEVER_REQUESTED(400, "SP001", "스쿼드에 참여신청을 한 이력이 없습니다."),
    MISMATCH_REFERENCE(400, "SP002", "해당 참가 신청은 요청된 스쿼드와 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String description;

}
