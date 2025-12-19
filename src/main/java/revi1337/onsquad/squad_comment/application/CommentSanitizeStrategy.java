package revi1337.onsquad.squad_comment.application;

import java.util.List;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

public interface CommentSanitizeStrategy {

    List<SquadCommentResult> sanitize(List<SquadCommentResult> comments);

}
