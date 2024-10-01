package revi1337.onsquad.crew_participant.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_participant.domain.dto.CrewParticipantRequest;
import revi1337.onsquad.crew_participant.domain.dto.SimpleCrewParticipantRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CrewParticipantRepositoryImpl implements CrewParticipantRepository {

    private final CrewParticipantJpaRepository crewParticipantJpaRepository;
    private final CrewParticipantJdbcRepository crewParticipantJdbcRepository;
    private final CrewParticipantQueryDslRepository crewParticipantQueryDslRepository;

    @Override
    public CrewParticipant save(CrewParticipant crewParticipant) {
        return crewParticipantJpaRepository.save(crewParticipant);
    }

    @Override
    public List<CrewParticipant> saveAll(List<CrewParticipant> crewParticipants) {
        return crewParticipantJpaRepository.saveAll(crewParticipants);
    }

    @Override
    public CrewParticipant saveAndFlush(CrewParticipant crewParticipant) {
        return crewParticipantJpaRepository.saveAndFlush(crewParticipant);
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
    public void upsertCrewParticipant(Long crewId, Long memberId, LocalDateTime now) {
        crewParticipantJdbcRepository.upsertCrewParticipant(crewId, memberId, now);
    }

    @Override
    public List<CrewParticipantRequest> findMyCrewRequests(Long memberId) {
        return crewParticipantQueryDslRepository.findMyCrewRequests(memberId);
    }

    @Override
    public List<SimpleCrewParticipantRequest> findCrewRequestsInCrew(Long crewId) {
        return crewParticipantQueryDslRepository.findCrewRequestsInCrew(crewId);
    }
}
