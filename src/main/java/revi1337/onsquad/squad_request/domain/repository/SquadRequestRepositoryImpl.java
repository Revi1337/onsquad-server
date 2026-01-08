package revi1337.onsquad.squad_request.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.result.SquadRequestResult;

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
    public Optional<SquadRequest> findBySquadIdAndMemberId(Long squadId, Long memberId) {
        return squadRequestJpaRepository.findBySquadIdAndMemberId(squadId, memberId);
    }

    @Override
    public Page<SquadRequestResult> fetchAllBySquadId(Long squadId, Pageable pageable) {
        return squadRequestQueryDslRepository.fetchAllBySquadId(squadId, pageable);
    }

    @Override
    public List<SquadRequest> fetchMyRequests(Long memberId) {
        return squadRequestQueryDslRepository.fetchMySquadRequestsWithDetails(memberId);
    }

    @Override
    public void deleteById(Long id) {
        squadRequestJpaRepository.deleteById(id);
    }

    @Override
    public int deleteByMemberId(Long memberId) {
        return squadRequestJpaRepository.deleteByMemberId(memberId);
    }

    @Override
    public int deleteBySquadIdMemberId(Long squadId, Long memberId) {
        return squadRequestJpaRepository.deleteBySquadIdAndMemberId(squadId, memberId);
    }

    @Override
    public int deleteBySquadIdIn(List<Long> squadIds) {
        return squadRequestJpaRepository.deleteBySquadIdIn(squadIds);
    }
}
