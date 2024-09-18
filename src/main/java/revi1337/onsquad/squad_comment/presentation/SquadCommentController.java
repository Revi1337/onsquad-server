//package revi1337.onsquad.squad_comment.presentation;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.hibernate.validator.constraints.Range;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import revi1337.onsquad.auth.config.Authenticate;
//import revi1337.onsquad.auth.dto.AuthenticatedMember;
//import revi1337.onsquad.common.dto.RestResponse;
//import revi1337.onsquad.squad_comment.application.SquadCommentService;
//import revi1337.onsquad.squad_comment.dto.request.CreateSquadCommentReplyRequest;
//import revi1337.onsquad.squad_comment.dto.request.CreateSquadCommentRequest;
//import revi1337.onsquad.squad_comment.dto.response.SquadCommentResponse;
//import revi1337.onsquad.squad_comment.dto.response.SquadCommentsResponse;
//
//import java.util.List;
//
//@Validated
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/squad")
//@RestController
//public class SquadCommentController {
//
//    private final SquadCommentService squadCommentService;
//
//    @PostMapping("/comment/new")
//    public ResponseEntity<RestResponse<SquadCommentResponse>> addComment(
//            @RequestParam String crewName,
//            @RequestParam Long squadId,
//            @Valid @RequestBody CreateSquadCommentRequest request,
//            @Authenticate AuthenticatedMember authenticatedMember
//    ) {
//        SquadCommentResponse commentResponse = SquadCommentResponse.from(
//                squadCommentService.addComment(
//                        crewName,
//                        squadId,
//                        request.toDto(),
//                        authenticatedMember.toDto().getId()
//                )
//        );
//
//        return ResponseEntity.ok().body(RestResponse.created(commentResponse));
//    }
//
//    @PostMapping("/comment/reply/new")
//    public ResponseEntity<RestResponse<SquadCommentResponse>> addCommentReply(
//            @RequestParam String crewName,
//            @RequestParam Long squadId,
//            @Valid @RequestBody CreateSquadCommentReplyRequest request,
//            @Authenticate AuthenticatedMember authenticatedMember
//    ) {
////        SquadCommentResponse commentResponse = SquadCommentResponse.from(
////                squadCommentService.addCommentReply(
////                        crewName,
////                        squadId,
////                        request.toDto(),
////                        authenticatedMember.toDto().getId()
////                )
////        );
////
////        return ResponseEntity.ok().body(RestResponse.created(commentResponse));
//        return ResponseEntity.ok(RestResponse.noContent());
//    }
//
//    @GetMapping("/comments")
//    public ResponseEntity<RestResponse<List<SquadCommentsResponse>>> findComments(
//            @RequestParam String crewName,
//            @RequestParam Long squadId,
//            @Qualifier("parent") Pageable parentPageable,
//            @RequestParam(required = false, defaultValue = "5") @Range(min = 0, max = 100) Integer childSize,
//            @Authenticate AuthenticatedMember ignored
//    ) {
////        List<SquadCommentsResponse> commentsResponses = squadCommentService.findComments(crewName, squadId, parentPageable, childSize)
////                .stream()
////                .map(CommentsResponse::from)
////                .toList();
////
////        return ResponseEntity.ok().body(RestResponse.success(commentsResponses));
//        return ResponseEntity.ok(RestResponse.noContent());
//    }
//
//    @GetMapping("/comment/all")
//    public ResponseEntity<RestResponse<List<SquadCommentsResponse>>> findAllComments(
//            @RequestParam String crewName,
//            @RequestParam Long squadId,
//            @Authenticate AuthenticatedMember ignored
//    ) {
////        List<SquadCommentsResponse> commentsResponses = squadCommentService.findAllComments(crewName, squadId)
////                .stream()
////                .map(SquadCommentsResponse::from)
////                .toList();
//
//        return ResponseEntity.ok(RestResponse.noContent());
//    }
//}
