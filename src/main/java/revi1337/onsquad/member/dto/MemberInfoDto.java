package revi1337.onsquad.member.dto;

import revi1337.onsquad.member.domain.Member;

public record MemberInfoDto(
        Long id,
        String email,
        String nickname
) {
    public static MemberInfoDto from(Member member) {
        return new MemberInfoDto(
                member.getId(),
                member.getEmail().getValue(),
                member.getNickname().getValue()
        );
    }
}
