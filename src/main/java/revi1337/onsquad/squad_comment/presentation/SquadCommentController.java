package revi1337.onsquad.squad_comment.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.CurrentMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_comment.application.SquadCommentCommandService;
import revi1337.onsquad.squad_comment.application.SquadCommentQueryService;
import revi1337.onsquad.squad_comment.presentation.dto.request.CommentCreateRequest;
import revi1337.onsquad.squad_comment.presentation.dto.response.SquadCommentResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class SquadCommentController {

    private final SquadCommentCommandService squadCommentCommandService;
    private final SquadCommentQueryService squadCommentQueryService;

    @PostMapping("/{crewId}/squads/{squadId}/comments")
    public ResponseEntity<RestResponse<String>> add(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Valid @RequestBody CommentCreateRequest request,
            @Authenticate CurrentMember currentMember
    ) {
        squadCommentCommandService.add(currentMember.id(), crewId, squadId, request.content());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @PostMapping("/{crewId}/squads/{squadId}/replies/{parentId}")
    public ResponseEntity<RestResponse<String>> addReply(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PathVariable Long parentId,
            @Valid @RequestBody CommentCreateRequest request,
            @Authenticate CurrentMember currentMember
    ) {
        squadCommentCommandService.addReply(currentMember.id(), crewId, squadId, parentId, request.content());

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/{crewId}/squads/{squadId}/comments")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> fetchInitialComments(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false, defaultValue = "5") @Range(min = 0, max = 100) int childSize,
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadCommentResponse> commentsResponses = squadCommentQueryService.fetchInitialComments(currentMember.id(), crewId, squadId, pageable, childSize)
                .stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }

    @GetMapping("/{crewId}/squads/{squadId}/replies/{parentId}")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> fetchMoreChildren(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PathVariable Long parentId,
            @PageableDefault Pageable pageable,
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadCommentResponse> childComments = squadCommentQueryService.fetchMoreChildren(currentMember.id(), crewId, squadId, parentId, pageable).stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(childComments));
    }

    @Deprecated
    @GetMapping("/{crewId}/squads/{squadId}/comments/all")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> findAllComments(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadCommentResponse> commentsResponses = squadCommentQueryService.findAllComments(currentMember.id(), crewId, squadId).stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }

    @PatchMapping("/{crewId}/squads/{squadId}/comments/{commentId}")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> updateComment(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentCreateRequest request,
            @Authenticate CurrentMember currentMember
    ) {
        squadCommentCommandService.update(currentMember.id(), crewId, squadId, commentId, request.content());

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
