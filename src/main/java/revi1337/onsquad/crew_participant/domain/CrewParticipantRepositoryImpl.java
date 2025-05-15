package revi1337.onsquad.crew_participant.domain;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_participant.domain.dto.CrewRequestWithCrewDomainDto;
import revi1337.onsquad.crew_participant.domain.dto.CrewRequestWithMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewParticipantRepositoryImpl implements CrewParticipantRepository {

    private final CrewParticipantJpaRepository crewParticipantJpaRepository;
    private final CrewParticipantQueryDslRepository crewParticipantQueryDslRepository;

    @Override
    public CrewParticipant save(CrewParticipant crewParticipant) {
        return crewParticipantJpaRepository.save(crewParticipant);
    }

    @Override
    public CrewParticipant saveAndFlush(CrewParticipant crewParticipant) {
        return crewParticipantJpaRepository.saveAndFlush(crewParticipant);
    }

    @Override
    public Optional<CrewParticipant> findById(Long id) {
        return crewParticipantJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        crewParticipantJpaRepository.deleteById(id);
    }

    @Override
    public Optional<CrewParticipant> findByCrewIdAndMemberId(Long crewId, Long memberId) {
        return crewParticipantJpaRepository.findByCrewIdAndMemberId(crewId, memberId);
    }

    @Override
    public List<CrewRequestWithCrewDomainDto> fetchAllWithSimpleCrewByMemberId(Long memberId) {
        return crewParticipantQueryDslRepository.fetchAllWithSimpleCrewByMemberId(memberId);
    }

    @Override
    public Page<CrewRequestWithMemberDomainDto> fetchCrewRequests(Long crewId, Pageable pageable) {
        return crewParticipantQueryDslRepository.fetchCrewRequests(crewId, pageable);
    }
}
