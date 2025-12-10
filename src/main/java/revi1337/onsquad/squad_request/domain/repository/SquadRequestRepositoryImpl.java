package revi1337.onsquad.squad_request.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestDomainDto;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestWithSquadAndCrewDomainDto;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

@RequiredArgsConstructor
@Repository
public class SquadRequestRepositoryImpl implements SquadRequestRepository {

    private final SquadRequestJpaRepository squadRequestJpaRepository;
    private final SquadRequestQueryDslRepository squadRequestQueryDslRepository;

    @Override
    public SquadRequest save(SquadRequest squadRequest) {
        return squadRequestJpaRepository.save(squadRequest);
    }

    @Override
    public SquadRequest saveAndFlush(SquadRequest squadRequest) {
        return squadRequestJpaRepository.saveAndFlush(squadRequest);
    }

    @Override
    public Optional<SquadRequest> findById(Long id) {
        return squadRequestJpaRepository.findById(id);
    }

    @Override
    public Optional<SquadRequest> findByIdWithSquad(Long id) {
        return squadRequestJpaRepository.findByIdWithSquad(id);
    }

    @Override
    public Optional<SquadRequest> findBySquadIdAndMemberId(Long squadId, Long memberId) {
        return squadRequestJpaRepository.findBySquadIdAndMemberId(squadId, memberId);
    }

    @Override
    public List<SquadRequestWithSquadAndCrewDomainDto> findSquadParticipantRequestsByMemberId(Long memberId) {
        return squadRequestQueryDslRepository.findSquadParticipantRequestsByMemberIdV2(memberId);
    }

    @Override
    public Page<SquadRequestDomainDto> fetchAllBySquadId(Long squadId, Pageable pageable) {
        return squadRequestQueryDslRepository.fetchAllBySquadId(squadId, pageable);
    }

    @Override
    public void deleteBySquadIdMemberId(Long squadId, Long memberId) {
        squadRequestJpaRepository.deleteBySquadIdAndMemberId(squadId, memberId);
    }

    @Override
    public void deleteById(Long id) {
        squadRequestJpaRepository.deleteById(id);
    }
}
