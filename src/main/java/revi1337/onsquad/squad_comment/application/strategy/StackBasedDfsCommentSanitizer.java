package revi1337.onsquad.squad_comment.application.strategy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.squad_comment.application.CommentMaskPolicy;
import revi1337.onsquad.squad_comment.application.CommentSanitizeStrategy;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

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
@Deprecated
@RequiredArgsConstructor
public class StackBasedDfsCommentSanitizer implements CommentSanitizeStrategy {

    private final CommentMaskPolicy maskPolicy;

    @Override
    public List<SquadCommentResult> sanitize(List<SquadCommentResult> comments) {
        List<SquadCommentResult> sanitized = new ArrayList<>();
        Map<Long, SquadCommentResult> lookupTable = new HashMap<>();
        Deque<SquadCommentResult> queue = new ArrayDeque<>();

        for (SquadCommentResult parent : comments) {
            SquadCommentResult recreatedParent = maskPolicy.apply(parent);
            lookupTable.put(recreatedParent.id(), recreatedParent);
            sanitized.add(recreatedParent);
            queue.add(parent);
        }

        while (!queue.isEmpty()) {
            SquadCommentResult current = queue.pop();
            SquadCommentResult recreatedCurrent = lookupTable.get(current.id());
            for (SquadCommentResult reply : current.replies()) {
                SquadCommentResult recreatedReply = maskPolicy.apply(reply);
                lookupTable.put(recreatedReply.id(), recreatedReply);
                recreatedCurrent.addReply(recreatedReply);
                queue.add(reply);
            }
        }

        return sanitized;
    }
}
