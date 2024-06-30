package revi1337.onsquad.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
        @NotEmpty String address,
        String addressDetail
) implements StringComparator {

    public MemberDto toDto() {
        return MemberDto.create()
                .email(email)
                .password(password)
                .nickname(nickname)
                .address(address)
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
