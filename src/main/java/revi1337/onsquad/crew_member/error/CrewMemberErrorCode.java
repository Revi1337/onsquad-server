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
    ALREADY_JOIN(400, "CRM004", "이미 크루에 가입된 사용자입니다.");

    private final int status;
    private final String code;
    private final String description;

}
