package revi1337.onsquad.member.presentation.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.member.application.dto.MemberUpdateDto;

public record MemberUpdateRequest(
        @NotEmpty String nickname,
        @NotEmpty String introduce,
        @NotEmpty String mbti,
        @NotEmpty String kakaoLink,
        @NotEmpty String address,
        @NotEmpty String addressDetail
) {

    public MemberUpdateDto toDto() {
        return new MemberUpdateDto(nickname, introduce, mbti, kakaoLink, address, addressDetail);
    }
}
