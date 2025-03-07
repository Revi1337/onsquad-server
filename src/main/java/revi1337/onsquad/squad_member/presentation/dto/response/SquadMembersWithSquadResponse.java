package revi1337.onsquad.squad_member.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_member.application.dto.SquadMembersWithSquadDto;

public record SquadMembersWithSquadResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        Boolean isOwner,
        SimpleMemberInfoResponse owner,
        List<String> categories,
        List<SquadMemberResponse> members
) {
    public static SquadMembersWithSquadResponse from(SquadMembersWithSquadDto squadMembersWithSquadDto) {
        return new SquadMembersWithSquadResponse(
                squadMembersWithSquadDto.id(),
                squadMembersWithSquadDto.title(),
                squadMembersWithSquadDto.capacity(),
                squadMembersWithSquadDto.remain(),
                squadMembersWithSquadDto.isOwner(),
                SimpleMemberInfoResponse.from(squadMembersWithSquadDto.owner()),
                squadMembersWithSquadDto.categories(),
                squadMembersWithSquadDto.members().stream()
                        .map(SquadMemberResponse::from)
                        .toList()
        );
    }
}
