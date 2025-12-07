package revi1337.onsquad.squad_comment.application.policy;

import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.application.CommentMaskPolicy;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@Component
public class DefaultCommentMaskPolicy implements CommentMaskPolicy {

    @Override
    public SquadCommentDomainDto apply(SquadCommentDomainDto comment) {
        if (comment.deleted()) {
            return mask(comment);
        }
        return unmask(comment);
    }

    private SquadCommentDomainDto unmask(SquadCommentDomainDto c) {
        return new SquadCommentDomainDto(
                c.parentId(),
                c.id(),
                c.content(),
                c.deleted(),
                c.createdAt(),
                c.updatedAt(),
                c.writer()
        );
    }

    private SquadCommentDomainDto mask(SquadCommentDomainDto c) {
        return new SquadCommentDomainDto(
                c.parentId(),
                c.id(),
                "",
                c.deleted(),
                c.createdAt(),
                c.updatedAt(),
                null
        );
    }
}
