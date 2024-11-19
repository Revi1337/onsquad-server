package revi1337.onsquad.announce.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.crew_member.application.dto.SimpleCrewMemberDto;

public record AnnounceInfoDto(
        Boolean canModify,
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        boolean fixed,
        LocalDateTime fixedAt,
        SimpleCrewMemberDto memberInfo
) {
    public static AnnounceInfoDto from(AnnounceInfoDomainDto announceInfoDomainDto) {
        return new AnnounceInfoDto(
                announceInfoDomainDto.canModify(),
                announceInfoDomainDto.id(),
                announceInfoDomainDto.title().getValue(),
                announceInfoDomainDto.content(),
                announceInfoDomainDto.createdAt(),
                announceInfoDomainDto.fixed(),
                announceInfoDomainDto.fixedAt(),
                SimpleCrewMemberDto.from(announceInfoDomainDto.memberInfo())
        );
    }
}
