package revi1337.onsquad.squad_participant.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;

@RequiredArgsConstructor
@Repository
public class SquadParticipantRepositoryImpl implements SquadParticipantRepository {

    private final SquadParticipantJpaRepository squadParticipantJpaRepository;
    private final SquadParticipantJdbcRepository squadParticipantJdbcRepository;
    private final SquadParticipantQueryDslRepository squadParticipantQueryDslRepository;


    @Override
    public SquadParticipant save(SquadParticipant squadParticipant) {
        return squadParticipantJpaRepository.save(squadParticipant);
    }

    @Override
    public List<SquadParticipant> saveAll(List<SquadParticipant> squadParticipants) {
        return squadParticipantJpaRepository.saveAll(squadParticipants);
    }

    @Override
    public SquadParticipant saveAndFlush(SquadParticipant squadParticipant) {
        return squadParticipantJpaRepository.saveAndFlush(squadParticipant);
    }

    @Override
    public void delete(SquadParticipant squadParticipant) {
        squadParticipantJpaRepository.delete(squadParticipant);
    }

    @Override
    public void deleteById(Long crewParticipantId) {
        squadParticipantJpaRepository.deleteById(crewParticipantId);
    }

    @Override
    public Optional<SquadParticipant> findByCrewIdAndSquadIdAndMemberId(Long crewId, Long squadId, Long memberId) {
        return squadParticipantJpaRepository.findByCrewIdAndSquadIdAndMemberId(crewId, squadId, memberId);
    }

    @Override
    public Optional<SquadParticipant> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return squadParticipantJpaRepository.findBySquadIdAndCrewMemberId(squadId, crewMemberId);
    }

    @Override
    public void upsertSquadParticipant(Long squadId, Long crewMemberId, LocalDateTime now) {
        squadParticipantJdbcRepository.upsertSquadParticipant(squadId, crewMemberId, now);
    }

    @Override
    public List<SquadParticipantRequest> findSquadParticipantRequestsByMemberId(Long memberId) {
        return squadParticipantQueryDslRepository.findSquadParticipantRequestsByMemberId(memberId);
    }
}
