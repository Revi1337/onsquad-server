package revi1337.onsquad.squad_member.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadMemberErrorCode implements ErrorCode {

    ;

    private final int status;
    private final String code;
    private final String description;

}

