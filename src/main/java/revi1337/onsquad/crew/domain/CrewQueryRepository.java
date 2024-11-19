package revi1337.onsquad.crew.domain;

import java.util.Optional;

public interface CrewQueryRepository {

    Optional<Crew> findByIdWithImage(Long id);

    Optional<Crew> findByIdWithCrewMembers(Long id);

}
