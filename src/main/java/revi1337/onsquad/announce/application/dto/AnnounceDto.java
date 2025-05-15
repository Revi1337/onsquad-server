package revi1337.onsquad.announce.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.crew_member.application.dto.SimpleCrewMemberDto;

public record AnnounceDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        boolean fixed,
        LocalDateTime fixedAt,
        SimpleCrewMemberDto writer
) {
    public static AnnounceDto from(AnnounceDomainDto domainDto) {
        return new AnnounceDto(
                domainDto.id(),
                domainDto.title().getValue(),
                domainDto.content(),
                domainDto.createdAt(),
                domainDto.fixed(),
                domainDto.fixedAt(),
                SimpleCrewMemberDto.from(domainDto.writer())
        );
    }
}
