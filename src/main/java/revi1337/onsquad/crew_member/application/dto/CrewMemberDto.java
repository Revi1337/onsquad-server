package revi1337.onsquad.crew_member.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

public record CrewMemberDto(
        LocalDateTime participantAt,
        SimpleMemberDto member
) {

    public static CrewMemberDto from(CrewMemberDomainDto crewMemberDomainDto) {
        return new CrewMemberDto(
                crewMemberDomainDto.participantAt(),
                SimpleMemberDto.from(crewMemberDomainDto.memberInfo())
        );
    }
}
