//package revi1337.onsquad.squad_comment.domain;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//import revi1337.onsquad.crew.domain.vo.Name;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Repository
//public class SquadCommentRepository implements CommentQueryExecutor {
//
//    private final JPAQueryFactory jpaQueryFactory;
//
//    @Override
//    public List<SquadComment> findCommentsByCrewName(Name crewName) {
//        return List.of();
//    }
//
//    @Override
//    public List<SquadComment> findParentCommentsByCrewName(Name crewName) {
//        return List.of();
//    }
//
//    @Override
//    public List<SquadComment> findLimitedParentCommentsByCrewName(Name crewName, Pageable pageable) {
//        return List.of();
//    }
//
//    @Override
//    public List<SquadComment> findChildCommentsByParentIdIn(List<Long> parentIds) {
//        return List.of();
//    }
//
//    @Override
//    public Map<SquadComment, List<SquadComment>> findGroupedChildCommentsByParentIdIn(List<Long> parentIds) {
//        return Map.of();
//    }
//
//    @Override
//    public List<SquadComment> findLimitedChildCommentsByParentIdIn(List<Long> parentIds, Integer childrenSize) {
//        return List.of();
//    }
//
//    @Override
//    public Optional<SquadComment> findCommentById(Long commentId) {
//        return Optional.empty();
//    }
//}
