package revi1337.onsquad.squad_comment.application.strategy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.application.CommentMaskPolicy;
import revi1337.onsquad.squad_comment.application.CommentSanitizeStrategy;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

/**
 * Implements the {@link CommentSanitizeStrategy} using a <b>stack-based iterative Depth-First Search (DFS)</b> algorithm.
 * <p>
 * This strategy traverses the comment tree depth-first using an explicit stack (implemented via {@link Deque}), avoiding recursion to prevent stack overflow
 * errors in deeply nested comment threads. The algorithm processes each comment and its replies in depth-first order while maintaining the tree structure
 * through a lookup table.
 * <p>
 * The iterative approach with an explicit stack provides the same traversal order as recursive DFS but is safer for production environments where comment depth
 * is unbounded. This ensures reliable processing regardless of thread nesting depth while applying the masking policy to sanitize comments.
 */
@RequiredArgsConstructor
@Component
public class StackBasedDfsCommentSanitizer implements CommentSanitizeStrategy {

    private final CommentMaskPolicy maskPolicy;

    @Override
    public List<SquadCommentDomainDto> sanitize(List<SquadCommentDomainDto> comments) {
        List<SquadCommentDomainDto> sanitized = new ArrayList<>();
        Map<Long, SquadCommentDomainDto> lookupTable = new HashMap<>();
        Deque<SquadCommentDomainDto> queue = new ArrayDeque<>();

        for (SquadCommentDomainDto parent : comments) {
            SquadCommentDomainDto recreatedParent = maskPolicy.apply(parent);
            lookupTable.put(recreatedParent.id(), recreatedParent);
            sanitized.add(recreatedParent);
            queue.add(parent);
        }

        while (!queue.isEmpty()) {
            SquadCommentDomainDto current = queue.pop();
            SquadCommentDomainDto recreatedCurrent = lookupTable.get(current.id());
            for (SquadCommentDomainDto reply : current.replies()) {
                SquadCommentDomainDto recreatedReply = maskPolicy.apply(reply);
                lookupTable.put(recreatedReply.id(), recreatedReply);
                recreatedCurrent.addReply(recreatedReply);
                queue.add(reply);
            }
        }

        return sanitized;
    }
}
