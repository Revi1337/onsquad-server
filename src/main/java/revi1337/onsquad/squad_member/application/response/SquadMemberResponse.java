package revi1337.onsquad.squad_member.application.response;

import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

public record SquadMemberResponse(
        boolean isMe,
        boolean canKick,
        boolean canLeaderDelegate,
        SimpleMemberResponse member
) {

    public static SquadMemberResponse from(boolean isMe, boolean canKick, boolean canLeaderDelegate, SquadMember participant) {
        return new SquadMemberResponse(
                isMe,
                canKick,
                canLeaderDelegate,
                SimpleMemberResponse.from(participant.getMember())
        );
    }
}
