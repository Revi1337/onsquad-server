package revi1337.onsquad.squad_comment.application;

import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

public interface CommentMaskPolicy {

    SquadCommentResult apply(SquadCommentResult comment);

}
