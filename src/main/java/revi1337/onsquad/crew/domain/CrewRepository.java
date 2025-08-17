package revi1337.onsquad.crew.domain;

import static revi1337.onsquad.crew.error.CrewErrorCode.NOTFOUND_CREW;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.crew.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;

public interface CrewRepository {

    Crew save(Crew crew);

    Crew saveAndFlush(Crew crew);

    void deleteById(Long id);

    Optional<Crew> findById(Long id);

    boolean existsByName(Name name);

    Optional<CrewDomainDto> findCrewById(Long id);

    Page<CrewDomainDto> fetchCrewsByName(String name, Pageable pageable);

    Page<CrewDomainDto> fetchOwnedByMemberId(Long memberId, Pageable pageable);

    List<EnrolledCrewDomainDto> fetchParticipantsByMemberId(Long memberId);

    default CrewDomainDto getCrewById(Long id) {
        return findCrewById(id)
                .orElseThrow(() -> new CrewBusinessException.NotFoundById(NOTFOUND_CREW, id));
    }

    default Crew getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CrewBusinessException.NotFoundById(NOTFOUND_CREW, id));
    }
}
