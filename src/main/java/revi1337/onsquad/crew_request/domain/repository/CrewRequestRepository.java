package revi1337.onsquad.crew_request.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.result.CrewRequestWithCrewResult;
import revi1337.onsquad.crew_request.domain.result.CrewRequestWithMemberResult;

public interface CrewRequestRepository {

    CrewRequest save(CrewRequest crewRequest);

    CrewRequest saveAndFlush(CrewRequest crewRequest);

    Optional<CrewRequest> findById(Long id);

    Optional<CrewRequest> findByCrewIdAndMemberId(Long crewId, Long memberId);

    List<CrewRequestWithCrewResult> fetchAllWithSimpleCrewByMemberId(Long memberId);

    Page<CrewRequestWithMemberResult> fetchCrewRequests(Long crewId, Pageable pageable);

    void deleteById(Long id);

    int deleteByMemberId(Long memberId);

    int deleteByCrewIdAndMemberId(Long crewId, Long memberId);

    int deleteByCrewIdIn(List<Long> crewIds);

}
