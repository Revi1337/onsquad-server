package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.Password;

public record MemberJoinDto(
        String email,
        String password,
        String passwordConfirm,
        String nickname,
        String address,
        String addressDetail
) {
    public Member toEntity() {
        return Member.builder()
                .email(new Email(email))
                .password(new Password(password))
                .nickname(new Nickname(nickname))
                .address(new Address(address, addressDetail))
                .build();
    }
}
