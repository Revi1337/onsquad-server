package revi1337.onsquad.comment.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.comment.application.CrewCommentService;
import revi1337.onsquad.comment.dto.request.CreateCommentRequest;
import revi1337.onsquad.comment.dto.response.CommentResponse;
import revi1337.onsquad.common.dto.RestResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/crew")
@RestController
public class CrewCommentController {

    private final CrewCommentService crewCommentService;

    @PostMapping("/comment/new")
    public ResponseEntity<RestResponse<CommentResponse>> addComment(
            @RequestParam String crewName,
            @Valid @RequestBody CreateCommentRequest commentRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        CommentResponse commentResponse = CommentResponse.from(
                crewCommentService.addComment(crewName, commentRequest.toDto(), authenticatedMember.toDto().getId())
        );

        return ResponseEntity.ok().body(RestResponse.success(commentResponse));
    }
}