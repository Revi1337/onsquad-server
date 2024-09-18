//package revi1337.onsquad.squad_comment.domain;
//
//import org.springframework.data.domain.Pageable;
//import revi1337.onsquad.crew.domain.vo.Name;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//public interface CommentQueryRepository {
//
//    /**
//     * 모든 최상위 댓글들과 대댓글을 모두 한번에 가져온다.
//     * @param crewName
//     * @return
//     */
//    List<SquadComment> findCommentsByCrewName(Name crewName);
//
//    /**
//     * 모든 최상위 댓글들을 가져온다.
//     * @param crewName
//     * @return
//     * @see #findLimitedParentCommentsByCrewName(Name, Pageable)
//     */
//    List<SquadComment> findParentCommentsByCrewName(Name crewName);
//
//    /**
//     * 페이징처리에 맞게 최상위 댓글들을 가져온다.
//     * @param crewName
//     * @param pageable
//     * @return
//     */
//    List<SquadComment> findLimitedParentCommentsByCrewName(Name crewName, Pageable pageable);
//
//    /**
//     * 최상위 댓글 id 들을 부모로 갖고있는 모든 대댓글들을 가져온다.
//     * @param parentIds
//     * @return
//     * @see #findGroupedChildCommentsByParentIdIn(List)
//     */
//    List<SquadComment> findChildCommentsByParentIdIn(List<Long> parentIds);
//
//    /**
//     * 최상위 댓글 id 들을 부모로 갖고있는 모든 대댓글들을 group by 하여 가져온다.
//     * @param parentIds
//     * @return
//     */
//    Map<SquadComment, List<SquadComment>> findGroupedChildCommentsByParentIdIn(List<Long> parentIds);
//
//    /**
//     * 최상위 댓글 id 들을 부모로 갖고 있는 대댓글들을 childrenSize 만큼 가져온다.
//     * <p> NativeQuery 를 사용하므로, Comment 에서 Member 를 가져오려면 N + 1 가 발생한다.
//     * <p> 이를 해결하기 위해 Comment 에 속한 Member id 들을 추출 후, 영속성 컨텍스트에 추가하기 위한 쿼리가 한방 더 나간다.
//     * @param parentIds
//     * @param childrenSize
//     * @return
//     */
//    List<SquadComment> findLimitedChildCommentsByParentIdIn(List<Long> parentIds, Integer childrenSize);
//
//    /**
//     * 댓글 id 를 통해 댓글 정보를 가져온다.
//     * @param commentId
//     * @return
//     */
//    Optional<SquadComment> findCommentById(Long commentId);
//
//}
