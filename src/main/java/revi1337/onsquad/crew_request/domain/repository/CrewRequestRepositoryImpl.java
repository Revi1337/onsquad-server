package revi1337.onsquad.crew_request.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_request.domain.dto.CrewRequestWithCrewDomainDto;
import revi1337.onsquad.crew_request.domain.dto.CrewRequestWithMemberDomainDto;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;

@RequiredArgsConstructor
@Repository
public class CrewRequestRepositoryImpl implements CrewRequestRepository {

    private final CrewRequestJpaRepository crewRequestJpaRepository;
    private final CrewRequestQueryDslRepository crewRequestQueryDslRepository;

    @Override
    public CrewRequest save(CrewRequest crewRequest) {
        return crewRequestJpaRepository.save(crewRequest);
    }

    @Override
    public CrewRequest saveAndFlush(CrewRequest crewRequest) {
        return crewRequestJpaRepository.saveAndFlush(crewRequest);
    }

    @Override
    public Optional<CrewRequest> findById(Long id) {
        return crewRequestJpaRepository.findById(id);
    }

    @Override
    public Optional<CrewRequest> findWithCrewById(Long id) {
        return crewRequestJpaRepository.findWithCrewById(id);
    }

    @Override
    public void deleteById(Long id) {
        crewRequestJpaRepository.deleteById(id);
    }

    @Override
    public Optional<CrewRequest> findByCrewIdAndMemberId(Long crewId, Long memberId) {
        return crewRequestJpaRepository.findByCrewIdAndMemberId(crewId, memberId);
    }

    @Override
    public List<CrewRequestWithCrewDomainDto> fetchAllWithSimpleCrewByMemberId(Long memberId) {
        return crewRequestQueryDslRepository.fetchAllWithSimpleCrewByMemberId(memberId);
    }

    @Override
    public Page<CrewRequestWithMemberDomainDto> fetchCrewRequests(Long crewId, Pageable pageable) {
        return crewRequestQueryDslRepository.fetchCrewRequests(crewId, pageable);
    }
}
