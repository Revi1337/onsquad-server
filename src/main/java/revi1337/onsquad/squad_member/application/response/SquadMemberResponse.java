package revi1337.onsquad.squad_member.application.response;

import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

public record SquadMemberResponse(
        boolean isMe,
        boolean canKick,
        boolean canDelegateLeader,
        SimpleMemberResponse member
) {

    public static SquadMemberResponse from(boolean isMe, boolean canKick, boolean canDelegateLeader, SquadMember participant) {
        return new SquadMemberResponse(
                isMe,
                canKick,
                canDelegateLeader,
                SimpleMemberResponse.from(participant.getMember())
        );
    }
}
