package revi1337.onsquad.crew_member.application.response;

import revi1337.onsquad.crew.application.dto.response.SimpleCrewResponse;
import revi1337.onsquad.crew_member.domain.result.MyParticipantCrewResult;

public record MyParticipantResponse(
        boolean isOwner,
        SimpleCrewResponse crew
) {

    public static MyParticipantResponse from(MyParticipantCrewResult result) {
        return new MyParticipantResponse(
                result.isOwner(),
                SimpleCrewResponse.from(result.crew())
        );
    }
}
