package revi1337.onsquad.crew_member.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewActivity {

    CREW_PARTICIPANT(5),
    SQUAD_CREATE(10),
    SQUAD_PARTICIPANT(3),
    SQUAD_COMMENT(1),
    SQUAD_COMMENT_REPLY(1);

    private final int score;

}
