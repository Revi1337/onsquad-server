package revi1337.onsquad.member.presentation.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import revi1337.onsquad.common.presentation.validator.StringComparator;
import revi1337.onsquad.common.presentation.validator.StringCompare;
import revi1337.onsquad.member.application.dto.MemberCreateDto;

@StringCompare
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
    public Map<String, String> getComparedFields() {
        return Map.of("password", password, "passwordConfirm", passwordConfirm);
    }
}
