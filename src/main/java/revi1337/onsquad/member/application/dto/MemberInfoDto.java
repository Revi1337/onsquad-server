package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.Member;

public record MemberInfoDto(
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
    public static MemberInfoDto from(Member member) {
        return new MemberInfoDto(
                member.getId(),
                member.getEmail().getValue(),
                member.getNickname().getValue(),
                member.getIntroduce().getValue(),
                member.getMbti() != null ? member.getMbti().name() : "",
                member.getKakaoLink(),
                member.getProfileImage(),
                member.getUserType().getText(),
                member.getAddress().getValue(),
                member.getAddress().getDetail()
        );
    }
}
