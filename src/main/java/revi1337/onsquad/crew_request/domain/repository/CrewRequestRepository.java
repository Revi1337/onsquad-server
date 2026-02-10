package revi1337.onsquad.crew_request.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;

public interface CrewRequestRepository {

    CrewRequest save(CrewRequest crewRequest);

    CrewRequest saveAndFlush(CrewRequest crewRequest);

    Optional<CrewRequest> findById(Long id);

    Optional<CrewRequest> findByCrewIdAndMemberId(Long crewId, Long memberId);

    Page<CrewRequest> fetchAllWithSimpleCrewByMemberId(Long memberId, Pageable pageable);

    Page<CrewRequest> fetchCrewRequests(Long crewId, Pageable pageable);

    void delete(CrewRequest request);

    int deleteByMemberId(Long memberId);

    int deleteByCrewIdAndMemberId(Long crewId, Long memberId);

    int deleteByCrewIdIn(List<Long> crewIds);

}
