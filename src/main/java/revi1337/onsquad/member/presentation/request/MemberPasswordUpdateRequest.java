package revi1337.onsquad.member.presentation.request;

import java.util.HashMap;
import java.util.Map;
import revi1337.onsquad.member.application.dto.MemberPasswordUpdateDto;
import revi1337.onsquad.member.presentation.validator.StringComparator;
import revi1337.onsquad.member.presentation.validator.StringValidator;

@StringValidator
public record MemberPasswordUpdateRequest(
        String currentPassword,
        String newPassword,
        String newPasswordConfirm
) implements StringComparator {

    public MemberPasswordUpdateDto toDto() {
        return new MemberPasswordUpdateDto(currentPassword, newPassword, newPasswordConfirm);
    }

    @Override
    public Map<String, String> inspectStrings() {
        return new HashMap<>() {
            {
                put("newPassword", newPassword);
                put("newPasswordConfirm", newPasswordConfirm);
            }
        };
    }
}
