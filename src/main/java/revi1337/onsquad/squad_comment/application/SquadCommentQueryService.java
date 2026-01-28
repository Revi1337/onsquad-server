package revi1337.onsquad.squad_comment.application;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.dto.PageResponse;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SquadCommentQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadMemberAccessor squadMemberAccessor;
    private final SquadCommentAccessor squadCommentAccessor;

    public PageResponse<SquadCommentWithStateResponse> fetchInitialComments(Long memberId, Long squadId, Pageable pageable) {
        Optional<SquadMember> meOpt = squadMemberAccessor.findByMemberIdAndSquadId(memberId, squadId);
        Page<SquadComment> comments = squadCommentAccessor.fetchAllParentsBySquadId(squadId, pageable);
        if (meOpt.isPresent()) {
            return mapToResponsesAsSquadMember(meOpt.get(), comments);
        }

        Squad squad = squadAccessor.getById(squadId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrew().getId());
        return mapToResponsesAsCrewMember(me, comments);
    }

    public PageResponse<SquadCommentWithStateResponse> fetchMoreChildren(Long memberId, Long squadId, Long parentId, Pageable pageable) {
        SquadComment comment = squadCommentAccessor.getById(parentId);
        SquadCommentPolicy.ensureMatchSquad(comment, squadId);
        Optional<SquadMember> meOpt = squadMemberAccessor.findByMemberIdAndSquadId(memberId, squadId);
        Page<SquadComment> replies = squadCommentAccessor.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable);
        if (meOpt.isPresent()) {
            return mapToResponsesAsSquadMember(meOpt.get(), replies);
        }

        Squad squad = squadAccessor.getById(squadId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrew().getId());
        return mapToResponsesAsCrewMember(me, replies);
    }

    private PageResponse<SquadCommentWithStateResponse> mapToResponsesAsSquadMember(SquadMember me, Page<SquadComment> comments) {
        Page<SquadCommentWithStateResponse> response = comments.map(comment -> {
            boolean canDelete = SquadCommentPolicy.canDelete(comment, me);
            return new SquadCommentWithStateResponse(canDelete, SquadCommentResponse.from(comment));
        });

        return PageResponse.from(response);
    }

    private PageResponse<SquadCommentWithStateResponse> mapToResponsesAsCrewMember(CrewMember me, Page<SquadComment> comments) {
        Page<SquadCommentWithStateResponse> response = comments.map(comment -> {
            boolean canDelete = false;
            return new SquadCommentWithStateResponse(canDelete, SquadCommentResponse.from(comment));
        });

        return PageResponse.from(response);
    }
}
