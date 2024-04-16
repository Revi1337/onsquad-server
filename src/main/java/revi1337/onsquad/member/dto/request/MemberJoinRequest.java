package revi1337.onsquad.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.dto.MemberDto;

public record MemberJoinRequest(
        @NotEmpty String email,
        @NotEmpty String password,
        @NotEmpty String passwordConfirm,
        @NotEmpty String nickname,
        @NotEmpty String address
) {
    public MemberDto toDto() {
        return MemberDto.builder()
                .email(new revi1337.onsquad.member.domain.vo.Email(email))
                .nickname(new Nickname(nickname))
                .address(new Address(address))
                .build();
    }
}
