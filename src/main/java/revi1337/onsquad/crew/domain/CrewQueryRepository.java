package revi1337.onsquad.crew.domain;

import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.OwnedCrewsDto;

import java.util.List;
import java.util.Optional;

public interface CrewQueryRepository {

    List<OwnedCrewsDto> findOwnedCrews(Long memberId);

    Optional<Crew> findCrewByNameWithImage(Name name);

    Optional<Crew> findCrewByNameWithCrewMembers(Name name);

}
