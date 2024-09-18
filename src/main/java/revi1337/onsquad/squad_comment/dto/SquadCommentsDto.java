//package revi1337.onsquad.squad_comment.dto;
//
//import revi1337.onsquad.member.dto.SimpleMemberInfoDto;
//import revi1337.onsquad.squad_comment.domain.SquadComment;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public record SquadCommentsDto(
//        Long parentCommentId,
//        Long commentId,
//        String content,
//        LocalDateTime createdAt,
//        LocalDateTime updatedAt,
//        SimpleMemberInfoDto memberInfo,
//        List<SquadCommentsDto> replies
//) {
//    public SquadCommentsDto(Long parentCommentId, Long commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt, SimpleMemberInfoDto memberInfo) {
//        this(parentCommentId, commentId, content, createdAt, updatedAt, memberInfo, new ArrayList<>());
//    }
//
//    public static SquadCommentsDto from(SquadComment comment) {
//        return new SquadCommentsDto(
//                comment.getParent() != null ? comment.getParent().getId() : null,
//                comment.getId(),
//                comment.getContent(),
//                comment.getCreatedAt(),
//                comment.getUpdatedAt(),
//                SimpleMemberInfoDto.from(comment.getMember())
//        );
//    }
//}