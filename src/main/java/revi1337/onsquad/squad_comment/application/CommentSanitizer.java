package revi1337.onsquad.squad_comment.application;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@Component
public class CommentSanitizer {

    public List<SquadCommentDomainDto> sanitizeIteratively(List<SquadCommentDomainDto> comments) {
        List<SquadCommentDomainDto> results = new ArrayList<>();
        for (SquadCommentDomainDto parent : comments) {
            SquadCommentDomainDto recreatedParent = recreate(parent);
            recreatedParent.addReplies(recreates(parent.replies()));
            results.add(recreatedParent);
        }

        return results;
    }

    public List<SquadCommentDomainDto> sanitizeUsingStack(List<SquadCommentDomainDto> comments) {
        List<SquadCommentDomainDto> result = new ArrayList<>();
        Map<Long, SquadCommentDomainDto> lookupTable = new HashMap<>();
        Deque<SquadCommentDomainDto> stack = new ArrayDeque<>();

        for (SquadCommentDomainDto parents : comments) {
            SquadCommentDomainDto recreatedParent = recreate(parents);
            lookupTable.put(recreatedParent.id(), recreatedParent);
            result.add(recreatedParent);
            stack.push(parents);
        }

        while (!stack.isEmpty()) {
            SquadCommentDomainDto comment = stack.pop();
            SquadCommentDomainDto cachedParent = lookupTable.get(comment.id());

            for (SquadCommentDomainDto reply : comment.replies()) {
                SquadCommentDomainDto recreatedReply = recreate(reply);
                lookupTable.put(recreatedReply.id(), recreatedReply);
                cachedParent.addReply(recreatedReply);
                stack.push(reply);
            }
        }

        return result;
    }

    public List<SquadCommentDomainDto> sanitizeRecursively(List<SquadCommentDomainDto> comments) {
        List<SquadCommentDomainDto> results = new ArrayList<>();
        for (SquadCommentDomainDto comment : comments) {
            SquadCommentDomainDto recreated = recreate(comment);
            List<SquadCommentDomainDto> sanitizedReplies = sanitizeRecursively(comment.replies());
            recreated.addReplies(sanitizedReplies);
            results.add(recreated);
        }

        return results;
    }

    public SquadCommentDomainDto sanitizeSingleRecursively(SquadCommentDomainDto comment) {
        SquadCommentDomainDto recreated = recreate(comment);
        List<SquadCommentDomainDto> replies = new ArrayList<>();
        for (SquadCommentDomainDto reply : comment.replies()) {
            replies.add(sanitizeSingleRecursively(reply));
        }
        recreated.addReplies(replies);

        return recreated;
    }

    private List<SquadCommentDomainDto> recreates(List<SquadCommentDomainDto> comment) {
        return comment.stream()
                .map(this::recreate)
                .toList();
    }

    private SquadCommentDomainDto recreate(SquadCommentDomainDto comment) {
        if (comment.deleted()) {
            return mask(comment);
        }

        return unmask(comment);
    }

    private SquadCommentDomainDto unmask(SquadCommentDomainDto comment) {
        return new SquadCommentDomainDto(
                comment.parentId(),
                comment.id(),
                comment.content(),
                comment.deleted(),
                comment.createdAt(),
                comment.updatedAt(),
                comment.writer()
        );
    }

    private SquadCommentDomainDto mask(SquadCommentDomainDto comment) {
        return new SquadCommentDomainDto(
                comment.parentId(),
                comment.id(),
                "",
                comment.deleted(),
                comment.createdAt(),
                comment.updatedAt(),
                null
        );
    }
}
