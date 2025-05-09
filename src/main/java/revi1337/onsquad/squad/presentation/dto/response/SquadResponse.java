package revi1337.onsquad.squad.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.application.dto.SquadDto;

public record SquadResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberInfoResponse owner
) {
    public static SquadResponse from(SquadDto squadDto) {
        return new SquadResponse(
                squadDto.id(),
                squadDto.title(),
                squadDto.capacity(),
                squadDto.remain(),
                squadDto.categories(),
                SimpleMemberInfoResponse.from(squadDto.owner())
        );
    }
}
