package revi1337.onsquad.squad_comment.domain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@RequiredArgsConstructor
@Repository
public class SquadCommentRepositoryImpl implements SquadCommentRepository {

    private final SquadCommentJpaRepository squadCommentJpaRepository;
    private final SquadCommentQueryDslRepository squadCommentQueryDslRepository;
    private final SquadCommentJdbcRepository squadCommentJdbcRepository;

    @Override
    public SquadComment save(SquadComment comment) {
        return squadCommentJpaRepository.save(comment);
    }

    @Override
    public Optional<SquadComment> findById(Long id) {
        return squadCommentJpaRepository.findById(id);
    }

    @Override
    public Optional<SquadComment> findByIdAndSquadId(Long id, Long squadId) {
        return squadCommentJpaRepository.findByIdAndSquadId(id, squadId);
    }

    @Override
    public Optional<SquadComment> findWithSquadByIdAndSquadId(Long id, Long squadId) {
        return squadCommentJpaRepository.findWithSquadByIdAndSquadId(id, squadId);
    }

    @Override
    public Optional<SquadComment> findByIdAndSquadIdAndCrewId(Long id, Long squadId, Long crewId) {
        return squadCommentJpaRepository.findByIdAndSquadIdAndCrewId(id, squadId, crewId);
    }

    @Override
    public Map<Long, SquadCommentDomainDto> fetchAllParentsBySquadId(Long squadId, Pageable pageable) {
        return squadCommentQueryDslRepository.fetchAllParentsBySquadId(squadId, pageable);
    }

    @Override
    public List<SquadCommentDomainDto> fetchAllChildrenByParentIdIn(Collection<Long> parentIds, int childSize) {
        return squadCommentJdbcRepository.fetchAllChildrenByParentIdIn(parentIds, childSize);
    }

    @Override
    public List<SquadCommentDomainDto> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable) {
        return squadCommentQueryDslRepository.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable);
    }

    @Override
    public List<SquadCommentDomainDto> findAllWithMemberBySquadId(Long squadId) {
        return squadCommentQueryDslRepository.findAllWithMemberBySquadId(squadId);
    }
}
