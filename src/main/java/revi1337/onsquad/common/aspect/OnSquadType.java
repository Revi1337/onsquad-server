package revi1337.onsquad.common.aspect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OnSquadType {

    CREW("crew", "onsquad:crew:%s:%s"),
    SQUAD("squad", "onsquad:squad:%s:%s"),
    MEMBER("member", "onsquad:member:%s:%s")
    ;

    private final String text;
    private final String format;

}
