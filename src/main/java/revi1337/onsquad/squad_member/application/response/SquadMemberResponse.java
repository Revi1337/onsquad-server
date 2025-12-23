package revi1337.onsquad.squad_member.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_member.domain.result.SquadMemberResult;

@JsonInclude(Include.NON_NULL)
public record SquadMemberResponse(
        Boolean isMe,
        LocalDateTime participantAt,
        SimpleMemberDto member
) {

    public static SquadMemberResponse from(SquadMemberResult squadMemberResult) {
        return new SquadMemberResponse(
                null,
                squadMemberResult.participantAt(),
                SimpleMemberDto.from(squadMemberResult.member())
        );
    }

    public static SquadMemberResponse from(Long memberId, SquadMemberResult squadMemberResult) {
        return new SquadMemberResponse(
                squadMemberResult.member().id().equals(memberId),
                squadMemberResult.participantAt(),
                SimpleMemberDto.from(squadMemberResult.member())
        );
    }
}
