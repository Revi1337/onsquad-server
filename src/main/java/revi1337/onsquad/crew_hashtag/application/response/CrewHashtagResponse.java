package revi1337.onsquad.crew_hashtag.application.response;

import revi1337.onsquad.crew_hashtag.domain.result.CrewHashtagResult;

public record CrewHashtagResponse(
        Long id,
        String hashtag
) {

    public static CrewHashtagResponse from(CrewHashtagResult crewHashtagResult) {
        return new CrewHashtagResponse(
                crewHashtagResult.id(),
                crewHashtagResult.hashTagType().getText()
        );
    }
}
