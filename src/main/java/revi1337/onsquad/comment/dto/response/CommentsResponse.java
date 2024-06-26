package revi1337.onsquad.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.comment.dto.CommentsDto;
import revi1337.onsquad.member.dto.response.MemberInfoResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommentsResponse(
        Long parentCommentId,
        Long commentId,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        MemberInfoResponse memberInfo,
        List<CommentsResponse> replies
) {
    public static CommentsResponse from(CommentsDto commentsDto) {
        return new CommentsResponse(
                commentsDto.parentCommentId(),
                commentsDto.commentId(),
                commentsDto.comment(),
                commentsDto.createdAt(),
                commentsDto.updatedAt(),
                MemberInfoResponse.from(commentsDto.memberInfo()),
                commentsDto.replies().stream()
                        .map(CommentsResponse::from)
                        .collect(Collectors.toList())
        );
    }
}