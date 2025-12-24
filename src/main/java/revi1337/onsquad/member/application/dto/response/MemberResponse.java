package revi1337.onsquad.member.application.dto.response;

import revi1337.onsquad.member.domain.entity.Member;

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

    public static MemberResponse from(Member member) {
        return new MemberResponse(
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
