package revi1337.onsquad.squad_comment.application.strategy;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.application.CommentMaskPolicy;
import revi1337.onsquad.squad_comment.application.CommentSanitizeStrategy;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@RequiredArgsConstructor
@Component
public class RecursiveCommentSanitizer implements CommentSanitizeStrategy {

    private final CommentMaskPolicy maskPolicy;

    @Override
    public List<SquadCommentDomainDto> sanitize(List<SquadCommentDomainDto> comments) {
        List<SquadCommentDomainDto> sanitizedComments = new ArrayList<>();
        for (SquadCommentDomainDto comment : comments) {
            sanitizedComments.add(sanitizeRecursively(comment));
        }
        return sanitizedComments;
    }

    private SquadCommentDomainDto sanitizeRecursively(SquadCommentDomainDto original) {
        SquadCommentDomainDto sanitized = maskPolicy.apply(original);

        List<SquadCommentDomainDto> sanitizedChildren = new ArrayList<>();
        for (SquadCommentDomainDto child : original.replies()) {
            sanitizedChildren.add(sanitizeRecursively(child));
        }

        sanitized.addReplies(sanitizedChildren);
        return sanitized;
    }
}
