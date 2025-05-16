package revi1337.onsquad.squad.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.application.dto.SimpleSquadInfoDto;

public record SimpleSquadInfoResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        Boolean isLeader,
        List<String> categories,
        SimpleMemberInfoResponse leader
) {
    public static SimpleSquadInfoResponse from(SimpleSquadInfoDto simpleSquadInfoDto) {
        return new SimpleSquadInfoResponse(
                simpleSquadInfoDto.id(),
                simpleSquadInfoDto.title(),
                simpleSquadInfoDto.capacity(),
                simpleSquadInfoDto.remain(),
                simpleSquadInfoDto.isLeader(),
                simpleSquadInfoDto.categories(),
                SimpleMemberInfoResponse.from(simpleSquadInfoDto.leader())
        );
    }
}
