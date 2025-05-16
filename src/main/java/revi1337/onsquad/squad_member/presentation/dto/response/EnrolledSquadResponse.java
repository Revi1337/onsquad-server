package revi1337.onsquad.squad_member.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto;
import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto.SimpleSquadInfoDto;

public record EnrolledSquadResponse(
        Long id,
        String name,
        String imageUrl,
        SimpleMemberInfoResponse owner,
        List<SimpleSquadInfoResponse> squads
) {
    public static EnrolledSquadResponse from(EnrolledSquadDto enrolledSquadDto) {
        return new EnrolledSquadResponse(
                enrolledSquadDto.id(),
                enrolledSquadDto.name(),
                enrolledSquadDto.imageUrl(),
                SimpleMemberInfoResponse.from(enrolledSquadDto.owner()),
                enrolledSquadDto.squads().stream()
                        .map(SimpleSquadInfoResponse::from)
                        .toList()
        );
    }

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
}
