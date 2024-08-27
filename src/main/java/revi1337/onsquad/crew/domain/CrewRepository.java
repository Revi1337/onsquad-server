package revi1337.onsquad.crew.domain;

import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew.dto.OwnedCrewsDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew.error.CrewErrorCode.NOTFOUND_CREW;

public interface CrewRepository {

    Crew save(Crew crew);

    Crew saveAndFlush(Crew crew);

    Optional<Crew> findByName(Name name);

    boolean existsByName(Name name);

    List<Crew> findAllByMemberId(Long memberId);

    Optional<CrewWithMemberAndImageDto> findCrewByName(Name name);

    List<CrewWithMemberAndImageDto> findCrewsByName();

    List<OwnedCrewsDto> findOwnedCrews(Long memberId);

    Optional<Crew> findCrewByNameWithImage(Name name);

    Optional<Crew> findByNameWithCrewMembers(Name name);

    default CrewWithMemberAndImageDto getCrewByName(Name name) {
        return findCrewByName(name)
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, name.getValue()));
    }

    default Crew getByName(Name name) {
        return findByName(name)
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, name.getValue()));
    }

    default Crew getCrewByNameWithImage(Name name) {
        return findCrewByNameWithImage(name)
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, name.getValue()));
    }
}
