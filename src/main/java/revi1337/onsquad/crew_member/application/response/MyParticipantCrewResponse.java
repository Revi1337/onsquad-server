package revi1337.onsquad.crew_member.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.application.dto.response.SimpleCrewResponse;
import revi1337.onsquad.crew_member.domain.model.MyParticipantCrew;

public record MyParticipantCrewResponse(
        CrewMemberStates states,
        LocalDateTime participateAt,
        SimpleCrewResponse crew
) {

    public static MyParticipantCrewResponse from(MyParticipantCrew result) {
        return new MyParticipantCrewResponse(
                new CrewMemberStates(result.isOwner()),
                result.participateAt(),
                SimpleCrewResponse.from(result.crew())
        );
    }
}
