package revi1337.onsquad.member.dto.response;

import revi1337.onsquad.member.dto.MemberInfoDto;

public record MemberInfoResponse(
        Long id,
        String email,
        String nickname
) {
    public static MemberInfoResponse from(MemberInfoDto memberInfoDto) {
        return new MemberInfoResponse(
                memberInfoDto.id(),
                memberInfoDto.email(),
                memberInfoDto.nickname()
        );
    }
}
