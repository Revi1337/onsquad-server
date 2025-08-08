package revi1337.onsquad.squad_comment.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@Component
public class CommentCombinator {

    public List<SquadCommentDomainDto> combine(List<SquadCommentDomainDto> parents, List<SquadCommentDomainDto> children) {
        Map<Long, SquadCommentDomainDto> parentMap = parents.stream()
                .collect(Collectors.toMap(SquadCommentDomainDto::id, comment -> comment, (one, two) -> one, LinkedHashMap::new));

        children.forEach(child -> {
            SquadCommentDomainDto squadCommentDomainDto = parentMap.get(child.parentId());
            squadCommentDomainDto.replies().add(child);
        });

        return new ArrayList<>(parentMap.values());
    }

    @Deprecated
    public List<SquadCommentDomainDto> makeHierarchy(List<SquadCommentDomainDto> comments) {
        List<SquadCommentDomainDto> commentList = new ArrayList<>();
        Map<Long, SquadCommentDomainDto> hashMap = new HashMap<>();
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
