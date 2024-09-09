package revi1337.onsquad.crew.domain;

import revi1337.onsquad.crew.domain.vo.Name;

import java.util.Optional;

public interface CrewQueryRepository {

    Optional<Crew> findByNameWithImage(Name name);

    Optional<Crew> findByNameWithCrewMembers(Name name);

}
