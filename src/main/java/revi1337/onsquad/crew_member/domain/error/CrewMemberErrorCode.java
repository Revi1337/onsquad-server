package revi1337.onsquad.crew_member.domain.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum CrewMemberErrorCode implements ErrorCode {

    NOT_PARTICIPANT(400, "CRM001", "사용자가 크루에 속해있지 않습니다."),
    LESS_THAN_MANAGER(403, "CRM002", "크루장 혹은 크루매니저만 이용할 수 있습니다."),
    ALREADY_JOIN(400, "CRM003", "이미 크루에 가입된 사용자입니다."),
    CANNOT_TARGET_SELF(400, "CRM004", "자기 자신을 대상으로 선택할 수 없습니다."),
    MISMATCH_CREW_REFERENCE(400, "CRM005", "소속 크루가 서로 다른 멤버 간에는 해당 요청을 수행할 수 없습니다."),
    INSUFFICIENT_KICK_MEMBER_AUTHORITY(403, "CRM006", "크루원 추방은 크루 매니저 이상만 가능합니다."),
    CANNOT_KICK_EQUAL_OR_HIGHER_ROLE_MEMBER(403, "CRM007", "자신보다 같거나 높은 등급의 멤버는 추방할 수 없습니다."),
    INSUFFICIENT_DELEGATE_OWNER_AUTHORITY(403, "CRM008", "크루장 위임은 크루장만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
