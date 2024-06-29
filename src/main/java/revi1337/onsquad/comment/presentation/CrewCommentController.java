package revi1337.onsquad.comment.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.comment.application.CrewCommentService;
import revi1337.onsquad.comment.dto.request.CreateCommentReplyRequest;
import revi1337.onsquad.comment.dto.request.CreateCommentRequest;
import revi1337.onsquad.comment.dto.response.CommentResponse;
import revi1337.onsquad.comment.dto.response.CommentsResponse;
import revi1337.onsquad.common.dto.RestResponse;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/crew")
@RestController
public class CrewCommentController {

    private final CrewCommentService crewCommentService;

    @PostMapping("/comment/new")
    public ResponseEntity<RestResponse<CommentResponse>> addComment(
            @Valid @RequestBody CreateCommentRequest commentRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        CommentResponse commentResponse = CommentResponse.from(
                crewCommentService.addComment(commentRequest.toDto(), authenticatedMember.toDto().getId())
        );

        return ResponseEntity.ok().body(RestResponse.success(commentResponse));
    }

    @PostMapping("/comment/reply/new")
    public ResponseEntity<RestResponse<CommentResponse>> addCommentReply(
            @Valid @RequestBody CreateCommentReplyRequest commentReplyRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        CommentResponse commentResponse = CommentResponse.from(
                crewCommentService.addCommentReply(commentReplyRequest.toDto(), authenticatedMember.toDto().getId())
        );

        return ResponseEntity.ok().body(RestResponse.success(commentResponse));
    }

    @GetMapping("/comments")
    public ResponseEntity<RestResponse<List<CommentsResponse>>> findComments(
            @Qualifier("parent") Pageable parentPageable,
            @Qualifier("child") Pageable childPageable,
            @RequestParam String crewName,
            @Authenticate AuthenticatedMember ignored
    ) {
        List<CommentsResponse> commentsResponses = crewCommentService.findComments(crewName, parentPageable, childPageable)
                .stream()
                .map(CommentsResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }

    @GetMapping("/comment/all")
    public ResponseEntity<RestResponse<List<CommentsResponse>>> findAllComments(
            @RequestParam String crewName,
            @Authenticate AuthenticatedMember ignored
    ) {
        List<CommentsResponse> commentsResponses = crewCommentService.findAllComments(crewName)
                .stream()
                .map(CommentsResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }
}
