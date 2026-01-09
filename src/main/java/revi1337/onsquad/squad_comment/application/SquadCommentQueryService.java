package revi1337.onsquad.squad_comment.application;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_comment.application.response.SquadCommentResponse;
import revi1337.onsquad.squad_comment.application.response.SquadCommentWithStateResponse;
import revi1337.onsquad.squad_comment.domain.SquadCommentPolicy;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_member.application.SquadMemberAccessor;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SquadCommentQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadMemberAccessor squadMemberAccessor;
    private final SquadCommentAccessor squadCommentAccessor;

    public List<SquadCommentWithStateResponse> fetchInitialComments(Long memberId, Long squadId, Pageable pageable) {
        Optional<SquadMember> meOpt = squadMemberAccessor.findByMemberIdAndSquadId(memberId, squadId);
        List<SquadComment> comments = squadCommentAccessor.fetchAllParentsBySquadId(squadId, pageable);
        if (meOpt.isPresent()) {
            return mapToResponsesAsSquadMember(meOpt.get(), comments);
        }

        Squad squad = squadAccessor.getById(squadId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrewId());
        return mapToResponsesAsCrewMember(me, comments);
    }

    public List<SquadCommentWithStateResponse> fetchMoreChildren(Long memberId, Long squadId, Long parentId, Pageable pageable) {
        Optional<SquadMember> meOpt = squadMemberAccessor.findByMemberIdAndSquadId(memberId, squadId);
        List<SquadComment> replies = squadCommentAccessor.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable);
        if (meOpt.isPresent()) {
            return mapToResponsesAsSquadMember(meOpt.get(), replies);
        }

        Squad squad = squadAccessor.getById(squadId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrewId());
        return mapToResponsesAsCrewMember(me, replies);
    }

    private List<SquadCommentWithStateResponse> mapToResponsesAsSquadMember(SquadMember me, List<SquadComment> comments) {
        return comments.stream()
                .map(comment -> {
                    boolean canDelete = SquadCommentPolicy.canDeleteComment(me, comment);
                    return new SquadCommentWithStateResponse(canDelete, SquadCommentResponse.from(comment));
                })
                .toList();
    }

    private List<SquadCommentWithStateResponse> mapToResponsesAsCrewMember(CrewMember me, List<SquadComment> comments) {
        return comments.stream()
                .map(comment -> {
                    boolean canDelete = false;
                    return new SquadCommentWithStateResponse(canDelete, SquadCommentResponse.from(comment));
                })
                .toList();
    }
}
