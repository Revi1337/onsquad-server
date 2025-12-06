package revi1337.onsquad.squad_participant.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_participant.domain.dto.SimpleSquadParticipantDomainDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;
import revi1337.onsquad.squad_participant.domain.entity.SquadParticipant;

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
    public SquadParticipant saveAndFlush(SquadParticipant squadParticipant) {
        return squadParticipantJpaRepository.saveAndFlush(squadParticipant);
    }

    @Override
    public Optional<SquadParticipant> findById(Long id) {
        return squadParticipantJpaRepository.findById(id);
    }

    @Override
    public Optional<SquadParticipant> findByIdWithSquad(Long id) {
        return squadParticipantJpaRepository.findByIdWithSquad(id);
    }

    @Override
    public Optional<SquadParticipant> findBySquadIdAndCrewMemberId(Long squadId, Long crewMemberId) {
        return squadParticipantJpaRepository.findBySquadIdAndCrewMemberId(squadId, crewMemberId);
    }

    @Override
    public List<SquadParticipantRequest> findSquadParticipantRequestsByMemberId(Long memberId) {
        return squadParticipantQueryDslRepository.findSquadParticipantRequestsByMemberId(memberId);
    }

    @Override
    public Page<SimpleSquadParticipantDomainDto> fetchAllBySquadId(Long squadId, Pageable pageable) {
        return squadParticipantQueryDslRepository.fetchAllBySquadId(squadId, pageable);
    }

    @Override
    public void deleteBySquadIdCrewMemberId(Long squadId, Long crewMemberId) {
        squadParticipantJpaRepository.deleteBySquadIdAndCrewMemberId(squadId, crewMemberId);
    }

    @Override
    public void deleteById(Long crewParticipantId) {
        squadParticipantJpaRepository.deleteById(crewParticipantId);
    }
}
