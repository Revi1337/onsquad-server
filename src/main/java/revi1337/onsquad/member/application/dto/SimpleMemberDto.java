package revi1337.onsquad.member.application.dto;

import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SimpleMemberDto(
        Long id,
        String email,
        String nickname,
        String introduce,
        String mbti
) {
    public static SimpleMemberDto from(Member member) {
        return new SimpleMemberDto(
                member.getId(),
                member.getEmail().getValue(),
                member.getNickname().getValue(),
                member.getIntroduce() != null ? member.getIntroduce().getValue() : null,
                member.getMbti() != null ? member.getMbti().name() : ""
        );
    }

    public static SimpleMemberDto from(SimpleMemberDomainDto simpleMemberDomainDto) {
        return new SimpleMemberDto(
                simpleMemberDomainDto.id(),
                simpleMemberDomainDto.email() != null ? simpleMemberDomainDto.email().getValue() : null,
                simpleMemberDomainDto.nickname().getValue(),
                simpleMemberDomainDto.introduce() != null ? simpleMemberDomainDto.introduce().getValue() : null,
                simpleMemberDomainDto.mbti() != null ? simpleMemberDomainDto.mbti().name() : ""
        );
    }
}
