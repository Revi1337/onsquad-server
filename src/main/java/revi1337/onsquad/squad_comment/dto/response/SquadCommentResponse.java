//package revi1337.onsquad.squad_comment.dto.response;
//
//import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;
//import revi1337.onsquad.squad_comment.dto.SquadCommentDto;
//
//import java.time.LocalDateTime;
//
//public record SquadCommentResponse(
//        Long id,
//        String content,
//        LocalDateTime createdAt,
//        LocalDateTime updatedAt,
//        SimpleMemberInfoResponse memberInfo
//) {
//    public static SquadCommentResponse from(SquadCommentDto squadCommentDto) {
//        return new SquadCommentResponse(
//                squadCommentDto.id(),
//                squadCommentDto.content(),
//                squadCommentDto.createdAt(),
//                squadCommentDto.updatedAt(),
//                SimpleMemberInfoResponse.from(squadCommentDto.memberInfo())
//        );
//    }
//}
