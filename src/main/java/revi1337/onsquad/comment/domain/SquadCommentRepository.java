package revi1337.onsquad.comment.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class SquadCommentRepository implements CommentQueryExecutor {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findCommentsByCrewName(Name crewName) {
        return List.of();
    }

    @Override
    public List<Comment> findParentCommentsByCrewName(Name crewName) {
        return List.of();
    }

    @Override
    public List<Comment> findLimitedParentCommentsByCrewName(Name crewName, Pageable pageable) {
        return List.of();
    }

    @Override
    public List<Comment> findChildCommentsByParentIdIn(List<Long> parentIds) {
        return List.of();
    }

    @Override
    public Map<Comment, List<Comment>> findGroupedChildCommentsByParentIdIn(List<Long> parentIds) {
        return Map.of();
    }

    @Override
    public List<Comment> findLimitedChildCommentsByParentIdIn(List<Long> parentIds, Integer childrenSize) {
        return List.of();
    }

    @Override
    public Optional<Comment> findCommentById(Long commentId) {
        return Optional.empty();
    }
}
