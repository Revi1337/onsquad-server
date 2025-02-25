package revi1337.onsquad.squad_member.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;

public record SquadMemberDto(
        SimpleMemberInfoDto memberInfo,
        LocalDateTime participantAt,
        Boolean matchCurrentUser
) {
    public static SquadMemberDto from(SquadMemberDomainDto squadMemberDomainDto) {
        return new SquadMemberDto(
                SimpleMemberInfoDto.from(squadMemberDomainDto.memberInfo()),
                squadMemberDomainDto.participantAt(),
                null
        );
    }

    public static SquadMemberDto from(Long memberId, SquadMemberDomainDto squadMemberDomainDto) {
        return new SquadMemberDto(
                SimpleMemberInfoDto.from(squadMemberDomainDto.memberInfo()),
                squadMemberDomainDto.participantAt(),
                squadMemberDomainDto.memberInfo().id().equals(memberId)
        );
    }
}
