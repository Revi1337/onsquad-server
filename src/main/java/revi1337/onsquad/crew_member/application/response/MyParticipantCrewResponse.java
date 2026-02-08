package revi1337.onsquad.crew_member.application.response;

import revi1337.onsquad.crew.application.dto.response.SimpleCrewResponse;
import revi1337.onsquad.crew_member.domain.model.MyParticipantCrew;

public record MyParticipantCrewResponse(
        CrewMemberStates states,
        SimpleCrewResponse crew
) {

    public static MyParticipantCrewResponse from(MyParticipantCrew result) {
        return new MyParticipantCrewResponse(
                new CrewMemberStates(result.isOwner()),
                SimpleCrewResponse.from(result.crew())
        );
    }
}
