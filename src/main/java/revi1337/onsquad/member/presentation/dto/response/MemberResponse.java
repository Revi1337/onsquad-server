package revi1337.onsquad.member.presentation.dto.response;

import revi1337.onsquad.member.application.dto.MemberInfoDto;

public record MemberResponse(
        Long id,
        String email,
        String nickname,
        String introduce,
        String mbti,
        String kakaoLink,
        String profileImage,
        String userType,
        String address,
        String addressDetail
) {
    public static MemberResponse from(MemberInfoDto memberInfoDto) {
        return new MemberResponse(
                memberInfoDto.id(),
                memberInfoDto.email(),
                memberInfoDto.nickname(),
                memberInfoDto.introduce(),
                memberInfoDto.mbti(),
                memberInfoDto.kakaoLink(),
                memberInfoDto.profileImage(),
                memberInfoDto.userType(),
                memberInfoDto.address(),
                memberInfoDto.addressDetail()
        );
    }
}
