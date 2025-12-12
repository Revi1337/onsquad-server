package revi1337.onsquad.squad_member.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadMemberErrorCode implements ErrorCode {

    NOT_PARTICIPANT(404, "SQM001", "사용자가 스쿼드에 속해있지 않습니다."),
    ALREADY_JOIN(400, "SQM002", "이미 스쿼드에 가입된 사용자입니다."),
    CANNOT_LEAVE_LEADER(403, "SQM003", "스쿼드 리더는 권한 위임 후 탈퇴 가능합니다."),
    INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY(403, "SQM004", "스쿼드 참가자 조회는 스쿼드 리더만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
