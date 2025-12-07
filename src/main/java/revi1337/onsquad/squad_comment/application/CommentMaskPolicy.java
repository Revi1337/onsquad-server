package revi1337.onsquad.squad_comment.application;

import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

public interface CommentMaskPolicy {

    SquadCommentDomainDto apply(SquadCommentDomainDto comment);

}
