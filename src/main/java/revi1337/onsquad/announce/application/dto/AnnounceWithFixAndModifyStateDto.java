package revi1337.onsquad.announce.application.dto;

import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;

public record AnnounceWithFixAndModifyStateDto(
        Boolean canFix,
        Boolean canModify,
        AnnounceDto announce
) {
    public static AnnounceWithFixAndModifyStateDto from(Boolean canFix, Boolean canModify,
                                                        AnnounceDomainDto domainDto) {
        return new AnnounceWithFixAndModifyStateDto(
                canFix,
                canModify,
                AnnounceDto.from(domainDto)
        );
    }
}
