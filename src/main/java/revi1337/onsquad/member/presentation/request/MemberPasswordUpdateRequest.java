package revi1337.onsquad.member.presentation.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import revi1337.onsquad.common.presentation.validator.StringComparator;
import revi1337.onsquad.common.presentation.validator.StringCompare;
import revi1337.onsquad.member.application.dto.MemberPasswordUpdateDto;

@StringCompare
public record MemberPasswordUpdateRequest(
        @NotEmpty String currentPassword,
        @NotEmpty String newPassword,
        @NotEmpty String newPasswordConfirm
) implements StringComparator {

    public MemberPasswordUpdateDto toDto() {
        return new MemberPasswordUpdateDto(currentPassword, newPassword, newPasswordConfirm);
    }

    @Override
    public Map<String, String> getComparedFields() {
        return Map.of("newPassword", newPassword, "newPasswordConfirm", newPasswordConfirm);
    }
}
