//package revi1337.onsquad.squad_comment.dto;
//
//import revi1337.onsquad.member.domain.Member;
//import revi1337.onsquad.member.dto.SimpleMemberInfoDto;
//import revi1337.onsquad.squad_comment.domain.SquadComment;
//
//import java.time.LocalDateTime;
//
//public record SquadCommentDto(
//        Long id,
//        String content,
//        LocalDateTime createdAt,
//        LocalDateTime updatedAt,
//        SimpleMemberInfoDto memberInfo
//) {
//    public static SquadCommentDto from(SquadComment comment, Member member) {
//        return new SquadCommentDto(
//                comment.getId(),
//                comment.getContent(),
//                comment.getCreatedAt(),
//                comment.getUpdatedAt(),
//                SimpleMemberInfoDto.from(member)
//        );
//    }
//}
