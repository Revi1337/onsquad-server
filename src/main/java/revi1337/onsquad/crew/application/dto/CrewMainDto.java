package revi1337.onsquad.crew.application.dto;

import java.util.List;
import revi1337.onsquad.announce.application.dto.AnnounceDto;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.crew.application.dto.top.Top5CrewMemberDto;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.crew.domain.entity.CrewTopMember;
import revi1337.onsquad.squad.application.dto.SquadDto;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;

public record CrewMainDto(
        boolean canManage,
        CrewDto crew,
        List<AnnounceDto> announces,
        List<Top5CrewMemberDto> topMembers,
        List<SquadDto> squads
) {

    public static CrewMainDto from(boolean canManage, CrewDomainDto crew, List<AnnounceDomainDto> announces,
                                   List<CrewTopMember> topMembers, List<SquadDomainDto> squads) {
        return new CrewMainDto(
                canManage,
                CrewDto.from(crew),
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
