package revi1337.onsquad.member.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SimpleMemberResponse(
        Long id,
        String email,
        String nickname,
        String introduce,
        String mbti
) {
    public static SimpleMemberResponse from(SimpleMemberDto simpleMemberDto) {
        return new SimpleMemberResponse(
                simpleMemberDto.id(),
                simpleMemberDto.email(),
                simpleMemberDto.nickname(),
                simpleMemberDto.introduce(),
                simpleMemberDto.mbti()
        );
    }
}
