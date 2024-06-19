package revi1337.onsquad.crew.domain;

import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew.dto.OwnedCrewsDto;

import java.util.List;
import java.util.Optional;

public interface CrewQueryRepository {

    Optional<CrewWithMemberAndImageDto> findCrewByName(Name name);

    List<CrewWithMemberAndImageDto> findCrewsByName();

    List<OwnedCrewsDto> findOwnedCrews(Long memberId);

    Optional<Crew> findCrewByNameForUpdate(Name name);

}
