package revi1337.onsquad.member.presentation.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import revi1337.onsquad.member.application.dto.MemberCreateDto;
import revi1337.onsquad.member.presentation.validator.StringComparator;
import revi1337.onsquad.member.presentation.validator.StringValidator;

@StringValidator
public record MemberCreateRequest(
        @NotEmpty String email,
        @NotEmpty String password,
        @NotEmpty String passwordConfirm,
        @NotEmpty String nickname,
        @NotEmpty String address,
        @NotEmpty String addressDetail
) implements StringComparator {

    public MemberCreateDto toDto() {
        return new MemberCreateDto(email, password, passwordConfirm, nickname, address, addressDetail);
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
