package revi1337.onsquad.member.presentation.dto.response;

import revi1337.onsquad.member.application.dto.MemberInfoDto;

public record MemberInfoResponse(
        Long id,
        String email,
        String nickname,
        String userType,
        String address,
        String addressDetail,
        String mbti
) {
    public static MemberInfoResponse from(MemberInfoDto memberInfoDto) {
        return new MemberInfoResponse(
                memberInfoDto.id(),
                memberInfoDto.email(),
                memberInfoDto.nickname(),
                memberInfoDto.userType(),
                memberInfoDto.address(),
                memberInfoDto.addressDetail(),
                memberInfoDto.mbti()
        );
    }
}
