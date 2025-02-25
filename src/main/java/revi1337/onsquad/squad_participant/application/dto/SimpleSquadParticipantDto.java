package revi1337.onsquad.squad_participant.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_participant.domain.dto.SimpleSquadParticipantDomainDto;

public record SimpleSquadParticipantDto(
        SimpleMemberInfoDto memberInfo,
        LocalDateTime requestAt
) {
    public static SimpleSquadParticipantDto from(SimpleSquadParticipantDomainDto simpleSquadParticipantDomainDto) {
        return new SimpleSquadParticipantDto(
                SimpleMemberInfoDto.from(simpleSquadParticipantDomainDto.memberInfo()),
                simpleSquadParticipantDomainDto.requestAt()
        );
    }
}
