package revi1337.onsquad.squad_comment.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_comment.application.SquadCommentService;
import revi1337.onsquad.squad_comment.presentation.dto.request.CreateSquadCommentRequest;
import revi1337.onsquad.squad_comment.presentation.dto.response.SimpleSquadCommentResponse;
import revi1337.onsquad.squad_comment.presentation.dto.response.SquadCommentResponse;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/squad")
@RestController
public class SquadCommentController {

    private final SquadCommentService squadCommentService;

    @PostMapping("/comment")
    public ResponseEntity<RestResponse<SimpleSquadCommentResponse>> addCommentReply(
            @RequestParam Long crewId,
            @RequestParam Long squadId,
            @Valid @RequestBody CreateSquadCommentRequest request,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        SimpleSquadCommentResponse commentResponse = SimpleSquadCommentResponse.from(
                squadCommentService.addComment(authenticatedMember.toDto().getId(), crewId, squadId, request.toDto())
        );

        return ResponseEntity.ok().body(RestResponse.created(commentResponse));
    }

    @GetMapping("/comments")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> findParentComments(
            @RequestParam Long crewId,
            @RequestParam Long squadId,
            @Qualifier("parent") Pageable parentPageable,
            @RequestParam(required = false, defaultValue = "5") @Range(min = 0, max = 100) Integer childSize,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SquadCommentResponse> commentsResponses = squadCommentService.findParentComments(
                        authenticatedMember.toDto().getId(), crewId, squadId, parentPageable, childSize).stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }

    @GetMapping("/comments/more")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> findMoreChildComments(
            @RequestParam Long crewId,
            @RequestParam Long squadId,
            @RequestParam Long parentId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SquadCommentResponse> childComments = squadCommentService.findMoreChildComments(
                        authenticatedMember.toDto().getId(), crewId, squadId, parentId, pageable).stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(childComments));
    }

    @GetMapping("/comment/all")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> findAllComments(
            @RequestParam Long crewId,
            @RequestParam Long squadId,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<SquadCommentResponse> commentsResponses = squadCommentService.findAllComments(
                        authenticatedMember.toDto().getId(), crewId, squadId).stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }
}
