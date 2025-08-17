package revi1337.onsquad.squad.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad.application.dto.SimpleSquadDto;

public record SimpleSquadResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberResponse leader
) {
    public static SimpleSquadResponse from(SimpleSquadDto squadDto) {
        return new SimpleSquadResponse(
                squadDto.id(),
                squadDto.title(),
                squadDto.capacity(),
                squadDto.remain(),
                squadDto.categories(),
                SimpleMemberResponse.from(squadDto.leader())
        );
    }
}
