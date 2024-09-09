package revi1337.onsquad.crew_participant.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CrewParticipantErrorCode implements ErrorCode {

    NEVER_REQUESTED(400, "CP001", "크루에 참여신청을 한 이력이 없습니다."),
    CANT_SEE_PARTICIPANTS(400, "CP002", "crew 작성자 이외에는 신청자를 볼 수 없습니다.");

    private final int status;
    private final String code;
    private final String description;

}
