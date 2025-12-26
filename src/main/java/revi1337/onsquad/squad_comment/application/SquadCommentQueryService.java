package revi1337.onsquad.squad_comment.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_comment.application.response.SquadCommentResponse;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SquadCommentQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadCommentAccessor squadCommentAccessor;
    private final CommentCombinator commentCombinator;
    private final CommentSanitizer commentSanitizer;

    public List<SquadCommentResponse> fetchInitialComments(Long memberId, Long squadId, Pageable pageable, int childSize) {
        Squad squad = squadAccessor.getById(squadId);
        crewMemberAccessor.validateMemberInCrew(memberId, squad.getCrewId());

        List<SquadCommentResult> parents = squadCommentAccessor.fetchAllParentsBySquadId(squadId, pageable);
        if (parents.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> parentIds = parents.stream().map(SquadCommentResult::id).toList();
        List<SquadCommentResult> replies = squadCommentAccessor.fetchAllChildrenByParentIdIn(parentIds, childSize);
        List<SquadCommentResult> comments = commentCombinator.combine(parents, replies);

        return commentSanitizer.sanitize(comments).stream()
                .map(SquadCommentResponse::from)
                .toList();
    }

    public List<SquadCommentResponse> fetchMoreChildren(Long memberId, Long squadId, Long parentId, Pageable pageable) {
        Squad squad = squadAccessor.getById(squadId);
        crewMemberAccessor.validateMemberInCrew(memberId, squad.getCrewId());

        return squadCommentAccessor.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable).stream()
                .map(SquadCommentResponse::from)
                .toList();
    }
}
