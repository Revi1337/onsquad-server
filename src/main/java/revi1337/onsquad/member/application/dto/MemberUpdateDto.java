package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Introduce;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;

public record MemberUpdateDto(
        String nickname,
        String introduce,
        String mbti,
        String kakaoLink,
        String address,
        String addressDetail
) {
    public Member toEntity() {
        return Member.builder()
                .nickname(new Nickname(nickname))
                .introduce(new Introduce(introduce))
                .mbti(Mbti.valueOf(mbti))
                .kakaoLink(kakaoLink)
                .address(new Address(address, addressDetail))
                .build();
    }
}
