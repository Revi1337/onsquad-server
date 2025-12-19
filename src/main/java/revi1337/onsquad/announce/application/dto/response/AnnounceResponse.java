package revi1337.onsquad.announce.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.crew_member.application.response.SimpleCrewMemberResponse;

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

    public static AnnounceResponse from(AnnounceResult domainDto) {
        return new AnnounceResponse(
                domainDto.id(),
                domainDto.title().getValue(),
                domainDto.content(),
                domainDto.createdAt(),
                domainDto.fixed(),
                domainDto.fixedAt(),
                SimpleCrewMemberResponse.from(domainDto.writer())
        );
    }
}
