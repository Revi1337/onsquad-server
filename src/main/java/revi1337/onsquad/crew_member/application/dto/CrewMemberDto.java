package revi1337.onsquad.crew_member.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

public record CrewMemberDto(
        SimpleMemberInfoDto memberInfo,
        LocalDateTime participantAt
) {
    public static CrewMemberDto from(CrewMemberDomainDto crewMemberDomainDto) {
        return new CrewMemberDto(
                SimpleMemberInfoDto.from(crewMemberDomainDto.memberInfo()),
                crewMemberDomainDto.participantAt()
        );
    }
}
