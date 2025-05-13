package revi1337.onsquad.squad_member.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;

public record SquadMemberDto(
        Boolean matchCurrentMember,
        LocalDateTime participantAt,
        SimpleMemberInfoDto member
) {
    public static SquadMemberDto from(SquadMemberDomainDto squadMemberDomainDto) {
        return new SquadMemberDto(
                null,
                squadMemberDomainDto.participantAt(),
                SimpleMemberInfoDto.from(squadMemberDomainDto.member())
        );
    }

    public static SquadMemberDto from(Long memberId, SquadMemberDomainDto squadMemberDomainDto) {
        return new SquadMemberDto(
                squadMemberDomainDto.member().id().equals(memberId),
                squadMemberDomainDto.participantAt(),
                SimpleMemberInfoDto.from(squadMemberDomainDto.member())
        );
    }
}
