package revi1337.onsquad.squad_member.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadMemberErrorCode implements ErrorCode {

    NOT_IN_SQUAD(404, "SQM001", "사용자가 스쿼드에 속해있지 않습니다."),
    NOT_LEADER(403, "SQM002", "사용자가 스쿼드 Leader 가 아닙니다.");

    private final int status;
    private final String code;
    private final String description;

}

