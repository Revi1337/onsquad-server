package revi1337.onsquad.squad_member.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

public record SquadMemberResponse(
        SquadMemberStates states,
        LocalDateTime participateAt,
        SimpleMemberResponse member
) {

    public static SquadMemberResponse from(Boolean isMe, Boolean canKick, Boolean canDelegateLeader, SquadMember participant) {
        return new SquadMemberResponse(
                SquadMemberStates.of(isMe, canKick, canDelegateLeader),
                participant.getParticipateAt(),
                SimpleMemberResponse.from(participant.getMember())
        );
    }
}
