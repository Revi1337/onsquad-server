package revi1337.onsquad.squad_comment.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_comment.application.SquadCommentService;
import revi1337.onsquad.squad_comment.presentation.dto.request.CreateSquadCommentRequest;
import revi1337.onsquad.squad_comment.presentation.dto.response.SquadCommentResponse;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SquadCommentController {

    private final SquadCommentService squadCommentService;

    @PostMapping("/crews/{crewId}/squads/{squadId}/comments")
    public ResponseEntity<RestResponse<String>> addComment(
            @Authenticate AuthenticatedMember authenticatedMember,
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Valid @RequestBody CreateSquadCommentRequest request
    ) {
        Long commentId = squadCommentService.addComment(
                authenticatedMember.toDto().getId(), crewId, squadId, request.content()
        );
        URI uri = buildLocationPath(
                "/crews/{crewId}/squads/{squadId}/comments/{commentId}", crewId, squadId, commentId
        );

        return ResponseEntity.ok().location(uri).body(RestResponse.created());
    }

    @PostMapping("/crews/{crewId}/squads/{squadId}/comments/{commentId}")
    public ResponseEntity<RestResponse<String>> addCommentReply(
            @Authenticate AuthenticatedMember authenticatedMember,
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PathVariable Long commentId,
            @Valid @RequestBody CreateSquadCommentRequest request
    ) {
        Long childCommentId = squadCommentService.addCommentReply(
                authenticatedMember.toDto().getId(), crewId, squadId, commentId, request.content()
        );
        URI uri = buildLocationPath(
                "/crews/{crewId}/squads/{squadId}/comments/{commentId}", crewId, squadId, childCommentId
        );

        return ResponseEntity.ok().location(uri).body(RestResponse.created());
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/comments")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> fetchParentCommentsWithChildren(
            @Authenticate AuthenticatedMember authenticatedMember,
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Qualifier("parent") Pageable parentPageable,
            @RequestParam(required = false, defaultValue = "5") @Range(min = 0, max = 100) int childSize
    ) {
        List<SquadCommentResponse> commentsResponses = squadCommentService
                .fetchParentCommentsWithChildren(
                        authenticatedMember.toDto().getId(), crewId, squadId, parentPageable, childSize).stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }

    @GetMapping("/squad/comments")
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

    @GetMapping("/squad/comment/all")
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

    private URI buildLocationPath(String path, Object... args) {
        return UriComponentsBuilder.fromPath(path)
                .buildAndExpand(args)
                .toUri();
    }
}
