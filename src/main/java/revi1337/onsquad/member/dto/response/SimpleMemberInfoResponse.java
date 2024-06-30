package revi1337.onsquad.member.dto.response;

import revi1337.onsquad.member.dto.SimpleMemberInfoDto;

public record SimpleMemberInfoResponse(
        Long id,
        String email,
        String nickname
) {
    public static SimpleMemberInfoResponse from(SimpleMemberInfoDto simpleMemberInfoDto) {
        return new SimpleMemberInfoResponse(
                simpleMemberInfoDto.id(),
                simpleMemberInfoDto.email(),
                simpleMemberInfoDto.nickname()
        );
    }
}
