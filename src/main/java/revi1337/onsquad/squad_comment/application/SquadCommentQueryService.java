package revi1337.onsquad.squad_comment.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.squad.application.SquadAccessPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_comment.application.response.SquadCommentResponse;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadCommentQueryService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final SquadAccessPolicy squadAccessPolicy;
    private final SquadCommentRepository commentRepository;
    private final CommentCombinator commentCombinator;
    private final CommentSanitizer commentSanitizer;

    public List<SquadCommentResponse> fetchInitialComments(Long memberId, Long squadId, Pageable pageable, int childSize) {
        Squad squad = squadAccessPolicy.ensureSquadExistsAndGet(squadId);
        crewMemberAccessPolicy.ensureMemberInCrew(memberId, squad.getCrewId());

        List<SquadCommentResult> parents = commentRepository.fetchAllParentsBySquadId(squadId, pageable);
        if (parents.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> parentIds = parents.stream().map(SquadCommentResult::id).toList();
        List<SquadCommentResult> replies = commentRepository.fetchAllChildrenByParentIdIn(parentIds, childSize);
        List<SquadCommentResult> comments = commentCombinator.combine(parents, replies);

        return commentSanitizer.sanitize(comments).stream()
                .map(SquadCommentResponse::from)
                .toList();
    }

    public List<SquadCommentResponse> fetchMoreChildren(Long memberId, Long squadId, Long parentId, Pageable pageable) {
        Squad squad = squadAccessPolicy.ensureSquadExistsAndGet(squadId);
        crewMemberAccessPolicy.ensureMemberInCrew(memberId, squad.getCrewId());

        return commentRepository.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable).stream()
                .map(SquadCommentResponse::from)
                .toList();
    }
}
