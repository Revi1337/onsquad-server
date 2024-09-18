package revi1337.onsquad.crew_comment.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_comment.application.CrewCommentService;
import revi1337.onsquad.crew_comment.dto.request.CreateCrewCommentReplyRequest;
import revi1337.onsquad.crew_comment.dto.request.CreateCrewCommentRequest;
import revi1337.onsquad.crew_comment.dto.response.CrewCommentResponse;
import revi1337.onsquad.crew_comment.dto.response.CrewCommentsResponse;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/crew")
@RestController
public class CrewCommentController {

    private final CrewCommentService crewCommentService;

    @PostMapping("/comment/new")
    public ResponseEntity<RestResponse<CrewCommentResponse>> addComment(
            @RequestParam String crewName,
            @Valid @RequestBody CreateCrewCommentRequest request,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        CrewCommentResponse commentResponse = CrewCommentResponse.from(
                crewCommentService.addComment(crewName, request.toDto(), authenticatedMember.toDto().getId())
        );

        return ResponseEntity.ok().body(RestResponse.created(commentResponse));
    }

    @PostMapping("/comment/reply/new")
    public ResponseEntity<RestResponse<CrewCommentResponse>> addCommentReply(
            @RequestParam String crewName,
            @Valid @RequestBody CreateCrewCommentReplyRequest request,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        CrewCommentResponse commentResponse = CrewCommentResponse.from(
                crewCommentService.addCommentReply(crewName, request.toDto(), authenticatedMember.toDto().getId())
        );

        return ResponseEntity.ok().body(RestResponse.created(commentResponse));
    }

    @GetMapping("/comments")
    public ResponseEntity<RestResponse<List<CrewCommentsResponse>>> findComments(
            @RequestParam String crewName,
            @Qualifier("parent") Pageable parentPageable,
            @RequestParam(required = false, defaultValue = "5") @Range(min = 0, max = 100) Integer childSize,
            @Authenticate AuthenticatedMember ignored
    ) {
        List<CrewCommentsResponse> commentsResponses = crewCommentService.findComments(crewName, parentPageable, childSize).stream()
                .map(CrewCommentsResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }

    @GetMapping("/comment/all")
    public ResponseEntity<RestResponse<List<CrewCommentsResponse>>> findAllComments(
            @RequestParam String crewName,
            @Authenticate AuthenticatedMember ignored
    ) {
        List<CrewCommentsResponse> commentsResponses = crewCommentService.findAllComments(crewName).stream()
                .map(CrewCommentsResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }
}
