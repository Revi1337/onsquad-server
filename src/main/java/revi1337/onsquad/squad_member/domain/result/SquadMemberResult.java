package revi1337.onsquad.squad_member.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SquadMemberResult(
        Boolean isMe,
        LocalDateTime participantAt,
        SimpleMemberDomainDto member
) {

    @QueryProjection
    public SquadMemberResult(Boolean isMe, LocalDateTime participantAt, SimpleMemberDomainDto member) {
        this.isMe = isMe;
        this.participantAt = participantAt;
        this.member = member;
    }

    @QueryProjection
    public SquadMemberResult(LocalDateTime participantAt, SimpleMemberDomainDto member) {
        this(null, participantAt, member);
    }
}
