package revi1337.onsquad.comment.domain;

import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;
import java.util.Optional;

public interface CommentQueryRepository {

    List<Comment> findCommentsByCrewName(Name crewName);

    Optional<Comment> findCommentById(Long commentId);

}
