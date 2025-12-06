package revi1337.onsquad.squad_member.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;

public record SquadMemberDto(
        Boolean matchCurrentMember,
        LocalDateTime participantAt,
        SimpleMemberDto member
) {

    public static SquadMemberDto from(SquadMemberDomainDto squadMemberDomainDto) {
        return new SquadMemberDto(
                null,
                squadMemberDomainDto.participantAt(),
                SimpleMemberDto.from(squadMemberDomainDto.member())
        );
    }

    public static SquadMemberDto from(Long memberId, SquadMemberDomainDto squadMemberDomainDto) {
        return new SquadMemberDto(
                squadMemberDomainDto.member().id().equals(memberId),
                squadMemberDomainDto.participantAt(),
                SimpleMemberDto.from(squadMemberDomainDto.member())
        );
    }
}
