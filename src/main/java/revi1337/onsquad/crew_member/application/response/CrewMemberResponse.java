package revi1337.onsquad.crew_member.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

public record CrewMemberResponse(
        boolean isMe,
        boolean canKick,
        boolean canOwnerDelegate,
        LocalDateTime participantAt,
        SimpleMemberResponse member
) {

    public static CrewMemberResponse from(boolean isMe, boolean canKick, boolean canOwnerDelegate, CrewMember participant) {
        return new CrewMemberResponse(
                isMe,
                canKick,
                canOwnerDelegate,
                participant.getRequestAt(),
                SimpleMemberResponse.from(participant.getMember())
        );
    }
}
