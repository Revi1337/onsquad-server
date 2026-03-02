package revi1337.onsquad.squad_member.application.response;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.crew.domain.model.CrewWithOwnerState;
import revi1337.onsquad.crew_member.application.response.CrewMemberStates;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad.application.response.SimpleSquadResponse;
import revi1337.onsquad.squad_member.domain.model.MyParticipantSquad;

public record MyParticipantSquadResponse(
        CrewMemberStates states,
        CrewWithSquadsResponse crew
) {

    public static MyParticipantSquadResponse from(CrewWithOwnerState crewResult, List<MyParticipantSquad> squadResults) {
        return new MyParticipantSquadResponse(
                new CrewMemberStates(crewResult.isOwner()),
                CrewWithSquadsResponse.from(crewResult, squadResults)
        );
    }

    public record CrewWithSquadsResponse(
            Long id,
            String name,
            String imageUrl,
            SimpleMemberResponse owner,
            List<MySquadParticipantResponse> squads
    ) {

        public static CrewWithSquadsResponse from(CrewWithOwnerState crewResult, List<MyParticipantSquad> squadResults) {
            return new CrewWithSquadsResponse(
                    crewResult.crew().id(),
                    crewResult.crew().name().getValue(),
                    crewResult.crew().imageUrl() == null ? "" : crewResult.crew().imageUrl(),
                    SimpleMemberResponse.from(crewResult.crew().owner()),
                    squadResults.stream()
                            .map(MySquadParticipantResponse::from)
                            .toList()
            );
        }
    }

    public record MySquadParticipantResponse(
            SquadMemberStates states,
            LocalDateTime participateAt,
            SimpleSquadResponse squad
    ) {

        public static MySquadParticipantResponse from(MyParticipantSquad result) {
            return new MySquadParticipantResponse(
                    SquadMemberStates.of(result.isLeader()),
                    result.participateAt(),
                    SimpleSquadResponse.from(result.squad())
            );
        }
    }
}
