package revi1337.onsquad.crew_member.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewMemberStates(
        Boolean isMe,
        Boolean canKick,
        Boolean canDelegateOwner,
        Boolean isOwner
) {

    public CrewMemberStates(boolean isOwner) {
        this(null, null, null, isOwner);
    }

    public CrewMemberStates(boolean isMe, boolean canKick, boolean canDelegateOwner) {
        this(isMe, canKick, canDelegateOwner, null);
    }
}
