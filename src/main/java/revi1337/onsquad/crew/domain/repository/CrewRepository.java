package revi1337.onsquad.crew.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.crew.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.entity.vo.Name;

public interface CrewRepository {

    Crew save(Crew crew);

    Crew saveAndFlush(Crew crew);

    Crew getReferenceById(Long id);

    void deleteById(Long id);

    Optional<Crew> findById(Long id);

    Optional<Crew> findByIdForUpdate(Long id);

    boolean existsByName(Name name);

    Optional<CrewDomainDto> findCrewById(Long id);

    Page<CrewDomainDto> fetchCrewsByName(String name, Pageable pageable);

    Page<CrewDomainDto> fetchOwnedByMemberId(Long memberId, Pageable pageable);

    List<EnrolledCrewDomainDto> fetchParticipantsByMemberId(Long memberId);

}
