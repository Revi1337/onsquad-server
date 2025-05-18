package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.Member;

public record MemberCreateDto(
        String email,
        String password,
        String passwordConfirm,
        String nickname,
        String address,
        String addressDetail
) {
    public Member toEntity() {
        return Member.general(email, password, nickname, address, addressDetail);
    }
}
