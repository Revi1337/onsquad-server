package revi1337.onsquad.squad_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;

public record EnrolledSquadDomainDto(
        Long crewId,
        Name crewName,
        String imageUrl,
        SimpleMemberInfoDomainDto crewOwner,
        List<SimpleSquadInfoDomainDto> squads
) {
    @QueryProjection
    public EnrolledSquadDomainDto(Long crewId, Name crewName, String imageUrl, SimpleMemberInfoDomainDto crewOwner,
                                  List<SimpleSquadInfoDomainDto> squads) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.imageUrl = imageUrl;
        this.crewOwner = crewOwner;
        this.squads = squads;
    }
}
