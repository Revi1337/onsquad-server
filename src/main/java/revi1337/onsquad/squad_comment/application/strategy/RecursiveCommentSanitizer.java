package revi1337.onsquad.squad_comment.application.strategy;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.application.CommentMaskPolicy;
import revi1337.onsquad.squad_comment.application.CommentSanitizeStrategy;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

@RequiredArgsConstructor
@Component
public class RecursiveCommentSanitizer implements CommentSanitizeStrategy {

    private final CommentMaskPolicy maskPolicy;

    @Override
    public List<SquadCommentResult> sanitize(List<SquadCommentResult> comments) {
        List<SquadCommentResult> sanitizedComments = new ArrayList<>();
        for (SquadCommentResult comment : comments) {
            sanitizedComments.add(sanitizeRecursively(comment));
        }
        return sanitizedComments;
    }

    private SquadCommentResult sanitizeRecursively(SquadCommentResult original) {
        SquadCommentResult sanitized = maskPolicy.apply(original);

        List<SquadCommentResult> sanitizedChildren = new ArrayList<>();
        for (SquadCommentResult child : original.replies()) {
            sanitizedChildren.add(sanitizeRecursively(child));
        }

        sanitized.addReplies(sanitizedChildren);
        return sanitized;
    }
}
