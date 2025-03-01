package revi1337.onsquad.squad_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;

public record EnrolledSquadDomainDto(
        Long id,
        Name name,
        String imageUrl,
        SimpleMemberInfoDomainDto owner,
        List<SimpleSquadInfoDomainDto> squads
) {
    @QueryProjection
    public EnrolledSquadDomainDto(Long crewId, Name crewName, String imageUrl, SimpleMemberInfoDomainDto crewOwner) {
        this(crewId, crewName, imageUrl, crewOwner, new ArrayList<>());
    }
}
