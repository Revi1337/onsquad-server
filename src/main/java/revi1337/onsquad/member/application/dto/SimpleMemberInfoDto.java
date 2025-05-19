package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

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

    public static SimpleMemberInfoDto from(SimpleMemberDomainDto simpleMemberDomainDto) {
        return new SimpleMemberInfoDto(
                simpleMemberDomainDto.id(),
                simpleMemberDomainDto.email() != null ? simpleMemberDomainDto.email().getValue() : null,
                simpleMemberDomainDto.nickname().getValue(),
                simpleMemberDomainDto.mbti() != null ? simpleMemberDomainDto.mbti().name() : ""
        );
    }
}
