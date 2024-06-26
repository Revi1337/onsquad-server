package revi1337.onsquad.crew_member.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum CrewMemberErrorCode implements ErrorCode {

    NEVER_REQUESTED(400, "CRM001", "%s 크루에 참여요청을 한 이력이 없습니다.");

    private final int status;
    private final String code;
    private final String description;

}

