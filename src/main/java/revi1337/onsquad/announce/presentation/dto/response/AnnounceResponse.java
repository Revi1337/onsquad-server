package revi1337.onsquad.announce.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import revi1337.onsquad.announce.application.dto.AnnounceDto;
import revi1337.onsquad.crew_member.presentation.dto.response.SimpleCrewMemberResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnnounceResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        boolean fixed,
        LocalDateTime fixedAt,
        SimpleCrewMemberResponse writer
) {
    public static AnnounceResponse from(AnnounceDto announceDto) {
        return new AnnounceResponse(
                announceDto.id(),
                announceDto.title(),
                announceDto.content(),
                announceDto.createdAt(),
                announceDto.fixed(),
                announceDto.fixedAt(),
                SimpleCrewMemberResponse.from(announceDto.writer())
        );
    }
}
