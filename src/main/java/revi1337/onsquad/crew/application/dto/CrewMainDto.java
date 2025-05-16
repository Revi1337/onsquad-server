package revi1337.onsquad.crew.application.dto;

import java.util.List;
import revi1337.onsquad.announce.application.dto.AnnounceDto;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.backup.crew.application.dto.Top5CrewMemberDto;
import revi1337.onsquad.backup.crew.domain.CrewTopMember;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.squad.application.dto.SquadDto;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;

public record CrewMainDto(
        boolean canManage,
        CrewInfoDto crew,
        List<AnnounceDto> announces,
        List<Top5CrewMemberDto> topMembers,
        List<SquadDto> squads
) {
    public static CrewMainDto from(boolean canManage, CrewInfoDomainDto crew, List<AnnounceDomainDto> announces,
                                   List<CrewTopMember> topMembers, List<SquadDomainDto> squads) {
        return new CrewMainDto(
                canManage,
                CrewInfoDto.from(crew),
                announces.stream()
                        .map(AnnounceDto::from)
                        .toList(),
                topMembers.stream()
                        .map(Top5CrewMemberDto::from)
                        .toList(),
                squads.stream()
                        .map(SquadDto::from)
                        .toList()
        );
    }
}
