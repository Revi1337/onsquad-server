package revi1337.onsquad.announce.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnnounceResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        boolean fixed,
        LocalDateTime fixedAt,
        SimpleMemberResponse writer
) {

    public static AnnounceResponse from(AnnounceResult result) {
        return new AnnounceResponse(
                result.id(),
                result.title().getValue(),
                result.content(),
                result.createdAt(),
                result.fixed(),
                result.fixedAt(),
                SimpleMemberResponse.from(result.writer())
        );
    }
}
