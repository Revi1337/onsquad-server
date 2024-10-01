package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record SimpleMemberInfoDto(
        Long id,
        String email,
        String nickname,
        String mbti
) {
    public static SimpleMemberInfoDto from(Member member) {
        return new SimpleMemberInfoDto(
                member.getId(),
                member.getEmail().getValue(),
                member.getNickname().getValue(),
                member.getMbti() != null ? member.getMbti().name() : ""
        );
    }

    public static SimpleMemberInfoDto from(SimpleMemberInfoDomainDto simpleMemberInfoDomainDto) {
        return new SimpleMemberInfoDto(
                simpleMemberInfoDomainDto.id(),
                simpleMemberInfoDomainDto.email() != null ? simpleMemberInfoDomainDto.email().getValue() : null,
                simpleMemberInfoDomainDto.nickname().getValue(),
                simpleMemberInfoDomainDto.mbti() != null ? simpleMemberInfoDomainDto.mbti().name() : ""
        );
    }
}
