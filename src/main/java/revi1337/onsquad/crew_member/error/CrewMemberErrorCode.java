package revi1337.onsquad.crew_member.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum CrewMemberErrorCode implements ErrorCode {

    NOT_PARTICIPANT(400, "CRM001", "사용자가 크루에 속해있지 않습니다."),
    NOT_OWNER(403, "CRM002", "사용자가 크루장이 아닙니다.");

    private final int status;
    private final String code;
    private final String description;

}

