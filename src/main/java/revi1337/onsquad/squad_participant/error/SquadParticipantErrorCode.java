package revi1337.onsquad.squad_participant.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadParticipantErrorCode implements ErrorCode {

    NEVER_REQUESTED(400, "SP001", "스쿼드에 참여신청을 한 이력이 없습니다.");

    private final int status;
    private final String code;
    private final String description;

}

