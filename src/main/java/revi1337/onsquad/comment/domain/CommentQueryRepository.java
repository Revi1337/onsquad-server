package revi1337.onsquad.comment.domain;

import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;

public interface CommentQueryRepository {

    List<Comment> findCommentsByCrewName(Name crewName);

}
