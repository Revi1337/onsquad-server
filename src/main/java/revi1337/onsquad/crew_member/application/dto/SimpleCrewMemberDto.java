package revi1337.onsquad.crew_member.application.dto;

import revi1337.onsquad.crew_member.domain.dto.SimpleCrewMemberDomainDto;

public record SimpleCrewMemberDto(
        Long id,
        String nickname,
        String role
) {
    public static SimpleCrewMemberDto from(SimpleCrewMemberDomainDto simpleCrewMemberDomainDto) {
        return new SimpleCrewMemberDto(
                simpleCrewMemberDomainDto.id(),
                simpleCrewMemberDomainDto.nickname().getValue(),
                simpleCrewMemberDomainDto.role().getText()
        );
    }
}
