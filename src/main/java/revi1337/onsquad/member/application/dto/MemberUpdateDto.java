package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.model.ProfileSpec;

public record MemberUpdateDto(
        String nickname,
        String introduce,
        String mbti,
        String kakaoLink,
        String address,
        String addressDetail
) {

    public ProfileSpec toSpec() {
        return new ProfileSpec(nickname, introduce, mbti, address, addressDetail, kakaoLink);
    }
}
