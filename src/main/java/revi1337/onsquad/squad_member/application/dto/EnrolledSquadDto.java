package revi1337.onsquad.squad_member.application.dto;

import java.util.List;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.application.dto.SimpleSquadInfoDto;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;

public record EnrolledSquadDto(
        Long id,
        String name,
        String imageUrl,
        SimpleMemberInfoDto owner,
        List<SimpleSquadInfoDto> squads
) {
    public static EnrolledSquadDto from(EnrolledSquadDomainDto squadDomainDto) {
        return new EnrolledSquadDto(
                squadDomainDto.id(),
                squadDomainDto.name().getValue(),
                squadDomainDto.imageUrl(),
                SimpleMemberInfoDto.from(squadDomainDto.owner()),
                squadDomainDto.squads().stream()
                        .map(SimpleSquadInfoDto::from)
                        .toList()
        );
    }
}
