//package revi1337.onsquad.squad_comment.dto.response;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;
//import revi1337.onsquad.squad_comment.dto.SquadCommentsDto;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public record SquadCommentsResponse(
//        Long parentCommentId,
//        Long commentId,
//        String content,
//        LocalDateTime createdAt,
//        LocalDateTime updatedAt,
//        SimpleMemberInfoResponse memberInfo,
//        List<SquadCommentsResponse> replies
//) {
//    public static SquadCommentsResponse from(SquadCommentsDto squadCommentsDto) {
//        return new SquadCommentsResponse(
//                squadCommentsDto.parentCommentId(),
//                squadCommentsDto.commentId(),
//                squadCommentsDto.content(),
//                squadCommentsDto.createdAt(),
//                squadCommentsDto.updatedAt(),
//                SimpleMemberInfoResponse.from(squadCommentsDto.memberInfo()),
//                squadCommentsDto.replies().stream()
//                        .map(SquadCommentsResponse::from)
//                        .collect(Collectors.toList())
//        );
//    }
//}