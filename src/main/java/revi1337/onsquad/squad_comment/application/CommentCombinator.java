package revi1337.onsquad.squad_comment.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

@Deprecated
public class CommentCombinator {

    public List<SquadCommentResult> combine(List<SquadCommentResult> parents, List<SquadCommentResult> children) {
        Map<Long, SquadCommentResult> parentMap = parents.stream()
                .collect(Collectors.toMap(SquadCommentResult::id, comment -> comment, (one, two) -> one, LinkedHashMap::new));

        children.forEach(child -> {
            SquadCommentResult squadCommentResult = parentMap.get(child.parentId());
            squadCommentResult.replies().add(child);
        });

        return new ArrayList<>(parentMap.values());
    }

    @Deprecated
    public List<SquadCommentResult> makeHierarchy(List<SquadCommentResult> comments) {
        List<SquadCommentResult> commentList = new ArrayList<>();
        Map<Long, SquadCommentResult> hashMap = new HashMap<>();
        comments.forEach(comment -> {
            hashMap.put(comment.id(), comment);
            if (comment.parentId() != null) {
                hashMap.get(comment.parentId()).replies().add(comment);
            } else {
                commentList.add(comment);
            }
        });

        return commentList;
    }
}
