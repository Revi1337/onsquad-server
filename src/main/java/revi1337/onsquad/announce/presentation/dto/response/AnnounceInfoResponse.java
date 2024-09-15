package revi1337.onsquad.announce.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;
import revi1337.onsquad.crew_member.presentation.dto.response.SimpleCrewMemberResponse;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnnounceInfoResponse(
        Boolean canModify,
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        SimpleCrewMemberResponse memberInfo
) {
    public static AnnounceInfoResponse from(AnnounceInfoDto announceInfoDto) {
        return new AnnounceInfoResponse(
                announceInfoDto.canModify(),
                announceInfoDto.id(),
                announceInfoDto.title(),
                announceInfoDto.content(),
                announceInfoDto.createdAt(),
                SimpleCrewMemberResponse.from(announceInfoDto.memberInfo())
        );
    }
}
