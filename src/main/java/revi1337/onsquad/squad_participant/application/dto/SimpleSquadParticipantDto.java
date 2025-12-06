package revi1337.onsquad.squad_participant.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_participant.domain.dto.SimpleSquadParticipantDomainDto;

public record SimpleSquadParticipantDto(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberDto member
) {

    public static SimpleSquadParticipantDto from(SimpleSquadParticipantDomainDto simpleSquadParticipantDomainDto) {
        return new SimpleSquadParticipantDto(
                simpleSquadParticipantDomainDto.id(),
                simpleSquadParticipantDomainDto.requestAt(),
                SimpleMemberDto.from(simpleSquadParticipantDomainDto.member())
        );
    }
}
