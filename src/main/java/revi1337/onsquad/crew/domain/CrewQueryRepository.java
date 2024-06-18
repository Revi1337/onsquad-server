package revi1337.onsquad.crew.domain;

import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImage;

import java.util.List;
import java.util.Optional;

public interface CrewQueryRepository {

    Optional<CrewWithMemberAndImage> findCrewByName(Name name);

    List<CrewWithMemberAndImage> findCrewsByName();

}
