package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.Member;

public record MemberInfoDto(
        Long id,
        String email,
        String nickname,
        String userType,
        String address,
        String addressDetail,
        String mbti
) {
    public static MemberInfoDto from(Member member) {
        return new MemberInfoDto(
                member.getId(),
                member.getEmail().getValue(),
                member.getNickname().getValue(),
                member.getUserType().getText(),
                member.getAddress().getValue(),
                member.getAddress().getDetail(),
                member.getMbti() != null ? member.getMbti().name() : ""
        );
    }
}
