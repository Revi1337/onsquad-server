package revi1337.onsquad.member.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.member.application.dto.MemberJoinDto;
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
        @NotEmpty String addressDetail
) implements StringComparator {

    public MemberJoinDto toDto() {
        return new MemberJoinDto(email, password, passwordConfirm, nickname, address, addressDetail);
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
