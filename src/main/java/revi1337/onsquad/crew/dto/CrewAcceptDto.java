package revi1337.onsquad.crew.dto;

import revi1337.onsquad.member.dto.MemberDto;

public record CrewAcceptDto(
        String crewName
) {
    public CrewDto toDto(MemberDto memberDto) {
        return CrewDto.of(crewName, memberDto);
    }
}
