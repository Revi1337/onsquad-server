package revi1337.onsquad.crew_hashtag.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.crew_hashtag.application.dto.CrewHashtagDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewHashtagResponse(
        Long id,
        String hashtag
) {
    public static CrewHashtagResponse from(CrewHashtagDto crewHashtagDto) {
        return new CrewHashtagResponse(
                crewHashtagDto.id(),
                crewHashtagDto.hashtag()
        );
    }
}
