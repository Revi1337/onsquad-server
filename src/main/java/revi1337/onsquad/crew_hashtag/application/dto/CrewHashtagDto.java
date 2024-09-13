package revi1337.onsquad.crew_hashtag.application.dto;

import revi1337.onsquad.crew_hashtag.domain.dto.CrewHashtagDomainDto;

public record CrewHashtagDto(
        Long id,
        String hashtag
) {
    public static CrewHashtagDto from(CrewHashtagDomainDto crewHashtagDomainDto) {
        return new CrewHashtagDto(
                crewHashtagDomainDto.id(),
                crewHashtagDomainDto.hashTagType().getText()
        );
    }
}
