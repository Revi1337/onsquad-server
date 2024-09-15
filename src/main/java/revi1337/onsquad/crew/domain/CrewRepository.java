package revi1337.onsquad.crew.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.hashtag.domain.Hashtag;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew.error.CrewErrorCode.NOTFOUND_CREW;
import static revi1337.onsquad.crew.error.CrewErrorCode.NOTFOUND_CREW_ID;

public interface CrewRepository {

    Crew save(Crew crew);

    Crew saveAndFlush(Crew crew);

    Optional<Crew> findById(Long id);

    Optional<Crew> findByName(Name name);

    Optional<Crew> findByNameWithHashtags(Name name);

    boolean existsByName(Name name);

    List<Crew> findAllByMemberId(Long memberId);

    Optional<CrewInfoDomainDto> findCrewByName(Name name);

    Page<CrewInfoDomainDto> findCrewsByName(String name, Pageable pageable);

    Optional<Crew> findByNameWithImage(Name name);

    Optional<Crew> findByNameWithCrewMembers(Name name);

    void batchInsertCrewHashtags(Long crewId, List<Hashtag> hashtags);

    default CrewInfoDomainDto getCrewByName(Name name) {
        return findCrewByName(name)
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, name.getValue()));
    }

    default Crew getCrewByNameWithImage(Name name) {
        return findByNameWithImage(name)
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, name.getValue()));
    }

    default Crew getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CrewBusinessException.NotFoundById(NOTFOUND_CREW_ID, id));
    }

    default Crew getByName(Name name) {
        return findByName(name)
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, name.getValue()));
    }

    default Crew getByNameWithHashtags(Name name) {
        return findByNameWithHashtags(name)
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, name.getValue()));
    }

    default Crew getByNameWithCrewMembers(Name name) {
        return findByNameWithCrewMembers(name)
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, name.getValue()));
    }
}
