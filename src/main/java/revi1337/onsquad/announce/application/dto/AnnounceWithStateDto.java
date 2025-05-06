package revi1337.onsquad.announce.application.dto;

import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;

public record AnnounceWithStateDto(
        Boolean canFix,
        Boolean canModify,
        AnnounceDto announce
) {
    public static AnnounceWithStateDto from(Boolean canFix, Boolean canModify, AnnounceDomainDto domainDto) {
        return new AnnounceWithStateDto(
                canFix,
                canModify,
                AnnounceDto.from(domainDto)
        );
    }
}
