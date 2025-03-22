package revi1337.onsquad.squad_comment.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_comment.application.SquadCommentService;
import revi1337.onsquad.squad_comment.presentation.dto.request.CreateSquadCommentRequest;
import revi1337.onsquad.squad_comment.presentation.dto.response.SquadCommentResponse;

@RequiredArgsConstructor
@RequestMapping("/api/crews")
@RestController
public class SquadCommentController {

    private final SquadCommentService squadCommentService;

    @PostMapping("/{crewId}/squads/{squadId}/comments")
    public ResponseEntity<RestResponse<String>> addComment(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Valid @RequestBody CreateSquadCommentRequest request,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadCommentService.addComment(
                authMemberAttribute.id(), crewId, squadId, request.content()
        );

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @PostMapping("/{crewId}/squads/{squadId}/comments/{parentId}")
    public ResponseEntity<RestResponse<String>> addCommentReply(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PathVariable Long parentId,
            @Valid @RequestBody CreateSquadCommentRequest request,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        squadCommentService.addCommentReply(
                authMemberAttribute.id(), crewId, squadId, parentId, request.content()
        );

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/comments")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> fetchParentCommentsWithChildren(
            @Authenticate AuthMemberAttribute authMemberAttribute,
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Qualifier("parent") Pageable parentPageable,
            @RequestParam(required = false, defaultValue = "5") @Range(min = 0, max = 100) int childSize
    ) {
        List<SquadCommentResponse> commentsResponses = squadCommentService
                .fetchParentCommentsWithChildren(authMemberAttribute.id(), crewId, squadId, parentPageable, childSize)
                .stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/comments/{parentId}")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> findMoreChildComments(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @PathVariable Long parentId,
            @PageableDefault Pageable pageable,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<SquadCommentResponse> childComments = squadCommentService
                .findMoreChildComments(authMemberAttribute.id(), crewId, squadId, parentId, pageable).stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(childComments));
    }

    @GetMapping("/crews/{crewId}/squads/{squadId}/comments/all")
    public ResponseEntity<RestResponse<List<SquadCommentResponse>>> findAllComments(
            @PathVariable Long crewId,
            @PathVariable Long squadId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<SquadCommentResponse> commentsResponses = squadCommentService
                .findAllComments(authMemberAttribute.id(), crewId, squadId).stream()
                .map(SquadCommentResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
    }
}
