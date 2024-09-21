package revi1337.onsquad.member.dto.response;

import revi1337.onsquad.member.dto.MemberInfoDto;

import java.time.LocalDate;

public record MemberInfoResponse(
        Long id,
        String nickname,
        String email,
        String gender,
        LocalDate birth,
        String userType,
        String address,
        String addressDetail
) {
    public MemberInfoResponse(Long id, String nickname, String email, String userType, String address, String addressDetail) {
        this(id, nickname, email, null, null, userType, address, addressDetail);
    }

    public static MemberInfoResponse from(MemberInfoDto memberInfoDto) {
        return new MemberInfoResponse(
                memberInfoDto.id(),
                memberInfoDto.nickname(),
                memberInfoDto.email(),
                memberInfoDto.userType(),
                memberInfoDto.address(),
                memberInfoDto.addressDetail()
        );
    }
}
