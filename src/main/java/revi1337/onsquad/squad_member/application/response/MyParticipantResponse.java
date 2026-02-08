package revi1337.onsquad.squad_member.application.response;

import java.util.List;
import revi1337.onsquad.crew.domain.model.CrewWithOwnerState;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad.application.dto.response.SimpleSquadResponse;
import revi1337.onsquad.squad_member.domain.result.MyParticipantSquadResult;

public record MyParticipantResponse(
        boolean isOwner,
        CrewWithSquadsResponse crew
) {

    public static MyParticipantResponse from(CrewWithOwnerState crewResult, List<MyParticipantSquadResult> squadResults) {
        return new MyParticipantResponse(
                crewResult.isOwner(),
                CrewWithSquadsResponse.from(crewResult, squadResults)
        );
    }

    record CrewWithSquadsResponse(
            Long id,
            String name,
            String imageUrl,
            SimpleMemberResponse owner,
            List<MySquadParticipantResponse> squads
    ) {

        public static CrewWithSquadsResponse from(CrewWithOwnerState crewResult, List<MyParticipantSquadResult> squadResults) {
            return new CrewWithSquadsResponse(
                    crewResult.crew().id(),
                    crewResult.crew().name(),
                    crewResult.crew().imageUrl() == null ? "" : crewResult.crew().imageUrl(),
                    SimpleMemberResponse.from(crewResult.crew().owner()),
                    squadResults.stream()
                            .map(MySquadParticipantResponse::from)
                            .toList()
            );
        }
    }

    record MySquadParticipantResponse(
            boolean isLeader,
            SimpleSquadResponse squad
    ) {

        public static MySquadParticipantResponse from(MyParticipantSquadResult result) {
            return new MySquadParticipantResponse(result.isLeader(), SimpleSquadResponse.from(result.squad()));
        }
    }
}
