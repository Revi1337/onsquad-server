package revi1337.onsquad.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.member.dto.MemberDto;
import revi1337.onsquad.member.presentation.validator.StringComparator;
import revi1337.onsquad.member.presentation.validator.StringValidator;

import java.util.HashMap;
import java.util.Map;

@StringValidator
public record MemberJoinRequest(
        @NotEmpty String email,
        @NotEmpty String password,
        @NotEmpty String passwordConfirm,
        @NotEmpty String nickname,
        @NotEmpty String address
) implements StringComparator {

    public MemberDto toDto() {
        return MemberDto.builder()
                .email(new revi1337.onsquad.member.domain.vo.Email(email))
                .password(new Password(password))
                .nickname(new Nickname(nickname))
                .address(new Address(address))
                .build();
    }

    @Override
    public Map<String, String> inspectStrings() {
        return new HashMap<>() {
            {
                put("password", password);
                put("passwordConfirm", passwordConfirm);
            }
        };
    }
}
