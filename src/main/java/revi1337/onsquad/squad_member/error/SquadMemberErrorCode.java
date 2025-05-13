package revi1337.onsquad.squad_member.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadMemberErrorCode implements ErrorCode {

    NOT_IN_SQUAD(404, "SQM001", "사용자가 스쿼드에 속해있지 않습니다."),
    NOT_LEADER(403, "SQM002", "스쿼드 Leader 만 이용할 수 있습니다."),
    CANNOT_LEAVE_LEADER(403, "SQM003", "스쿼드 Leader 는 Leader 권한 위임 후 탈퇴 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}

