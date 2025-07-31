package revi1337.onsquad.squad_comment.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@Component
public class CommentCombinator {

    public List<SquadCommentDomainDto> combine(Map<Long, SquadCommentDomainDto> parents, List<SquadCommentDomainDto> children) {
        if (parents.isEmpty()) {
            return new ArrayList<>();
        }
        children.forEach(child -> {
            SquadCommentDomainDto squadCommentDomainDto = parents.get(child.parentId());
            squadCommentDomainDto.replies().add(child);
        });

        return new ArrayList<>(parents.values());
    }

    @Deprecated
    public List<SquadCommentDomainDto> makeHierarchy(List<SquadCommentDomainDto> comments) {
        List<SquadCommentDomainDto> commentList = new ArrayList<>();
        Map<Long, SquadCommentDomainDto> hashMap = new HashMap<>();
        comments.forEach(comment -> {
            hashMap.put(comment.commentId(), comment);
            if (comment.parentId() != null) {
                hashMap.get(comment.parentId()).replies().add(comment);
            } else {
                commentList.add(comment);
            }
        });

        return commentList;
    }
}
