package revi1337.onsquad.crew_comment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CrewCommentRepositoryImpl implements CrewCommentRepository {

    private final CrewCommentJpaRepository crewCommentJpaRepository;
    private final CrewCommentQueryDslRepository crewCommentQueryDslRepository;
    private final CrewCommentJdbcRepository crewCommentJdbcRepository;

    @Override
    public CrewComment save(CrewComment crewComment) {
        return crewCommentJpaRepository.save(crewComment);
    }

    @Override
    public Optional<CrewComment> findById(Long id) {
        return crewCommentJpaRepository.findById(id);
    }

    @Override
    public List<CrewComment> findCommentsWithMemberByCrewId(Long crewId) {
        return crewCommentQueryDslRepository.findCommentsWithMemberByCrewId(crewId);
    }

    @Override
    public List<CrewComment> findLimitedParentCommentsByCrewId(Long crewId, Pageable pageable) {
        return crewCommentQueryDslRepository.findLimitedParentCommentsByCrewId(crewId, pageable);
    }

    @Override
    public List<CrewComment> findLimitedChildCommentsByParentIdIn(List<Long> parentIds, Integer childrenSize) {
        return crewCommentJdbcRepository.findLimitedChildCommentsByParentIdIn(parentIds, childrenSize);
    }
}
