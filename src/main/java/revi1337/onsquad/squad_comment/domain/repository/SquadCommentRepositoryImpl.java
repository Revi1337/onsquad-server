package revi1337.onsquad.squad_comment.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

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
    public List<SquadCommentDomainDto> fetchAllParentsBySquadId(Long squadId, Pageable pageable) {
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
}
