//package revi1337.onsquad.squad_comment.dto.request;
//
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.Positive;
//import revi1337.onsquad.squad_comment.dto.CreateSquadCommentReplyDto;
//
//public record CreateSquadCommentReplyRequest(
//        @Positive Long parentCommentId,
//        @NotEmpty String content
//) {
//    public CreateSquadCommentReplyDto toDto() {
//        return new CreateSquadCommentReplyDto(parentCommentId, content);
//    }
//}
