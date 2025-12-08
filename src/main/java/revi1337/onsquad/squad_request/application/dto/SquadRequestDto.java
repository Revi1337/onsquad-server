package revi1337.onsquad.squad_request.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestDomainDto;

public record SquadRequestDto(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberDto member
) {

    public static SquadRequestDto from(SquadRequestDomainDto squadRequestDomainDto) {
        return new SquadRequestDto(
                squadRequestDomainDto.id(),
                squadRequestDomainDto.requestAt(),
                SimpleMemberDto.from(squadRequestDomainDto.member())
        );
    }
}
