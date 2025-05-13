package revi1337.onsquad.squad_member.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_member.application.dto.SquadMemberDto;

@JsonInclude(Include.NON_NULL)
public record SquadMemberResponse(
        Boolean matchCurrentMember,
        LocalDateTime participantAt,
        SimpleMemberInfoResponse member
) {
    public static SquadMemberResponse from(SquadMemberDto squadMemberDto) {
        return new SquadMemberResponse(
                squadMemberDto.matchCurrentMember(),
                squadMemberDto.participantAt(),
                SimpleMemberInfoResponse.from(squadMemberDto.member())
        );
    }
}
