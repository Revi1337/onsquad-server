package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.Member.MemberBase;

public record MemberUpdateDto(
        String nickname,
        String introduce,
        String mbti,
        String kakaoLink,
        String address,
        String addressDetail
) {
    public MemberBase toMemberBase() {
        return new MemberBase(nickname, introduce, mbti, address, addressDetail, kakaoLink);
    }
}
