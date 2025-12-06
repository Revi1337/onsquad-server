package revi1337.onsquad.member.application.dto;

public record MemberPasswordUpdateDto(
        String currentPassword,
        String newPassword,
        String newPasswordConfirm
) {

}
