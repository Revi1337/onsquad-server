package revi1337.onsquad.crew_comment.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_comment.application.CrewCommentService;
import revi1337.onsquad.crew_comment.presentation.dto.request.CreateCrewCommentReplyRequest;
import revi1337.onsquad.crew_comment.presentation.dto.request.CreateCrewCommentRequest;
import revi1337.onsquad.crew_comment.presentation.dto.response.CrewCommentResponse;
import revi1337.onsquad.crew_comment.presentation.dto.response.SimpleCrewCommentResponse;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/crew")
@RestController
public class CrewCommentController {

    private final CrewCommentService crewCommentService;

    @PostMapping("/comment/new")
    public ResponseEntity<RestResponse<SimpleCrewCommentResponse>> addComment(
            @RequestParam Long crewId,
            @Valid @RequestBody CreateCrewCommentRequest request,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        SimpleCrewCommentResponse commentResponse = SimpleCrewCommentResponse.from(
                crewCommentService.addComment(authenticatedMember.toDto().getId(), crewId, request.toDto())
        );

        return ResponseEntity.ok().body(RestResponse.created(commentResponse));
    }

    @PostMapping("/comment/reply/new")
    public ResponseEntity<RestResponse<SimpleCrewCommentResponse>> addCommentReply(
            @RequestParam Long crewId,
            @Valid @RequestBody CreateCrewCommentReplyRequest request,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        SimpleCrewCommentResponse commentResponse = SimpleCrewCommentResponse.from(
                crewCommentService.addCommentReply(authenticatedMember.toDto().getId(), crewId, request.toDto())
        );

        return ResponseEntity.ok().body(RestResponse.created(commentResponse));
    }

    @GetMapping("/comments")
    public ResponseEntity<RestResponse<List<CrewCommentResponse>>> findParentComments(
            @RequestParam Long crewId,
            @Qualifier("parent") Pageable parentPageable,
            @RequestParam(required = false, defaultValue = "5") @Range(min = 0, max = 100) Integer childSize,
            @Authenticate AuthenticatedMember ignored
    ) {
        List<CrewCommentResponse> commentsResponses = crewCommentService.findParentComments(crewId, parentPageable, childSize).stream()
                .map(CrewCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }

    @GetMapping("/comments/more")
    public ResponseEntity<RestResponse<List<CrewCommentResponse>>> findMoreChildComments(
            @RequestParam Long crewId,
            @RequestParam Long parentId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthenticatedMember ignored
    ) {
        List<CrewCommentResponse> childComments = crewCommentService.findMoreChildComments(crewId, parentId, pageable).stream()
                .map(CrewCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(childComments));
    }

    @GetMapping("/comment/all")
    public ResponseEntity<RestResponse<List<CrewCommentResponse>>> findAllComments(
            @RequestParam Long crewId,
            @Authenticate AuthenticatedMember ignored
    ) {
        List<CrewCommentResponse> commentsResponses = crewCommentService.findAllComments(crewId).stream()
                .map(CrewCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }
}
