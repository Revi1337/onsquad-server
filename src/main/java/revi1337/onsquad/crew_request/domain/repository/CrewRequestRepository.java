package revi1337.onsquad.crew_request.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_request.domain.dto.CrewRequestWithCrewDomainDto;
import revi1337.onsquad.crew_request.domain.dto.CrewRequestWithMemberDomainDto;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;

public interface CrewRequestRepository {

    CrewRequest save(CrewRequest crewRequest);

    CrewRequest saveAndFlush(CrewRequest crewRequest);

    Optional<CrewRequest> findById(Long id);

    void deleteById(Long id);

    void deleteByCrewIdAndMemberId(Long crewId, Long memberId);

    Optional<CrewRequest> findByCrewIdAndMemberId(Long crewId, Long memberId);

    List<CrewRequestWithCrewDomainDto> fetchAllWithSimpleCrewByMemberId(Long memberId);

    Page<CrewRequestWithMemberDomainDto> fetchCrewRequests(Long crewId, Pageable pageable);

}
