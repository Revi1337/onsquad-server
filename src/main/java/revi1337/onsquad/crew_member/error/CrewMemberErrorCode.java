package revi1337.onsquad.crew_member.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum CrewMemberErrorCode implements ErrorCode {

    NOT_PARTICIPANT(400, "CRM001", "사용자가 크루에 속해있지 않습니다."),
    NOT_OWNER(403, "CRM002", "크루장만 이용할 수 있습니다."),
    LESS_THAN_MANAGER(403, "CRM003", "크루장 혹은 크루매니저만 이용할 수 있습니다."),
    ALREADY_JOIN(400, "CRM004", "이미 크루에 가입된 사용자입니다."),
    CANNOT_TARGET_SELF(400, "CRM005", "자기 자신을 대상으로 선택할 수 없습니다."),
    INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY(403, "CRM006", "크루 참가자 조회는 크루장만 가능합니다."),
    INSUFFICIENT_READ_CREW_STATISTIC_AUTHORITY(403, "CRM007", "크루 통계 정보 조회는 크루장만 가능합니다."),
    INSUFFICIENT_LEAVE_CREW_AUTHORITY(403, "CRM008", "크루장은 권한 위임 후 탈퇴 가능합니다."),
    INSUFFICIENT_KICK_MEMBER_AUTHORITY(403, "CRM009", "크루원 추방은 크루 매니저 이상만 가능합니다."),
    CANNOT_KICK_HIGHER_ROLE_MEMBER(403, "CRM010", "자신보다 높은 등급의 멤버는 강퇴할 수 없습니다."),
    INSUFFICIENT_DELEGATE_OWNER_AUTHORITY(403, "CRM011", "크루장 위임은 크루장만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
