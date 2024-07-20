package revi1337.onsquad.comment.presentation;

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
import revi1337.onsquad.comment.application.SquadCommentService;
import revi1337.onsquad.comment.dto.request.CreateCommentReplyRequest;
import revi1337.onsquad.comment.dto.request.CreateCommentRequest;
import revi1337.onsquad.comment.dto.response.CommentResponse;
import revi1337.onsquad.comment.dto.response.CommentsResponse;
import revi1337.onsquad.common.dto.RestResponse;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/squad")
@RestController
public class SquadCommentController {

    private final SquadCommentService squadCommentService;

    @PostMapping("/comment/new")
    public ResponseEntity<RestResponse<CommentResponse>> addComment(
            @RequestParam String crewName,
            @RequestParam Long squadId,
            @Valid @RequestBody CreateCommentRequest commentRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        CommentResponse commentResponse = CommentResponse.from(
                squadCommentService.addComment(
                        crewName,
                        squadId,
                        commentRequest.toDto(),
                        authenticatedMember.toDto().getId()
                )
        );

        return ResponseEntity.ok().body(RestResponse.created(commentResponse));
    }

    @PostMapping("/comment/reply/new")
    public ResponseEntity<RestResponse<CommentResponse>> addCommentReply(
            @RequestParam String crewName,
            @RequestParam Long squadId,
            @Valid @RequestBody CreateCommentReplyRequest commentReplyRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        CommentResponse commentResponse = CommentResponse.from(
                squadCommentService.addCommentReply(
                        crewName,
                        squadId,
                        commentReplyRequest.toDto(),
                        authenticatedMember.toDto().getId()
                )
        );

        return ResponseEntity.ok().body(RestResponse.created(commentResponse));
    }

    @GetMapping("/comments")
    public ResponseEntity<RestResponse<List<CommentsResponse>>> findComments(
            @RequestParam String crewName,
            @RequestParam Long squadId,
            @Qualifier("parent") Pageable parentPageable,
            @RequestParam(required = false, defaultValue = "5") @Range(min = 0, max = 100) Integer childSize,
            @Authenticate AuthenticatedMember ignored
    ) {
        List<CommentsResponse> commentsResponses = squadCommentService.findComments(crewName, squadId, parentPageable, childSize)
                .stream()
                .map(CommentsResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }

    @GetMapping("/comment/all")
    public ResponseEntity<RestResponse<List<CommentsResponse>>> findAllComments(
            @RequestParam String crewName,
            @RequestParam Long squadId,
            @Authenticate AuthenticatedMember ignored
    ) {
        List<CommentsResponse> commentsResponses = squadCommentService.findAllComments(crewName, squadId)
                .stream()
                .map(CommentsResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }
}
