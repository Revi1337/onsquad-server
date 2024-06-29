package revi1337.onsquad.member.dto;

import revi1337.onsquad.member.domain.Member;

public record SimpleMemberInfoDto(
        Long id,
        String email,
        String nickname
) {
    public static SimpleMemberInfoDto from(Member member) {
        return new SimpleMemberInfoDto(
                member.getId(),
                member.getEmail().getValue(),
                member.getNickname().getValue()
        );
    }
}
