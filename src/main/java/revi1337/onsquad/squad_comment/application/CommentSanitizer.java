package revi1337.onsquad.squad_comment.application;

import java.util.List;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@Component
public class CommentSanitizer {

    private final CommentSanitizeStrategy sanitizeStrategy;

    public CommentSanitizer(CommentSanitizeStrategy stackBasedDfsCommentSanitizer) {
        this.sanitizeStrategy = stackBasedDfsCommentSanitizer;
    }

    public List<SquadCommentDomainDto> sanitize(List<SquadCommentDomainDto> comments) {
        return sanitizeStrategy.sanitize(comments);
    }
}
