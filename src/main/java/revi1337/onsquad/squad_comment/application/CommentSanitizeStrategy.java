package revi1337.onsquad.squad_comment.application;

import java.util.List;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

public interface CommentSanitizeStrategy {

    List<SquadCommentDomainDto> sanitize(List<SquadCommentDomainDto> comments);

}
