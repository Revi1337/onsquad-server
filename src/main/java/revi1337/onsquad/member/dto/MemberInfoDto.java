package revi1337.onsquad.member.dto;


import revi1337.onsquad.member.domain.Member;

import java.time.LocalDate;

public record MemberInfoDto(
        Long id,
        String nickname,
        String email,
        String gender,
        LocalDate birth,
        String userType,
        String address,
        String addressDetail
) {
    // TODO 회원가입에 Gender, birth 정보가 추가되면 리팩토링이 필요.
    public MemberInfoDto(Long id, String nickname, String email, String userType, String address, String addressDetail) {
        this(id, nickname, email, null, null, userType, address, addressDetail);
    }

    public static MemberInfoDto from(Member member) {
        return new MemberInfoDto(
                member.getId(),
                member.getNickname().getValue(),
                member.getEmail().getValue(),
                member.getUserType().getText(),
                member.getAddress().getValue(),
                member.getAddress().getDetail()
        );
    }
}
