package revi1337.onsquad.squad_participant.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_participant.domain.dto.SimpleSquadParticipantDomainDto;

public record SimpleSquadParticipantDto(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberInfoDto member
) {
    public static SimpleSquadParticipantDto from(SimpleSquadParticipantDomainDto simpleSquadParticipantDomainDto) {
        return new SimpleSquadParticipantDto(
                simpleSquadParticipantDomainDto.id(),
                simpleSquadParticipantDomainDto.requestAt(),
                SimpleMemberInfoDto.from(simpleSquadParticipantDomainDto.member())
        );
    }
}
