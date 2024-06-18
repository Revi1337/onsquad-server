package revi1337.onsquad.crew.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.crew.dto.CrewDto;
import revi1337.onsquad.member.dto.MemberDto;

public record CrewJoinRequest(
        @NotEmpty String crewName
) {
    public CrewDto toDto(MemberDto memberDto) {
        return CrewDto.of(crewName, memberDto);
    }
}
