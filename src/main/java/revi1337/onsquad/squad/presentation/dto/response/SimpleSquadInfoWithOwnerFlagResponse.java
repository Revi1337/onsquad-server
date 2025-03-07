package revi1337.onsquad.squad.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.application.dto.SimpleSquadInfoWithOwnerFlagDto;

public record SimpleSquadInfoWithOwnerFlagResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        Boolean isOwner,
        List<String> categories,
        SimpleMemberInfoResponse squadOwner
) {
    public static SimpleSquadInfoWithOwnerFlagResponse from(SimpleSquadInfoWithOwnerFlagDto squadInfoWithOwnerFlagDto) {
        return new SimpleSquadInfoWithOwnerFlagResponse(
                squadInfoWithOwnerFlagDto.id(),
                squadInfoWithOwnerFlagDto.title(),
                squadInfoWithOwnerFlagDto.capacity(),
                squadInfoWithOwnerFlagDto.remain(),
                squadInfoWithOwnerFlagDto.isOwner(),
                squadInfoWithOwnerFlagDto.categories(),
                SimpleMemberInfoResponse.from(squadInfoWithOwnerFlagDto.squadOwner())
        );
    }
}
