package revi1337.onsquad.crew_participant.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CrewParticipantErrorCode implements ErrorCode {

    NEVER_REQUESTED(400, "CP001", "신청정보를 찾을 수 없습니다."),
    CANT_SEE_PARTICIPANTS(400, "CP002", "crew 작성자 이외에는 신청자를 볼 수 없습니다."),
    INVALID_REFERENCE(400, "CP003", "신청정보와 크루정보가 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String description;

}
