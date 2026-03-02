package revi1337.onsquad.squad_member.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadMemberStates(
        Boolean isMe,
        Boolean isLeader,
        Boolean canKick,
        Boolean canDelegateLeader
) {

    public static SquadMemberStates of(boolean isLeader) {
        return new SquadMemberStates(isLeader, null, null, null);
    }

    public static SquadMemberStates of(Boolean isMe, Boolean canKick, Boolean canDelegateLeader) {
        return new SquadMemberStates(isMe, null, canKick, canDelegateLeader);
    }
}
