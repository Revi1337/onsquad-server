package revi1337.onsquad.crew.application.dto;

import java.util.List;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.crew_member.application.dto.Top5CrewMemberDto;
import revi1337.onsquad.crew_member.domain.dto.Top5CrewMemberDomainDto;
import revi1337.onsquad.squad.application.dto.SquadInfoDto;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;

public record CrewMainDto(
        CrewInfoDto crew,
        List<AnnounceInfoDto> announces,
        List<Top5CrewMemberDto> topMembers,
        List<SquadInfoDto> squads
) {
    public static CrewMainDto from(CrewInfoDomainDto crew, List<AnnounceInfoDomainDto> announces,
                                   List<Top5CrewMemberDomainDto> topMembers, List<SquadInfoDomainDto> squads) {
        return new CrewMainDto(
                CrewInfoDto.from(crew),
                announces.stream()
                        .map(AnnounceInfoDto::from)
                        .toList(),
                topMembers.stream()
                        .map(Top5CrewMemberDto::from)
                        .toList(),
                squads.stream()
                        .map(SquadInfoDto::from)
                        .toList()
        );
    }
}
