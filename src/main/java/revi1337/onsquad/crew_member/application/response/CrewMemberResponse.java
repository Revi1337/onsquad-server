package revi1337.onsquad.crew_member.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

public record CrewMemberResponse(
        CrewMemberStates states,
        LocalDateTime participantAt,
        SimpleMemberResponse member
) {

    public static CrewMemberResponse from(boolean isMe, boolean canKick, boolean canDelegateOwner, CrewMember participant) {
        return new CrewMemberResponse(
                new CrewMemberStates(isMe, canKick, canDelegateOwner),
                participant.getParticipateAt(),
                SimpleMemberResponse.from(participant.getMember())
        );
    }
}
