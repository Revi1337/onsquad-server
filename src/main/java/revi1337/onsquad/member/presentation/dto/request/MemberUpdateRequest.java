package revi1337.onsquad.member.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.member.application.dto.MemberUpdateDto;
import revi1337.onsquad.member.presentation.validator.MbtiValidator;

public record MemberUpdateRequest(
        @NotEmpty String nickname,
        @NotEmpty String introduce,
        @MbtiValidator String mbti,
        @NotEmpty String kakaoLink,
        @NotEmpty String address,
        @NotEmpty String addressDetail
) {
    public MemberUpdateDto toDto() {
        return new MemberUpdateDto(nickname, introduce, mbti.toUpperCase(), kakaoLink, address, addressDetail);
    }
}
