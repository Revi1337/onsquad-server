package revi1337.onsquad.comment.domain;

import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentQueryRepository {

    /**
     * 모든 최상위 댓글들과 대댓글을 모두 한번에 가져온다.
     * @param crewName
     * @return
     */
    List<Comment> findCommentsByCrewName(Name crewName);

    /**
     * 모든 최상위 댓글들을 가져온다.
     * @param crewName
     * @return
     * @see #findParentCommentsByCrewNameUsingPageable(Name, Pageable)
     */
    List<Comment> findParentCommentsByCrewName(Name crewName);

    /**
     * 페이징처리에 맞게 최상위 댓글들을 가져온다.
     * @param crewName
     * @param pageable
     * @return
     */
    List<Comment> findParentCommentsByCrewNameUsingPageable(Name crewName, Pageable pageable);

    /**
     * 최상위 댓글 id 들을 부모로 갖고있는 모든 대댓글들을 가져온다.
     * @param parentIds
     * @return
     * @see #findGroupedChildCommentsByParentIdIn(List, Pageable)
     */
    List<Comment> findChildCommentsByParentIdIn(List<Long> parentIds);

    /**
     * 최상위 댓글 id 들을 부모로 갖고있는 모든 대댓글들을 group by 하여 가져온다.
     * @param parentIds
     * @return
     */
    Map<Comment, List<Comment>> findGroupedChildCommentsByParentIdIn(List<Long> parentIds, Pageable childPageable);

    /**
     * 댓글 id 를 통해 댓글 정보를 가져온다.
     * @param commentId
     * @return
     */
    Optional<Comment> findCommentById(Long commentId);

}
