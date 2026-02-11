package revi1337.onsquad.squad_member.domain.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadMemberErrorCode implements ErrorCode {

    NOT_PARTICIPANT(404, "SQM001", "사용자가 스쿼드에 속해있지 않습니다."),
    ALREADY_JOIN(400, "SQM002", "이미 스쿼드에 가입된 사용자입니다."),
    CANNOT_TARGET_SELF(400, "SQM003", "자기 자신을 대상으로 선택할 수 없습니다."),
    INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY(403, "SQM004", "스쿼드 참가자 조회는 스쿼드 참가자 또는 크루장만 가능합니다."),
    INSUFFICIENT_DELEGATE_LEADER_AUTHORITY(403, "SQM005", "스쿼드 리더 위임은 스쿼드 리더만 가능합니다."),
    INSUFFICIENT_KICK_MEMBER_AUTHORITY(403, "SQM006", "스쿼드원 강퇴는 스쿼드 리더만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
